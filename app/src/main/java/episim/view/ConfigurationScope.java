package episim.view;

import com.google.inject.Inject;
import de.saxsys.mvvmfx.Scope;
import episim.core.SimulationConfig;

public class ConfigurationScope implements Scope {
    public static final String SIMULATION = "SIMULATION";
    public static final String MAIN_CONFIG = "MAIN_CONFIG";

    @Inject
    private SimulationConfig simulationConfig;

    public SimulationConfig getSimulationConfig() {
        return simulationConfig;
    }
}
