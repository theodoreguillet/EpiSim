package episim.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import episim.util.SpinnerWrapper;
import episim.view.component.*;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import jfxtras.styles.jmetro.MDL2IconFont;
import org.checkerframework.checker.units.qual.A;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Vue de l'accueil
 */
public class HomeView implements FxmlView<HomeViewModel>, Initializable {
    @InjectViewModel
    private HomeViewModel viewModel;

    @FXML
    private ModelChart modelChartController;

    @FXML
    private Slider modelChartScale;

    @FXML
    private CheckBox pseudoSpatialisation;

    @FXML
    private HBox modelsList;
    private ArrayList<Toggle> modelsListToggles;
    private ToggleGroup modelsListToggleGroup;

    @FXML
    private Button addModelBtn;

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
    private SpinnerSlider infecRadiusController;

    @FXML
    private ChoiceBox<String> simulationMode;

    @FXML
    private VBox simulationModeParamsContainer;
    @FXML
    private VBox centerZoneEnterProbContainer;
    @FXML
    private SpinnerSlider centerZoneEnterProbController;
    @FXML
    private VBox centerZoneExitProbContainer;
    @FXML
    private SpinnerSlider centerZoneExitProbController;
    @FXML
    private VBox multiZoneTravelProbContainer;
    @FXML
    private SpinnerSlider multiZoneTravelProbController;

    @FXML
    private Accordion rulesAccordion;
    @FXML
    private TitledPane rulesDefaultExpendedPane;
    @FXML
    private EpidemicRule confinementRuleController;
    @FXML
    private EpidemicRule maskWearRuleController;
    @FXML
    private SpinnerSlider maskWearEfficacityController;
    @FXML
    private EpidemicRule quarantineRuleController;
    @FXML
    private EpidemicRule socialDistancingRuleController;
    @FXML
    private SpinnerSlider socialDistancingPctController;
    @FXML
    private EpidemicRule vaccinationRuleController;

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

        infecRadiusController.setMin(1);
        infecRadiusController.setMax(50);
        infecRadiusController.setStep(1);
        infecRadiusController.valueProperty().bindBidirectional(viewModel.infecRadiusProperty());

        addModelBtn.setGraphic(new MDL2IconFont("\uE710"));

        viewModel.chartProperty().addListener((ListChangeListener.Change<? extends ModelChart.Chart> c) -> {
            modelChartController.dataProperty().setAll(viewModel.chartProperty());
        });
        modelChartController.dataProperty().setAll(viewModel.chartProperty());

        modelChartScale.valueProperty().addListener((observable, oldValue, newValue) -> {
            viewModel.chartScaleProperty().set(Math.round(newValue.doubleValue()));
        });
        viewModel.chartScaleProperty().addListener((observable, oldValue, newValue) -> {
            modelChartScale.valueProperty().set(newValue.doubleValue());
        });
        modelChartScale.valueProperty().set(modelChartScale.valueProperty().doubleValue());

        pseudoSpatialisation.selectedProperty().bindBidirectional(viewModel.pseudoSpatialisation());

        simulationMode.getItems().setAll(
                HomeViewModel.SIMULATION_SIMPLE,
                HomeViewModel.SIMULATION_CENTER,
                HomeViewModel.SIMULATION_ZONES
        );
        simulationMode.valueProperty().bindBidirectional(viewModel.simulationMode());
        simulationMode.valueProperty().addListener((observable, oldValue, newValue) -> {
            handleSimulationModeChanged(newValue);
        });
        handleSimulationModeChanged(simulationMode.valueProperty().get());

        centerZoneEnterProbController.setMin(0);
        centerZoneEnterProbController.setMax(2);
        centerZoneEnterProbController.setStep(0.01);
        centerZoneEnterProbController.valueProperty().bindBidirectional(viewModel.centerZoneEnterProb());
        centerZoneEnterProbController.adjustValue(centerZoneEnterProbController.getValue());
        centerZoneExitProbController.setMin(0);
        centerZoneExitProbController.setMax(10);
        centerZoneExitProbController.setStep(0.1);
        centerZoneExitProbController.valueProperty().bindBidirectional(viewModel.centerZoneExitProb());
        centerZoneExitProbController.adjustValue(centerZoneExitProbController.getValue());
        multiZoneTravelProbController.setMin(0);
        multiZoneTravelProbController.setMax(10);
        multiZoneTravelProbController.setStep(0.01);
        multiZoneTravelProbController.valueProperty().bindBidirectional(viewModel.multiZoneTravelProb());
        multiZoneTravelProbController.adjustValue(multiZoneTravelProbController.getValue());

