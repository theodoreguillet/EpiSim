package episim.core;

import java.util.Collections;
import java.util.List;

public class SimulationStats {
    public final List<SimulationStatsPoint> points;
    public final double elapsedTime;
    public final double totalPopulation;

    public SimulationStats(List<SimulationStatsPoint> points, double elapsedTime, double totalPopulation) {
        this.points = Collections.unmodifiableList(points);
        this.elapsedTime = elapsedTime;
        this.totalPopulation = totalPopulation;
    }

    public void save(String path) {
        // TODO Write csv
    }
}
