package episim.core;

import java.util.Collections;
import java.util.List;

public class SimulationStatsPoint {
    public final double time;
    public final List<Double> populations;

    public SimulationStatsPoint(double time, List<Double> populations) {
        this.time = time;
        this.populations = Collections.unmodifiableList(populations);
    }
}
