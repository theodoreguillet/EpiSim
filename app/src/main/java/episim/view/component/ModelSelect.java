package episim.view.component;

import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import jfxtras.styles.jmetro.MDL2IconFont;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ModelSelect implements Initializable {
    /**
     * Crée un {@code ModelSelect}
     * @return retourne le {@code FXMLLoader}
     * @throws IOException Génère une erreur si la ressource est inaccessible
     */
    public static FXMLLoader load() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ModelComp.class.getResource("/episim/view/component/ModelSelect.fxml"));
        fxmlLoader.load();
        return fxmlLoader;
    }

    @FXML
    private ToggleButton toggle;

    @FXML
    private Button editBtn;

    @FXML
    private Button removeBtn;

    @FXML
    private BorderPane editPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        MDL2IconFont editIcon = new MDL2IconFont("\uE70F");
        editBtn.setGraphic(editIcon);
        MDL2IconFont removeIcon = new MDL2IconFont("\uE74D");
        removeBtn.setGraphic(removeIcon);

        toggle.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                displayEdit(newValue);
            }
        });
        displayEdit(toggle.isSelected());
    }

    /**
     * Propriété de l'état sélectionné de ce {@code ModelSelect}
     * @return La propriété de l'état sélectionné
     */
    public BooleanProperty selectedProperty() {
        return toggle.selectedProperty();
    }

    /**
     * Définie le groupe du {@code ToggleButton}
     * @param group Le {@code ToggleGroup}
     */
    public void setToggleGroup(ToggleGroup group) {
        toggle.setToggleGroup(group);
    }

    public Toggle getToggle() {
        return toggle;
    }

    /**
     * Définie le nom du modèle
     * @param name Le nom du modèle
     */
    public void setName(String name) {
        toggle.setText(name);
    }

    private void displayEdit(boolean visible) {
        if(visible) {
            editPane.setVisible(true);
            editPane.setPrefWidth(BorderPane.USE_COMPUTED_SIZE);
        } else {
            editPane.setVisible(false);
            editPane.setPrefWidth(0);
        }
    }
}
