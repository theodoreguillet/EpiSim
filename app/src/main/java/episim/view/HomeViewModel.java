package episim.view;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;

public class HomeViewModel implements ViewModel {
    @InjectScope
    private ConfigurationScope configScope;

    public void initialize() {
        System.out.println("From HomeViewModel: " + configScope.getSimulationConfig().uuid);
    }

    public void startSimulation() {
        configScope.publish(ConfigurationScope.SIMULATION);
    }
}
