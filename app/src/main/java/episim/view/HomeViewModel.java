package episim.view;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import episim.core.CompartmentConfig;
import episim.core.ModelChartGenerator;
import episim.core.ModelConfig;
import episim.view.component.ModelChart;
import episim.view.popup.ModelEditorViewPopup;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

import java.util.*;

/**
 * Modèle de la vue de {@link HomeView l'accueil}
 */
public class HomeViewModel implements ViewModel {
    public static class ModelCompProperty {
        private final DoubleProperty value = new SimpleDoubleProperty();
        private final StringProperty name = new SimpleStringProperty();
        private final ObjectProperty<Color> color = new SimpleObjectProperty<>();
        public ModelCompProperty(double value, String name, Color color) {
            this.value.set(value);
            this.name.set(name);
            this.color.set(color);
        }
        public DoubleProperty value() {
            return value;
        }
        public ObjectProperty<Color> color() {
            return color;
        }
        public StringProperty name() {
            return name;
        }
    }
    public static final String SIMULATION_SIMPLE = "Spatialisation simple";
    public static final String SIMULATION_ZONES = "Zones multiples";
    public static final String SIMULATION_CENTER = "Centre ville";

    @InjectScope
    private MainScope mainScope;

    private final ObservableList<String> models = FXCollections.observableArrayList();
    private final IntegerProperty selectedModelId = new SimpleIntegerProperty();
    private final ObservableList<ModelCompProperty> modelComps = FXCollections.observableArrayList();
    private final DoubleProperty modelBirth = new SimpleDoubleProperty();
    private final DoubleProperty modelDeath = new SimpleDoubleProperty();
    private final DoubleProperty popSize = new SimpleDoubleProperty();
    private final DoubleProperty infecPct = new SimpleDoubleProperty();
    private final DoubleProperty infecRadius = new SimpleDoubleProperty();
    private final ObservableList<ModelChart.Chart> chartsData = FXCollections.observableArrayList();
    private final BooleanProperty pseudoSpatialisation = new SimpleBooleanProperty(true);
    private final DoubleProperty chartScale = new SimpleDoubleProperty(100);
    private final StringProperty simulationMode = new SimpleStringProperty(SIMULATION_SIMPLE);
    private final DoubleProperty centerZoneEnterProb = new SimpleDoubleProperty(0);
    private final DoubleProperty centerZoneExitProb = new SimpleDoubleProperty(0);
    private final DoubleProperty multiZoneTravelProb = new SimpleDoubleProperty(0);
    private final DoubleProperty confinementRespect = new SimpleDoubleProperty(0);
    private final DoubleProperty confinementDelay = new SimpleDoubleProperty(0);
    private final DoubleProperty maskWearEfficacity = new SimpleDoubleProperty(0);
    private final DoubleProperty maskWearRespect = new SimpleDoubleProperty(0);
    private final DoubleProperty maskWearDelay = new SimpleDoubleProperty(0);
    private final DoubleProperty quarantineRespect = new SimpleDoubleProperty(0);
    private final DoubleProperty quarantineDelay = new SimpleDoubleProperty(0);
    private final DoubleProperty socialDistancingPct = new SimpleDoubleProperty(0);
    private final DoubleProperty socialDistancingRespect = new SimpleDoubleProperty(0);
    private final DoubleProperty socialDistancingDelay = new SimpleDoubleProperty(0);
    private final DoubleProperty vaccinationRespect = new SimpleDoubleProperty(0);
    private final DoubleProperty vaccinationDelay = new SimpleDoubleProperty(0);


    public void initialize() {
        selectedModelId.addListener((observable, oldValue, newValue) -> {
            loadModelConfig(selectedModelId.get());
        });
        chartScale.addListener((observable, oldValue, newValue) -> {
            updateChart();
        });
        pseudoSpatialisation.addListener((observable, oldValue, newValue) -> {
            updateChart();
        });

        ModelEditorViewPopup.onValidateProperty().set(config -> {
            if(config != null) {
                if(!mainScope.getSimulationConfig().getModels().contains(config)) {
                    mainScope.getSimulationConfig().addModel(config);
                }
                loadConfig();
            }
        });

        mainScope.simulationConfig().addListener((observable, oldValue, newValue) -> {
            loadConfig();
            bindConfig();
        });
        loadConfig();
        bindConfig();
    }

