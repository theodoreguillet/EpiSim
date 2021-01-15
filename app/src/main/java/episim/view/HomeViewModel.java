package episim.view;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import episim.core.CompartmentConfig;
import episim.core.ModelChartGenerator;
import episim.core.ModelConfig;
import episim.view.component.ModelChart;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.scene.chart.XYChart;

import java.util.*;

/**
 * Modèle de la vue de l'accueil
 */
public class HomeViewModel implements ViewModel {
    static class ModelCompProperty {
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

    @InjectScope
    private ConfigurationScope configScope;

    private final ObservableList<String> models = FXCollections.observableArrayList();
    private final IntegerProperty selectedModelId = new SimpleIntegerProperty();
    private final ObservableList<ModelCompProperty> modelComps = FXCollections.observableArrayList();
    private final DoubleProperty modelBirth = new SimpleDoubleProperty();
    private final DoubleProperty modelDeath = new SimpleDoubleProperty();
    private final DoubleProperty popSize = new SimpleDoubleProperty();
    private final DoubleProperty infecPct = new SimpleDoubleProperty();
    private final ObservableList<ModelChart.Chart> chartsData = FXCollections.observableArrayList();


    public void initialize() {
        selectedModelId.addListener((observable, oldValue, newValue) -> {
            loadModelConfig(selectedModelId.get());
            updateChart();
        });

        loadConfig();
        bindConfig();
        updateChart();
    }

    public void startSimulation() {
        configScope.publish(ConfigurationScope.SIMULATION);
    }

    public ObservableList<ModelChart.Chart> chartProperty() {
        return chartsData;
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

    private ModelConfig getModelConfig(int modelId) {
        return configScope.simulationConfig().getModels().get(modelId);
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
            configScope.simulationConfig().setSelectedModelId(newValue.intValue());
        });
        popSize.addListener((observable, oldValue, newValue) -> {
            configScope.simulationConfig().setPopulationSize(newValue.intValue());
        });
        infecPct.addListener((observable, oldValue, newValue) -> {
            configScope.simulationConfig().setInitialInfectious(newValue.doubleValue() / 100.0);
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
        var config = configScope.simulationConfig();
        models.clear();
        for(var model : config.getModels()) {
            models.add(model.getName());
        }
        loadModelConfig(selectedModelId.get());
        popSize.set(config.getPopulationSize());
        infecPct.set(config.getInitialInfectious() * 100.0);
    }
    private void loadModelConfig(int modelId) {
        var config = configScope.simulationConfig();
        var selectedModel = config.getModels().get(modelId);
        modelComps.clear();
        for(var comp : selectedModel.getCompartments()) {
            modelComps.add(new ModelCompProperty(comp.getParam(), comp.getName(),Color.valueOf(comp.getColor())));
        }
        modelBirth.set(selectedModel.getBirth());
        modelDeath.set(selectedModel.getDeath());
    }

    private void updateChart(){
        var data = ModelChartGenerator.generate(getModelConfig(selectedModelId().get()), infecPctProperty().get(), 365, 1);

        //transformer data en ModelChart.Chart
        chartProperty().clear();

        ArrayList<ModelChart.Chart> newData = new ArrayList<>();

        for(var CompData: data){
            ModelChart.Chart tempChart = new ModelChart.Chart();
            ArrayList<XYChart.Data<Double, Double>> tempList = new ArrayList<>();
            for(var x: CompData.x){
                for(var y: CompData.y){
                    tempList.add(new XYChart.Data<Double, Double>(x, y));
                }
            }
            tempChart.setPoints(FXCollections.observableArrayList(tempList));
            newData.add(tempChart);
        }

        chartProperty().addAll(FXCollections.observableArrayList(newData));
    }
}
