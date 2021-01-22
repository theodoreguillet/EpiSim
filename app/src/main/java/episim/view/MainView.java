package episim.view;

import de.saxsys.mvvmfx.*;
import episim.App;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Vue principale, contient les autres vues
 */
public class MainView implements FxmlView<MainViewModel>, Initializable {
    @InjectViewModel
    private MainViewModel viewModel;

    @InjectContext
    private Context mainContext;

    @FXML
    private BorderPane borderPane;

    @FXML
    private MenuItem loadConfigItem;
    @FXML
    private MenuItem simulationStartItem;
    @FXML
    private MenuItem simulationStopItem;
    @FXML
    private MenuItem simulationPauseItem;
    @FXML
    private MenuItem simulationResumeItem;
    @FXML
    private MenuItem simulationExportItem;

    @FXML
    private void loadConfig() {
        var fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Fichier de configuration Episim", "*.episim")
        );
        fileChooser.setTitle("Charger la configuration");
        var file = fileChooser.showOpenDialog(App.getPrimaryStage());
        viewModel.loadConfig(file);
    }
    @FXML
    private void saveConfig() {
        var fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Fichier de configuration Episim", "*.episim")
        );
        fileChooser.setTitle("Enregistrer la configuration");
        var file = fileChooser.showSaveDialog(App.getPrimaryStage());
        viewModel.saveConfig(file);
    }
    @FXML
    private void exit() {
        viewModel.exit();
    }
    @FXML
    private void startSimulation() {
        viewModel.startSimulation();
    }
    @FXML
    private void stopSimulation() {
        viewModel.stopSimulation();
    }
    @FXML
    private void pauseSimulation() {
        viewModel.pauseSimulation();
    }
    @FXML
    private void resumeSimulation() {
        viewModel.resumeSimulation();
    }
    @FXML
    private void exportSimulation() {
        viewModel.exportSimulation();
    }
    @FXML
    private void aboutUs() {
        viewModel.showAboutUs();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        viewModel.subscribe(MainViewModel.ACTION_SET_CONTENT, (k, v) -> {
            if(v.length > 0 && v[0] instanceof String) {
                String type = (String)v[0];
                setContent(type);
            }
        });

        loadConfigItem.visibleProperty().bind(viewModel.simulationStarted().not());

        simulationStopItem.visibleProperty().bind(viewModel.simulationStarted());
        simulationStartItem.visibleProperty().bind(viewModel.simulationStarted().not());

        simulationResumeItem.disableProperty().bind(
                viewModel.simulationStarted().not().or(viewModel.simulationPaused().not())
        );
        simulationPauseItem.disableProperty().bind(
                viewModel.simulationStarted().not().or(viewModel.simulationPaused())
        );
        simulationExportItem.disableProperty().bind(viewModel.simulationStarted().not());

        setContent(MainViewModel.HOME);
    }

    private void setContent(String type) {
        Class<? extends FxmlView<? extends ViewModel>> contentViewClass = switch (type) {
            case MainViewModel.HOME ->
                    HomeView.class;
            case MainViewModel.SIMULATION ->
                    SimulationView.class;
            default -> null;
        };
        if(contentViewClass != null) {
            Parent content = FluentViewLoader.fxmlView(contentViewClass).context(mainContext).load().getView();
            borderPane.setCenter(content);
        }
    }
}