    public void startSimulation() {
        mainScope.publish(MainScope.SIMULATION);
    }

    public void editModel(int modelId) {
        ModelEditorViewPopup.open(getModelConfig(modelId));
    }

    public void addModel() {
        ModelEditorViewPopup.open(ModelConfig.getDefault());
    }

    public void removeModel(int modelId) {
        if(mainScope.getSimulationConfig().getModels().size() > 1) {
            // Il doit y avoir toujours au moins un modèle
            mainScope.getSimulationConfig().removeModel(modelId);
            loadConfig();
        }
    }

    public ObservableList<String> modelsProperty() {
        return models;
    }
    public IntegerProperty selectedModelId() {
        return selectedModelId;
    }
    public ObservableList<ModelCompProperty> modelCompsProperty() {
        return modelComps;
    }
    public DoubleProperty modelBirthProperty() {
        return modelBirth;
    }
    public DoubleProperty modelDeathProperty() {
        return modelDeath;
    }
    public DoubleProperty popSizeProperty() {
        return popSize;
    }
    public DoubleProperty infecPctProperty() {
        return infecPct;
    }
    public DoubleProperty infecRadiusProperty() {
        return infecRadius;
    }
    public ObservableList<ModelChart.Chart> chartProperty() {
        return chartsData;
    }
    public BooleanProperty pseudoSpatialisation() {
        return pseudoSpatialisation;
    }
    public StringProperty simulationMode() {
        return simulationMode;
    }
    public DoubleProperty centerZoneEnterProb() {
        return centerZoneEnterProb;
    }
    public DoubleProperty centerZoneExitProb() {
        return centerZoneExitProb;
    }
    public DoubleProperty multiZoneTravelProb() {
        return multiZoneTravelProb;
    }
    public DoubleProperty chartScaleProperty() {
        return chartScale;
    }
    public DoubleProperty confinementRespect() {
        return confinementRespect;
    }
    public DoubleProperty confinementDelay() {
        return confinementDelay;
    }
    public DoubleProperty maskWearEfficacity() {
        return maskWearEfficacity;
    }
    public DoubleProperty maskWearRespect() {
        return maskWearRespect;
    }
    public DoubleProperty maskWearDelay() {
        return maskWearDelay;
    }
    public DoubleProperty quarantineRespect() {
        return quarantineRespect;
    }
    public DoubleProperty quarantineDelay() {
        return quarantineDelay;
    }
    public DoubleProperty socialDistancingPct() {
        return socialDistancingPct;
    }
    public DoubleProperty socialDistancingRespect() {
        return socialDistancingRespect;
    }
    public DoubleProperty socialDistancingDelay() {
        return socialDistancingDelay;
    }
    public DoubleProperty vaccinationRespect() {
        return vaccinationRespect;
    }
    public DoubleProperty vaccinationDelay() {
        return vaccinationDelay;
    }

    private ModelConfig getModelConfig(int modelId) {
        return mainScope.getSimulationConfig().getModels().get(modelId);
    }
    private CompartmentConfig getCompConfig(int modelId, int compId) {
        return getModelConfig(modelId).getCompartments().get(compId);
    }

