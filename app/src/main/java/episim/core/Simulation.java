package episim.core;

import episim.util.MathUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * La simulation de l'épidémie
 */
public class Simulation {
    public final int ZONE_SIZE = 100;
    public final int QUARANTINE_SIZE = ZONE_SIZE / 4;
    public final int ZONE_MAX_POP = 10000;
    public final double INDIVIDUAL_SPEED = 1;
    public final double INDIVIDUAL_DIRECTION_PROB = 0.05;
    public final double CONTAMINATION_RADIUS = 10;
    public final double MIN_CLOCK_SPEED = 30;

    private final SimulationConfig config;
    private final int nzones;
    private final int susceptibleCompId;
    private final int infectiousCompId;
    private final int recoveredCompId;

    private double simulationSpeed = 1; // Vitesse de la simulation en jours par seconde
    private double clockSpeed = MIN_CLOCK_SPEED; // Nombre de mise à jours de la simulation par seconde

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
        simulationSpeed = speed;
        clockSpeed = Math.max(MIN_CLOCK_SPEED, speed);
        task.cancel(false);
        startExecutor();
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
        state.set(new SimulationState(zones, quarantine, new ArrayList<>()));
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

        var zones = new ArrayList<ZoneState>(nzones);
        for(var lastZone : lastState.zones) {
            zones.add(updateZone(timeScale, ZONE_SIZE, lastZone));
        }
        var quarantine = updateZone(timeScale, QUARANTINE_SIZE, lastState.quarantine);

        ArrayList<TravelerState> travelers = new ArrayList<>();

        state.set(new SimulationState(zones, quarantine, travelers));
    }

    private ZoneState updateZone(double timeScale, int zoneSize, ZoneState lastZone) {
        ArrayList<IndividualState> individuals = new ArrayList<>();

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

            individuals.add(new IndividualState(lastInd.uuid, compId, pos[0], pos[1], pos[2]));
        }

        // On ajoute les naissances
        int i = 0;
        while (i < lastZone.individuals.size() && individuals.size() < ZONE_MAX_POP) {
            if(randBirth(timeScale)) {
                // Un individu est né
                individuals.add(randIndividual(false));
            }
            i++;
        }

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
        if(compId != recoveredCompId && (compId != susceptibleCompId || !metInfectious)) {
            double param = config.getSelectedModel().getCompartments().get(compId).getParam();
            if(rand.nextDouble() < param * timeScale) {
                return compId + 1;
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
}
