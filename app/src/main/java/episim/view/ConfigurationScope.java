package episim.view;

import de.saxsys.mvvmfx.Scope;
import episim.core.SimulationConfig;

public class ConfigurationScope implements Scope {
    public static final String SIMULATION = "SIMULATION";
    public static final String MAIN_CONFIG = "MAIN_CONFIG";

    private final SimulationConfig simulationConfig = SimulationConfig.getDefault();

    public SimulationConfig simulationConfig() {
        return simulationConfig;
    }
}