    /**
     * Connecte les propriétés de {@code HomeViewModel} au attributs de {@code SimulationConfig}
     */
    private void bindConfig() {
        bindModels();
        selectedModelId.addListener((observable, oldValue, newValue) -> {
            mainScope.getSimulationConfig().setSelectedModelId(newValue.intValue());
            updateChart();
        });
        popSize.addListener((observable, oldValue, newValue) -> {
            mainScope.getSimulationConfig().setPopulationSize(newValue.intValue());
            updateChart();
        });
        infecPct.addListener((observable, oldValue, newValue) -> {
            mainScope.getSimulationConfig().setInitialInfectious(newValue.doubleValue() / 100.0);
            updateChart();
        });
        infecRadius.addListener((observable, oldValue, newValue) -> {
            if(newValue.doubleValue() < 1) {
                infecRadius.set(1);
            } else {
                mainScope.getSimulationConfig().setInfectionRadius(newValue.doubleValue());
                updateChart();
            }
        });
        simulationMode.addListener((observable, oldValue, newValue) -> {
            var config = mainScope.getSimulationConfig();
            switch (newValue) {
                case SIMULATION_SIMPLE -> {
                    config.setEnableCenterZone(false);
                    config.setEnableMultiZone(false);
                }
                case SIMULATION_CENTER -> {
                    config.setEnableCenterZone(true);
                    config.setEnableMultiZone(false);
                }
                case SIMULATION_ZONES -> {
                    config.setEnableCenterZone(false);
                    config.setEnableMultiZone(true);
                }
            }
        });
        centerZoneEnterProb.addListener((observable, oldValue, newValue) -> {
            mainScope.getSimulationConfig().setCenterZoneEnterProb(newValue.doubleValue() / 100.0 * 24.0);
        });
        centerZoneExitProb.addListener((observable, oldValue, newValue) -> {
            mainScope.getSimulationConfig().setCenterZoneExitProb(newValue.doubleValue() / 100.0 * 24.0);
        });
        multiZoneTravelProb.addListener((observable, oldValue, newValue) -> {
            mainScope.getSimulationConfig().setMultiZoneTravelProb(newValue.doubleValue() / 100.0);
        });

        confinementRespect.addListener((observable, oldValue, newValue) -> {
            mainScope.getSimulationConfig().getConfinement().setRespectProb(newValue.doubleValue() / 100.0);
        });
        confinementDelay.addListener((observable, oldValue, newValue) -> {
            mainScope.getSimulationConfig().getConfinement().setDelay(newValue.doubleValue());
        });

        maskWearEfficacity.addListener((observable, oldValue, newValue) -> {
            mainScope.getSimulationConfig().setMaskWearEfficacity(newValue.doubleValue() / 100.0);
        });
        maskWearRespect.addListener((observable, oldValue, newValue) -> {
            mainScope.getSimulationConfig().getMaskWear().setRespectProb(newValue.doubleValue() / 100.0);
        });
        maskWearDelay.addListener((observable, oldValue, newValue) -> {
            mainScope.getSimulationConfig().getMaskWear().setDelay(newValue.doubleValue());
        });

        quarantineRespect.addListener((observable, oldValue, newValue) -> {
            mainScope.getSimulationConfig().getQuarantine().setRespectProb(newValue.doubleValue() / 100.0);
        });
        quarantineDelay.addListener((observable, oldValue, newValue) -> {
            mainScope.getSimulationConfig().getQuarantine().setDelay(newValue.doubleValue());
        });

        socialDistancingPct.addListener((observable, oldValue, newValue) -> {
            mainScope.getSimulationConfig().setSocialDistancingFactor(newValue.doubleValue() / 100.0);
        });
        socialDistancingRespect.addListener((observable, oldValue, newValue) -> {
            mainScope.getSimulationConfig().getSocialDistancing().setRespectProb(newValue.doubleValue() / 100.0);
        });
        socialDistancingDelay.addListener((observable, oldValue, newValue) -> {
            mainScope.getSimulationConfig().getSocialDistancing().setDelay(newValue.doubleValue());
        });

        vaccinationRespect.addListener((observable, oldValue, newValue) -> {
            mainScope.getSimulationConfig().getVaccination().setRespectProb(newValue.doubleValue() / 100.0);
        });
        vaccinationDelay.addListener((observable, oldValue, newValue) -> {
            mainScope.getSimulationConfig().getVaccination().setDelay(newValue.doubleValue());
        });
    }
    private void bindModels() {
        bindModelComps();
        modelComps.addListener((ListChangeListener.Change<? extends ModelCompProperty> c) -> {
            bindModelComps();
            updateChart();
        });
        modelBirth.addListener((observable, oldValue, newValue) -> {
            getModelConfig(selectedModelId.get()).setBirth(newValue.doubleValue());
            updateChart();
        });
        modelDeath.addListener((observable, oldValue, newValue) -> {
            getModelConfig(selectedModelId.get()).setDeath(newValue.doubleValue());
            updateChart();
        });
    }
    private void bindModelComps() {
        for(int i = 0; i < modelComps.size(); i++) {
            var comp = modelComps.get(i);
            int compId = i;
            comp.value().addListener((observable, oldValue, newValue) -> {
                getCompConfig(selectedModelId.get(), compId).setParam(newValue.doubleValue());
                updateChart();
            });
            comp.name().addListener((observable, oldValue, newValue) -> {
                getCompConfig(selectedModelId.get(), compId).setName(newValue);
                updateChart();
            });
            comp.color().addListener((observable, oldValue, newValue) -> {
                getCompConfig(selectedModelId.get(), compId).setColor(newValue.toString());
                updateChart();
            });
        }
    }

