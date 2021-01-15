package episim.core;

import java.util.ArrayList;

/**
 * Configuration d'une règle sanitaire de la simulation
 */
public class SimulationRuleConfig implements Cloneable {
    /**
     * Définie si la règle est activée ou non
     */
    private boolean enabled;
    /**
     * La probabilité qu'un individu respecte la règle par jour
     */
    private double respectProb;
    /**
     * Le délais en nombre de jours avant que la règle soit mise en place
     */
    private double delay;

    public SimulationRuleConfig() {
        this.enabled = false;
        this.respectProb = 0;
        this.delay = 0;
    }
    public SimulationRuleConfig(boolean enabled, double respectProb, double delay) {
        this.enabled = enabled;
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
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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
