package episim.core;

import java.util.ArrayList;

/**
 * Configuration d'une règle sanitaire de la simulation
 */
public class SimulationRuleConfig implements Cloneable {
    /**
     * La probabilité qu'un individu respecte la règle par jour
     */
    private double respectProb;
    /**
     * Le délais en nombre de jours avant que la règle soit mise en place
     */
    private double delay;

    public SimulationRuleConfig() {
        this.respectProb = 0;
        this.delay = 0;
    }
    public SimulationRuleConfig(double respectProb, double delay) {
        this.respectProb = respectProb;
        this.delay = delay;
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch(CloneNotSupportedException err) {
            // Ne doit pas arriver puisqu'on implémente Cloneable
            err.printStackTrace(System.err);
        }
        return null;
    }

    public boolean isEnabled() {
        return respectProb != 0;
    }

    public double getRespectProb() {
        return respectProb;
    }

    public void setRespectProb(double respectProb) {
        this.respectProb = respectProb;
    }

    public double getDelay() {
        return delay;
    }

    public void setDelay(double delay) {
        this.delay = delay;
    }
}
