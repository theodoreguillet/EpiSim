package episim.view;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ScopeProvider;
import de.saxsys.mvvmfx.ViewModel;

@ScopeProvider(scopes = {ConfigurationScope.class})
public class MainViewModel implements ViewModel {
    public static final String ACTION_SET_CONTENT = "ACTION_SET_CONTENT";
    public static final String HOME = "HOME";
    public static final String SIMULATION = "SIMULATION";

    @InjectScope
    private ConfigurationScope configScope;

    public void initialize() {
        configScope.subscribe(ConfigurationScope.MAIN_CONFIG, (k, v) -> {
            publish(ACTION_SET_CONTENT, HOME);
        });
        configScope.subscribe(ConfigurationScope.SIMULATION, (k, v) -> {
            publish(ACTION_SET_CONTENT, SIMULATION);
        });
    }
}
