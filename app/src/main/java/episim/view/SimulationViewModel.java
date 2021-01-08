package episim.view;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;

public class SimulationViewModel implements ViewModel {
    @InjectScope
    private ConfigurationScope configScope;

    public void initialize() {
        System.out.println("From SimulationViewModel: " + configScope.getSimulationConfig().uuid);
    }
}
