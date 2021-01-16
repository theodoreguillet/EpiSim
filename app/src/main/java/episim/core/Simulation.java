package episim.core;

import episim.util.MathUtils;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * La simulation de l'épidémie
 */
public class Simulation {
    public static final int ZONE_SIZE = 100;
    public static final int QUARANTINE_SIZE = ZONE_SIZE / 4;
    public static final int MAX_POP = 10000; // population a partir le laquelle les naissances ne se produisent pas
    public static final double INDIVIDUAL_SPEED = 10; // Vitesse en distance par jours
    public static final double TRAVELING_TIME = 2;
    public static final double INDIVIDUAL_DIRECTION_PROB = 0.05;
    public static final double CONTAMINATION_RADIUS = 10;
    public static final double MIN_CLOCK_SPEED = 30;
    public static final int STATS_UPDATE_DELAY = 100; // 100ms

    private final SimulationConfig config;
    private final int nzones;
    private final int susceptibleCompId;
    private final int infectiousCompId;
    private final int recoveredCompId;
    private final int quarantineZoneId = -1;

    private double simulationSpeed = 1; // Vitesse de la simulation en jours par seconde
    private double clockSpeed = MIN_CLOCK_SPEED; // Nombre de mise à jours de la simulation par seconde
    private long lastStatsUpdate = 0;

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private final AtomicReference<SimulationState> state = new AtomicReference<>(null);
    private ScheduledFuture<?> task;
    private boolean started = false;
    private boolean paused = false;

    private final Random rand = new Random();

    public Simulation(SimulationConfig config) {
        this.config = (SimulationConfig)config.clone();
        this.nzones = 1;
        int infectiousCompId = 1;
        for(int i = 0; i < this.config.getSelectedModel().getCompartments().size(); i++) {
            if(this.config.getSelectedModel().getCompartments().get(i).getName().equals(CompartmentConfig.INFECTIOUS)) {
                infectiousCompId = i;
            }
        }
        this.susceptibleCompId = 0;
        this.infectiousCompId = infectiousCompId;
        this.recoveredCompId = this.config.getSelectedModel().getCompartments().size() - 1;
    }

    public synchronized void start() {
        if(!started) {
            initState();
            startExecutor();
            started = true;
        }
    }

    public synchronized void stop() throws RuntimeException {
        if(started) {
            stopExecutor();
            clear();
        }
    }

    public synchronized void pause() throws RuntimeException {
        if(started && !paused) {
            task.cancel(false);
            paused = true;
        }
    }

    public synchronized void resume() {
        if(started && paused) {
            startExecutor();
            paused = false;
        }
    }

    public synchronized boolean isStarted() {
        return started;
    }

    public synchronized boolean isPaused() {
        return paused;
    }

    public synchronized void setSpeed(double speed) {
        if(started) {
            simulationSpeed = speed;
            clockSpeed = Math.max(MIN_CLOCK_SPEED, speed);
            if(!paused) {
                task.cancel(false);
                startExecutor();
            }
        }
    }

    public synchronized double getSpeed() {
        return simulationSpeed;
    }

    /**
     * Retourne l'état de la simulation ou {@code null} si elle n'est pas démarrée.
     * @return {@code SimulationState} ou {@code null}
     */
    public SimulationState getState() {
        return state.get();
    }

    private void clear() {
        started = false;
        paused = false;
        state.set(null);
    }

    private void startExecutor() {
        int period = Math.max((int)(1000.0 / clockSpeed), 10);
        task = executor.scheduleAtFixedRate(this::updateState, 0, period, TimeUnit.MILLISECONDS);
    }

