package episim.core;

/**
 * La simulation de l'épidémie
 */
public class Simulation {
    private SimulationConfig config;
    private SimulationState state;
    private int speed; // Vitesse de la simulation en jours par seconde
    boolean paused;
    boolean started;
    private Thread thread;

    public Simulation(SimulationConfig config) {
        this.config = (SimulationConfig)config.clone();
        this.speed = 1;
        this.paused = false;
    }

    public void start() {
    }

    public void stop() {

    }


}
