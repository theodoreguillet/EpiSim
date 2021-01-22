package episim.view.popup;

import de.saxsys.mvvmfx.ViewModel;
import episim.core.CompartmentConfig;
import episim.core.ModelConfig;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Modèle de la vue {@link ModelEditorView}
 */
public class ModelEditorViewModel implements ViewModel {
    public static List<String> RESERVED_NAMES = Arrays.asList(
            CompartmentConfig.SUSCEPTIBLE,
            CompartmentConfig.INFECTIOUS,
            CompartmentConfig.RECOVERED
    );
    public static final List<String> ALLOWED_NAMES = Stream.of(
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
            "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
    ).filter(n -> RESERVED_NAMES.stream().noneMatch(r -> r.equals(n))).collect(Collectors.toList());
    private ModelConfig config;
    private List<CompartmentConfig> modelComps;

    private final StringProperty name = new SimpleStringProperty("");
    private final ObservableList<StringProperty> compartments = FXCollections.observableArrayList();

    public void validate() {
        config.setName(name.get());
        config.setCompartments(modelComps);
        ModelEditorViewPopup.validate(config);
    }

    public void setEditedModel(ModelConfig modelConfig) {
        config = modelConfig;

        name.set(config.getName());

        modelComps = config.getCompartments().stream().map(c -> (CompartmentConfig)c.clone()).collect(Collectors.toList());
        updateCompartments();
    }

    public void addCompartment(int idx) {
        modelComps.add(idx + 1, new CompartmentConfig(0.25, "X", "#000080"));
        updateCompartments();
    }

    public void removeCompartment(int idx) {
        modelComps.remove(idx);
        updateCompartments();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public ObservableList<StringProperty> compartmentsProperty() {
        return compartments;
    }

    private void updateCompartments() {
        compartments.clear();
        for(var comp : modelComps) {
            var property = new SimpleStringProperty(comp.getName());
            property.addListener((observable, oldValue, newValue) -> {
                comp.setName(property.get());
            });
            compartments.add(property);
        }
    }
}
