package episim.core;

import java.util.Timer;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * La simulation de l'épidémie
 */
public class Simulation {
    private final SimulationConfig config;
    private double speed = 1; // Vitesse de la simulation en jours par seconde

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);;
    private final AtomicReference<SimulationState> state = new AtomicReference<>(null);
    private boolean started = false;
    private boolean paused = false;

    public Simulation(SimulationConfig config) {
        this.config = (SimulationConfig)config.clone();
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
            stopExecutor();
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
        this.speed = speed;
        executor.shutdown();
        startExecutor();
    }

    public synchronized double getSpeed() {
        return speed;
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
        int period = Math.max((int)(1000.0 / speed), 10);
        executor.scheduleAtFixedRate(this::updateState, 0, period, TimeUnit.MILLISECONDS);
    }

    private void stopExecutor() throws RuntimeException {
        if(!started) {
            return;
        }
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
        started = false;
    }

    /**
     * Génère l'état initial de la simulation
     */
    private void initState() {
        //
    }

    /**
     * Génère l'état suivant de la simulation
     */
    private void updateState() {
        //
    }
}
