package episim.view;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ScopeProvider;
import de.saxsys.mvvmfx.ViewModel;
import episim.App;
import episim.core.SimulationConfig;
import episim.view.popup.AboutViewPopup;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.io.File;

/**
 * Modèle de la vue {@link MainView principale}
 */
@ScopeProvider(scopes = {MainScope.class})
public class MainViewModel implements ViewModel {
    public static final String ACTION_SET_CONTENT = "ACTION_SET_CONTENT";
    public static final String HOME = "HOME";
    public static final String SIMULATION = "SIMULATION";

    @InjectScope
    private MainScope mainScope;

    private final BooleanProperty simulationStarted = new SimpleBooleanProperty();
    private final BooleanProperty simulationPaused = new SimpleBooleanProperty();

    public void initialize() {
        mainScope.subscribe(MainScope.MAIN_CONFIG, (k, v) -> {
            simulationStarted.set(false);
            publish(ACTION_SET_CONTENT, HOME);
        });
        mainScope.subscribe(MainScope.SIMULATION, (k, v) -> {
            simulationStarted.set(true);
            publish(ACTION_SET_CONTENT, SIMULATION);
        });
        simulationPaused.bindBidirectional(mainScope.simulationPaused());

        mainScope.publish(MainScope.MAIN_CONFIG);
    }

    public BooleanProperty simulationStarted() {
        return simulationStarted;
    }
    public BooleanProperty simulationPaused() {
        return simulationPaused;
    }

    public void loadConfig(File file) {
        mainScope.simulationConfig().set(SimulationConfig.load(file));
    }
    public void saveConfig(File file) {
        mainScope.getSimulationConfig().save(file);
    }
    public void exit() {
        App.getPrimaryStage().close();
    }
    public void startSimulation() {
        mainScope.publish(MainScope.SIMULATION);
    }
    public void stopSimulation() {
        mainScope.publish(MainScope.STOP_SIMULATION);
    }
    public void pauseSimulation() {
        simulationPaused.set(true);
    }
    public void resumeSimulation() {
        simulationPaused.set(false);
    }
    public void exportSimulation() {
        mainScope.publish(MainScope.EXPORT_SIMULATION);
    }
    public void showAboutUs() {
        AboutViewPopup.open();
    }
}
