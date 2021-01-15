package episim.core;

import java.util.Collections;
import java.util.List;

/**
 * L'état d'une zone de la simulation
 */
public class ZoneState {
    /**
     * Les individus dans la zone
     */
    public final List<IndividualState> individuals;

    public ZoneState(List<IndividualState> individuals) {
        this.individuals = Collections.unmodifiableList(individuals);
    }
}