    /**
     * Charge les attributs de {@code SimulationConfig} dans {@code HomeViewModel}
     */
    private void loadConfig() {
        var config = mainScope.getSimulationConfig();

        selectedModelId.set(config.getSelectedModelId());

        ArrayList<String> modelNames = new ArrayList<>(models.size());
        for(var model : config.getModels()) {
            modelNames.add(model.getName());
        }
        models.setAll(modelNames);

        loadModelConfig(selectedModelId.get());

        popSize.set(config.getPopulationSize());
        infecPct.set(config.getInitialInfectious() * 100.0);
        infecRadius.set(config.getInfectionRadius());
        simulationMode.set(config.isEnableMultiZone() ? SIMULATION_ZONES
                : config.isEnableCenterZone() ? SIMULATION_CENTER : SIMULATION_SIMPLE);

        centerZoneEnterProb.set(config.getCenterZoneEnterProb() * 100.0 / 24.0);
        centerZoneExitProb.set(config.getCenterZoneExitProb() * 100.0 / 24.0);
        multiZoneTravelProb.set(config.getMultiZoneTravelProb() * 100.0);

        confinementRespect.set(config.getConfinement().getRespectProb() * 100.0);
        confinementDelay.set(config.getConfinement().getDelay());

        quarantineRespect.set(config.getQuarantine().getRespectProb() * 100.0);
        quarantineDelay.set(config.getQuarantine().getDelay());

        maskWearEfficacity.set(config.getMaskWearEfficacity() * 100.0);
        maskWearRespect.set(config.getMaskWear().getRespectProb() * 100.0);
        maskWearDelay.set(config.getMaskWear().getDelay());

        socialDistancingPct.set(config.getSocialDistancingFactor() * 100.0);
        socialDistancingRespect.set(config.getSocialDistancing().getRespectProb() * 100.0);
        socialDistancingDelay.set(config.getSocialDistancing().getDelay());

        vaccinationRespect.set(config.getVaccination().getRespectProb() * 100.0);
        vaccinationDelay.set(config.getVaccination().getDelay());

        updateChart();
    }
    private void loadModelConfig(int modelId) {
        var config = mainScope.getSimulationConfig();
        var selectedModel = config.getModels().get(modelId);
        modelComps.clear();
        for(var comp : selectedModel.getCompartments()) {
            modelComps.add(new ModelCompProperty(comp.getParam(), comp.getName(),Color.valueOf(comp.getColor())));
        }
        modelBirth.set(selectedModel.getBirth());
        modelDeath.set(selectedModel.getDeath());
    }

    private void updateChart() {
        var modelConfig = getModelConfig(selectedModelId().get());

        var gen = new ModelChartGenerator(
                modelConfig,
                infecPctProperty().get() / 100,
                popSizeProperty().get(),
                infecRadius.get(),
                pseudoSpatialisation.get(),
                chartScale.doubleValue(),
                400
        );

        ArrayList<ModelChartGenerator.CompData> data;
        try {
            // long time = System.currentTimeMillis();
            data = gen.generate();
            // time = System.currentTimeMillis() - time;
            // System.out.println("ModelChartGenerator ms: " + time);
        } catch (RuntimeException err) {
            // L'erreur ne doit pas arriver
            err.printStackTrace(System.err);
            return;
        }

        ArrayList<ModelChart.Chart> chartData = new ArrayList<>(data.size());
        for(int i = 0; i < data.size(); i++) {
            var compData = data.get(i);
            var compConfig = modelConfig.getCompartments().get(i);
            var chart = new ModelChart.Chart(
                    compData.getX(),
                    compData.getY(),
                    compConfig.getName(),
                    Color.valueOf(compConfig.getColor())
            );
            chartData.add(chart);
        }
        chartProperty().setAll(chartData);
    }
}
