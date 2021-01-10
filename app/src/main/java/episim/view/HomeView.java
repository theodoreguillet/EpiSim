package episim.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import episim.util.SpinnerWrapper;
import episim.util.StringDoubleConverter;
import episim.view.component.ModelComp;
import episim.view.component.ModelSelect;
import episim.view.component.SpinnerSlider;
import javafx.beans.property.DoubleProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Vue de l'accueil
 */
public class HomeView implements FxmlView<HomeViewModel>, Initializable {
    @InjectViewModel
    private HomeViewModel viewModel;

    @FXML
    private HBox modelsList;
    private ArrayList<ModelSelect> modelsListControllers;

    @FXML
    private Button addModelBtn;

    @FXML
    private LineChart<Double, Double> modelChart;

    @FXML
    private HBox modelCompList;
    private ArrayList<ModelSelect> modelCompListControllers;

    @FXML
    private Spinner<Double> modelBirth;
    private SpinnerWrapper modelBirthWrapper;

    @FXML
    private Spinner<Double> modelDeath;
    private SpinnerWrapper modelDeathWrapper;

    @FXML
    private SpinnerSlider popSizeController;

    @FXML
    private SpinnerSlider infecPctController;

    @FXML
    private void onStartSimulationAction() {
        viewModel.startSimulation();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initialize(URL location, ResourceBundle resources) {
        viewModel.modelsProperty().addListener((ListChangeListener.Change<? extends String> c) -> {
            handleModelsChanged((ObservableList<String>) c.getList());
        });
        handleModelsChanged(viewModel.modelsProperty());

        viewModel.selectedModelId().addListener((observable, oldValue, newValue) -> {
            handleSelectedModelIdChanged(newValue.intValue());
        });
        handleSelectedModelIdChanged(viewModel.selectedModelId().get());

        viewModel.modelCompsProperty().addListener((ListChangeListener.Change<? extends HomeViewModel.ModelCompProperty> c) -> {
            handleModelCompsChanged((ObservableList<HomeViewModel.ModelCompProperty>) c.getList());
        });
        handleModelCompsChanged(viewModel.modelCompsProperty());

        modelBirthWrapper = new SpinnerWrapper(modelBirth, 0, 10, 0, 0.01);
        modelBirthWrapper.valueProperty().bindBidirectional(viewModel.modelBirthProperty());
        modelDeathWrapper = new SpinnerWrapper(modelDeath, 0, 10, 0, 0.01);
        modelDeathWrapper.valueProperty().bindBidirectional(viewModel.modelDeathProperty());

        popSizeController.setMin(0);
        popSizeController.setMax(500);
        popSizeController.setStep(1);
        popSizeController.valueProperty().bindBidirectional(viewModel.popSizeProperty());

        infecPctController.setMin(0);
        infecPctController.setMax(100);
        infecPctController.setStep(0.1);
        infecPctController.valueProperty().bindBidirectional(viewModel.infecPctProperty());
    }

    private void handleModelsChanged(ObservableList<String> models) {
        modelsList.getChildren().clear();
        modelsListControllers = new ArrayList<>(models.size());
        for(int i = 0; i < models.size(); i++) {
            try {
                var loader = ModelSelect.load();
                modelsList.getChildren().add(loader.getRoot());
                ModelSelect controller = loader.getController();
                controller.setName(models.get(i));
                modelsListControllers.add(controller);
                final int modelId = i;
                controller.selectedProperty().addListener((observable, oldValue, newValue) -> {
                    if(newValue) {
                        viewModel.selectedModelId().set(modelId);
                    }
                });
            } catch (Exception err) {
                // TODO: Report error to user
                err.printStackTrace(System.err);
            }
        }
    }

    private void handleSelectedModelIdChanged(int modelId) {
        if(modelsListControllers != null) {
            for (int i = 0; i < modelsListControllers.size(); i++) {
                modelsListControllers.get(i).selectedProperty().set(i == modelId);
            }
        }
    }

    private void handleModelCompsChanged(ObservableList<HomeViewModel.ModelCompProperty> comps) {
        modelCompList.getChildren().clear();
        modelCompListControllers = new ArrayList<>(comps.size());
        for(var comp : comps) {
            try {
                var loader = ModelComp.load();
                modelCompList.getChildren().add(loader.getRoot());
                ModelComp controller = loader.getController();
                controller.nameProperty().bind(comp.name());
                controller.valueProperty().bindBidirectional(comp.value());
            } catch (Exception err) {
                // TODO: Report error to user
                err.printStackTrace(System.err);
            }
        }
    }
}
