package episim.core;

/**
 * L'état d'une zone de la simulation
 */
public class ZoneState {
    /**
     * Les individus dans la zone
     */
    public final IndividualState[] individuals;

    public ZoneState(IndividualState[] individuals) {
        this.individuals = individuals;
    }
}
