package episim.core;

import java.util.Collections;
import java.util.List;

/**
 * L'état de la simulation
 */
public class SimulationState {
    /**
     * Les zones de la simulation
     */
    public final List<ZoneState> zones;
    /**
     * La zone de quarantaine
     */
    public final ZoneState quarantine;
    /**
     * Les individus voyageant
     */
    public final List<TravelerState> travelers;

    public SimulationState(List<ZoneState> zones, ZoneState quarantine, List<TravelerState> travelers) {
        this.zones = Collections.unmodifiableList(zones);
        this.quarantine = quarantine;
        this.travelers = Collections.unmodifiableList(travelers);
    }
}
