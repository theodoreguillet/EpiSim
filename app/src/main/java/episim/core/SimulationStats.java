package episim.core;

import java.util.Collections;
import java.util.List;

public class SimulationStats {
    public final List<SimulationStatsPoint> points;
    public final double elapsedTime;

    public SimulationStats(List<SimulationStatsPoint> points, double elapsedTime) {
        this.points = Collections.unmodifiableList(points);
        this.elapsedTime = elapsedTime;
    }

    public void save(String path) {
        // TODO Write csv
    }
}
