package episim.view.popup;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.shape.Line;
import jfxtras.styles.jmetro.MDL2IconFont;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Vue de la fênetre d'édition d'un modèle
 */
public class ModelEditorView implements FxmlView<ModelEditorViewModel>, Initializable {
    @InjectViewModel
    ModelEditorViewModel viewModel;

    @FXML
    private TextField nameField;

    @FXML
    private HBox compsContainer;

    @FXML
    private void validate() {
        viewModel.validate();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nameField.textProperty().bindBidirectional(viewModel.nameProperty());
        nameField.promptTextProperty().bind(viewModel.nameProperty());

        viewModel.compartmentsProperty().addListener((ListChangeListener.Change<? extends StringProperty> c) -> {
            handleCompartmentsChanged(viewModel.compartmentsProperty());
        });
        handleCompartmentsChanged(viewModel.compartmentsProperty());
    }

    private void handleCompartmentsChanged(ObservableList<StringProperty> compartments) {
        compsContainer.getChildren().clear();
        for(int i = 0; i < compartments.size(); i++) {
            final int compId = i;
            var comp = compartments.get(compId);
            TextField compName = new TextField(comp.getName());
            compName.setPrefSize(28, 28);
            compName.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
            compName.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
            compName.setAlignment(Pos.CENTER);
            if(ModelEditorViewModel.RESERVED_NAMES.contains(comp.get())) {
                compName.setEditable(false);
                compName.textProperty().bind(comp);
            } else {
                compName.textProperty().addListener((observable, oldValue, newValue) -> {
                    if(!newValue.isEmpty()) {
                        String val = newValue.toUpperCase();
                        if(ModelEditorViewModel.ALLOWED_NAMES.stream().noneMatch(n -> n.equals(val))) {
                            compName.setText(oldValue);
                        } else {
                            compName.setText(val);
                        }
                    }
                });
                compName.textProperty().bindBidirectional(comp);
                compName.promptTextProperty().set("X");
            }
            compsContainer.getChildren().add(compName);

            if(ModelEditorViewModel.RESERVED_NAMES.stream().noneMatch(n -> n.equals(comp.get()))) {
                Button removeBtn = new Button();
                MDL2IconFont removeIcon = new MDL2IconFont("\uE74D");
                removeBtn.setGraphic(removeIcon);
                removeBtn.setPrefSize(18, 18);
                removeBtn.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
                removeBtn.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
                removeBtn.setOnAction(e -> {
                    viewModel.removeCompartment(compId);
                });
                compsContainer.getChildren().addAll(
                        new Line(0, 0, 2, 0),
                        removeBtn
                );
            }

            if(i < compartments.size() - 1) {
                Button addBtn = new Button("+");
                addBtn.setPrefSize(24, 24);
                addBtn.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
                addBtn.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
                addBtn.setStyle("-fx-background-radius: 5em; -fx-border-radius: 5em; -fx-padding: 0; -fx-text-alignment: center;");
                addBtn.setOnAction(e -> {
                    viewModel.addCompartment(compId);
                });
                compsContainer.getChildren().addAll(
                        new Line(0, 0, 10, 0),
                        addBtn,
                        new Line(0, 0, 10, 0)
                );
            }
        }
    }
}