    private void stopExecutor() throws RuntimeException {
        executor.shutdownNow();
        try {
            if(!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                // TODO: throw custom exception
                throw new RuntimeException("Thread does not terminate");
            }
        } catch (InterruptedException err) {
            err.printStackTrace(System.err);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Génère l'état initial de la simulation
     */
    private void initState() {
        int popSize = config.getPopulationSize();
        int popInfected = (int)(config.getInitialInfectious() * (double)popSize);

        var zones = new ArrayList<ZoneState>(nzones);
        var individuals = new ArrayList<ArrayList<IndividualState>>(nzones);

        for(int i = 0; i < nzones; i++) {
            individuals.add(new ArrayList<>());
            zones.add(new ZoneState(individuals.get(i)));
        }
        for(int i = 0; i < popSize; i++) {
            int zoneIdx = rand.nextInt(nzones);
            individuals.get(zoneIdx).add(randIndividual(i < popInfected));
        }
        var quarantine = new ZoneState(new ArrayList<>());
        ArrayList<TravelerState> travelers = new ArrayList<>();
        double time = 0;

        var stats = new SimulationStats(new ArrayList<>(), 0, 0);
        stats = updateStats(stats, zones, quarantine, travelers, time);

        this.state.set(new SimulationState(zones, quarantine, travelers, time, stats));
    }

    private IndividualState randIndividual(boolean infectious) {
        int compId = infectious ? infectiousCompId : susceptibleCompId;
        double posX = rand.nextInt(ZONE_SIZE);
        double posY = rand.nextInt(ZONE_SIZE);
        var direction = (rand.nextDouble() - 2) * Math.PI;
        return new IndividualState(compId, posX, posY, direction);
    }

    /**
     * Génère l'état suivant de la simulation
     */
    private void updateState() {
        SimulationState lastState = state.get();
        double timeScale = simulationSpeed / clockSpeed;
        double time = lastState.time + timeScale;
        boolean allowBirth = lastState.stats.totalPopulation < MAX_POP;

        ArrayList<TravelerState> travelers = new ArrayList<>();
        ArrayList<TravelerState> arrived = new ArrayList<>();
        updateTravelers(timeScale, lastState.travelers, travelers, arrived);

        var zones = new ArrayList<ZoneState>(nzones);
        for(int i = 0; i < lastState.zones.size(); i++) {
            zones.add(updateZone(timeScale, ZONE_SIZE, i, allowBirth, travelers, arrived, lastState.zones.get(i)));
        }
        var quarantine = updateZone(timeScale, QUARANTINE_SIZE, quarantineZoneId, false,
                travelers, arrived, lastState.quarantine);

        var stats = lastState.stats;
        if(System.currentTimeMillis() - lastStatsUpdate > STATS_UPDATE_DELAY) {
            stats = updateStats(stats, zones, quarantine, travelers, time);
            lastStatsUpdate = System.currentTimeMillis();
        }

        this.state.set(new SimulationState(zones, quarantine, travelers, time, stats));
    }

    private ZoneState updateZone(double timeScale, int zoneSize, int zoneId, boolean allowBirth,
                                 ArrayList<TravelerState> travelers, ArrayList<TravelerState> arrived,
                                 ZoneState lastZone) {
        ArrayList<IndividualState> individuals =
                new ArrayList<>((allowBirth ? 2 * lastZone.individuals.size() : lastZone.individuals.size()) + arrived.size());

        // On met a jour chaque individu
        for(var lastInd : lastZone.individuals) {
            if(randDeath(timeScale)) {
                // L'individu est mort
                continue;
            }

            int compId = updateComp(
                    timeScale,
                    lastInd.compartmentId,
                    hasMetInfectious(lastInd, lastZone.individuals)
            );

            double direction = lastInd.direction;
            if(rand.nextDouble() < INDIVIDUAL_DIRECTION_PROB * timeScale) {
                direction = MathUtils.angleMod(
                        lastInd.direction + (rand.nextDouble() - 2) * Math.PI/2
                );
            }

            var pos = updatePos(timeScale, zoneSize, lastInd.posX, lastInd.posY, lastInd.direction);

            boolean travel = false;
            double dstX = 0, dstY = 0;
            int dstZoneId = 0;

            if(config.getQuarantine().isEnabled() && zoneId != quarantineZoneId && compId == infectiousCompId) {
                if(rand.nextDouble() < config.getQuarantine().getRespectProb() * timeScale) {
                    travel = true;
                    dstZoneId = quarantineZoneId;
                    dstX = (double)QUARANTINE_SIZE / 2.0;
                    dstY = (double)QUARANTINE_SIZE / 2.0;
                }
            }

            if(travel) {
                travelers.add(new TravelerState(lastInd.uuid, compId, zoneId, pos[0], pos[1], pos[2],
                        dstZoneId, dstX, dstY, 0));
            } else {
                individuals.add(new IndividualState(lastInd.uuid, compId, pos[0], pos[1], pos[2]));
            }
        }

        // On ajoute les naissances
        if(allowBirth) {
            for(int i = 0; i < lastZone.individuals.size(); i++) {
                if(randBirth(timeScale)) {
                    // Un individu est né
                    individuals.add(randIndividual(false));
                }
            }
        }

        // On ajoute les voyageurs arrivés
        arrived.forEach(t -> {
            if(t.dstZoneId == zoneId) {
                individuals.add(t);
            }
        });

        return new ZoneState(individuals);
    }

    private boolean hasMetInfectious(IndividualState ind, List<IndividualState> others) {
        for(var other : others) {
            if(other.uuid != ind.uuid &&
                    other.compartmentId == infectiousCompId &&
                    MathUtils.dst2(other.posX, other.posY, ind.posX, ind.posY) <
                            (CONTAMINATION_RADIUS * CONTAMINATION_RADIUS)/4
            ) {
                return true;
            }
        }
        return false;
    }

    private boolean randDeath(double timeScale) {
        double param = config.getSelectedModel().getDeath();
        return (rand.nextDouble() < param * timeScale);
    }

    private boolean randBirth(double timeScale) {
        double param = config.getSelectedModel().getBirth();
        return (rand.nextDouble() < param * timeScale);
    }

    private int updateComp(double timeScale, int compId, boolean metInfectious) {
        if(compId != recoveredCompId) {
            double param = config.getSelectedModel().getCompartments().get(compId).getParam();
            if(compId == susceptibleCompId) {
                if(metInfectious &&
                        rand.nextDouble() < param * timeScale * INDIVIDUAL_SPEED
                ) {
                    return compId + 1;
                }
            } else {
                if(rand.nextDouble() < param * timeScale) {
                    return compId + 1;
                }
            }
        }
        return compId;
    }

    private double[] updatePos(double timeScale, int zoneSize, double posX, double posY, double direction) {
        if(rand.nextDouble() < INDIVIDUAL_DIRECTION_PROB * timeScale) {
            direction = MathUtils.angleMod(
                    direction + (rand.nextDouble() - 2) * Math.PI/2
            );
        }
        return move(zoneSize, posX, posY, direction, INDIVIDUAL_SPEED * timeScale);
    }

    private double[] move(int zoneSize, double posX, double posY, double direction, double speed) {
        var pos = new double[]{
                posX + Math.cos(direction) * speed,
                posY + Math.sin(direction) * speed,
                direction
        };
        if(pos[0] < 0 || pos[0] >= zoneSize) {
            return move(zoneSize, posX, posY, Math.PI - direction, speed);
        }
        if(pos[1] < 0 || pos[1] >= zoneSize) {
            return move(zoneSize, posX, posY, -direction, speed);
        }
        return pos;
    }

    private void updateTravelers(double timeScale, List<TravelerState> lastTravelers,
                                                     ArrayList<TravelerState> travelers, ArrayList<TravelerState> arrived) {
        for(var trav : lastTravelers) {
            double ratio = Math.min(trav.ratio + timeScale / TRAVELING_TIME, 1);
            if(ratio < 1) {
                travelers.add(new TravelerState(trav.uuid, trav.compartmentId, trav.zoneId, trav.posX,
                        trav.posY, trav.direction, trav.dstZoneId, trav.dstX, trav.dstY, ratio));
            } else {
                arrived.add(new TravelerState(trav.uuid, trav.compartmentId, trav.zoneId, trav.dstX,
                        trav.dstY, trav.direction, trav.dstZoneId, trav.dstX, trav.dstY, 1));
            }
        }
    }

    private SimulationStats updateStats(SimulationStats stats, List<ZoneState> zones, ZoneState quarantine,
                                        List<TravelerState> travelers, double time
    ) {

        int ncomp = config.getSelectedModel().getCompartments().size();
        ArrayList<Double> populations = new ArrayList<>(ncomp);

        double totalPopulation = 0;
        for(int compId = 0; compId < ncomp; compId++) {
            double value = 0;
            for(var zone : zones) {
                value += countInCompartment(zone.individuals, compId);
            }
            value += countInCompartment(quarantine.individuals, compId);
            value += countInCompartment(travelers, compId);

            populations.add(value);
            totalPopulation += value;
        }

        ArrayList<SimulationStatsPoint> points = new ArrayList<>(stats.points.size() + 1);
        points.addAll(stats.points);
        points.add(new SimulationStatsPoint(time, populations, totalPopulation));

        return new SimulationStats(points, time, totalPopulation);
    }

    private int countInCompartment(List<? extends IndividualState> individuals, int compId) {
        int count = 0;
        for(var ind : individuals) {
            if(ind.compartmentId == compId) {
                count++;
            }
        }
        return count;
    }
}
