package episim.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SimulationStats {
    public final List<SimulationStatsPoint> points;
    public final double elapsedTime;
    public final double totalPopulation;

    public SimulationStats(List<SimulationStatsPoint> points, double elapsedTime, double totalPopulation) {
        this.points = Collections.unmodifiableList(points);
        this.elapsedTime = elapsedTime;
        this.totalPopulation = totalPopulation;
    }

    public void save(File file, ModelConfig config) {
        try {
            final String del = ",";
            final String line = "\n";
            final String xlabel = "temps";
            var outputStream = new FileOutputStream(file);
            outputStream.write((
                    xlabel + del +
                    config.getCompartments().stream().map(CompartmentConfig::getName).collect(Collectors.joining(del)) +
                    line
            ).getBytes());
            outputStream.write(
                    points.stream().map(
                            p ->p.time + del +
                                    p.populations.stream().map(Object::toString).collect(Collectors.joining(del))
                    ).collect(Collectors.joining(line)).getBytes()
            );
            outputStream.close();
        } catch (IOException err) {
            // TODO: Report error to user
            err.printStackTrace(System.err);
        }
    }
}