        confinementRuleController.respectProperty().bindBidirectional(viewModel.confinementRespect());
        confinementRuleController.delayProperty().bindBidirectional(viewModel.confinementDelay());

        maskWearEfficacityController.valueProperty().bindBidirectional(viewModel.maskWearEfficacity());
        maskWearRuleController.respectProperty().bindBidirectional(viewModel.maskWearRespect());
        maskWearRuleController.delayProperty().bindBidirectional(viewModel.maskWearDelay());

        quarantineRuleController.respectProperty().bindBidirectional(viewModel.quarantineRespect());
        quarantineRuleController.delayProperty().bindBidirectional(viewModel.quarantineDelay());

        socialDistancingPctController.valueProperty().bindBidirectional(viewModel.socialDistancingPct());
        socialDistancingRuleController.respectProperty().bindBidirectional(viewModel.socialDistancingRespect());
        socialDistancingRuleController.delayProperty().bindBidirectional(viewModel.socialDistancingDelay());

        vaccinationRuleController.respectProperty().bindBidirectional(viewModel.vaccinationRespect());
        vaccinationRuleController.delayProperty().bindBidirectional(viewModel.vaccinationDelay());
        vaccinationRuleController.setRespectText(new String("Pourcentage vacciné par jour".getBytes(), StandardCharsets.UTF_8));
        vaccinationRuleController.setRespectRange(0, 20, 0.01);

        rulesAccordion.setExpandedPane(rulesDefaultExpendedPane);
    }

    private void handleModelsChanged(ObservableList<String> models) {
        modelsList.getChildren().clear();
        modelsListToggles = new ArrayList<>();
        modelsListToggleGroup = new ToggleGroup();
        modelsListToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue == null) {
                modelsListToggleGroup.selectToggle(modelsListToggles.get(viewModel.selectedModelId().get()));
            } else {
                for(int i = 0; i < modelsListToggles.size(); i++) {
                    if(modelsListToggles.get(i) == newValue) {
                        viewModel.selectedModelId().set(i);
                    }
                }
            }
        });
        for (String model : models) {
            try {
                var loader = ModelSelect.load();
                modelsList.getChildren().add(loader.getRoot());
                ModelSelect controller = loader.getController();
                controller.setToggleGroup(modelsListToggleGroup);
                controller.setName(model);
                modelsListToggles.add(controller.getToggle());
            } catch (Exception err) {
                // TODO: Report error to user
                err.printStackTrace(System.err);
            }
        }
    }

    private void handleSelectedModelIdChanged(int modelId) {
        if(modelsListToggleGroup != null) {
            modelsListToggleGroup.selectToggle(modelsListToggles.get(modelId));
        }
    }

    private void handleModelCompsChanged(ObservableList<HomeViewModel.ModelCompProperty> comps) {
        modelCompList.getChildren().clear();
        modelCompListControllers = new ArrayList<>(comps.size());
        for(int i = 0; i < comps.size(); i++) {
            var comp = comps.get(i);
            try {
                var loader = ModelComp.load();
                modelCompList.getChildren().add(loader.getRoot());
                ModelComp controller = loader.getController();
                controller.nameProperty().bind(comp.name());
                controller.valueProperty().bindBidirectional(comp.value());
                controller.colorProperty().bindBidirectional(comp.color());
                if(i == comps.size() - 1) {
                    controller.showParam(false);
                }
            } catch (Exception err) {
                // TODO: Report error to user
                err.printStackTrace(System.err);
            }
        }
    }

    private void handleSimulationModeChanged(String simulationMode) {
        switch (simulationMode) {
            case HomeViewModel.SIMULATION_CENTER -> {
                simulationModeParamsContainer.getChildren().setAll(
                        centerZoneEnterProbContainer,
                        centerZoneExitProbContainer
                );
            }
            case HomeViewModel.SIMULATION_ZONES -> {
                simulationModeParamsContainer.getChildren().setAll(
                        multiZoneTravelProbContainer
                );
            }
            default -> {
                simulationModeParamsContainer.getChildren().clear();
            }
        }
    }
}
