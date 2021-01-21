package episim.view;

import de.saxsys.mvvmfx.Scope;
import episim.core.SimulationConfig;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

public class MainScope implements Scope {
    public static final String SIMULATION = "SIMULATION";
    public static final String MAIN_CONFIG = "MAIN_CONFIG";
    public static final String STOP_SIMULATION = "STOP_SIMULATION";
    public static final String EXPORT_SIMULATION = "EXPORT_SIMULATION";

    private final ObjectProperty<SimulationConfig> simulationConfig =
            new SimpleObjectProperty<>(SimulationConfig.getDefault());
    private final BooleanProperty simulationPaused = new SimpleBooleanProperty();

    public SimulationConfig getSimulationConfig() {
        return simulationConfig.get();
    }
    public ObjectProperty<SimulationConfig> simulationConfig() {
        return simulationConfig;
    }
    public BooleanProperty simulationPaused() {
        return simulationPaused;
    }
}
