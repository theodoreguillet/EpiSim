package episim.core;

/**
 * L'état de la simulation
 */
public class SimulationState {
    /**
     * Les zones de la simulation
     */
    public final ZoneState[] zones;
    /**
     * La zone de quarantaine
     */
    public final ZoneState quarantine;
    /**
     * Les individus voyageant
     */
    public final TravelerState[] travelers;

    public SimulationState(ZoneState[] zones, ZoneState quarantine, TravelerState[] travelers) {
        this.zones = zones;
        this.quarantine = quarantine;
        this.travelers = travelers;
    }
}
