package episim.view.component;

import episim.util.SpinnerWrapper;
import episim.util.StringDoubleConverter;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Composant représentant le compartiment d'un modèle épidémique avec son nom
 * et un sélecteur pour définir la valeur de son paramètre de transition
 */
public class ModelComp implements Initializable {
    /**
     * Crée un {@code ModelComp}
     * @return retourne le {@code FXMLLoader}
     * @throws IOException Génère une erreur si la ressource est inaccessible
     */
    public static FXMLLoader load() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ModelComp.class.getResource("/episim/view/component/ModelComp.fxml"));
        fxmlLoader.load();
        return fxmlLoader;
    }

    @FXML
    private Text name;

    @FXML
    private Spinner<Double> spinner;
    private SpinnerWrapper spinnerWrapper;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        spinnerWrapper = new SpinnerWrapper(spinner, 0, 10, 0.5, 0.01);
        var valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 10, 0.5, 0.01);
        valueFactory.setConverter(new StringDoubleConverter());
        spinner.setValueFactory(valueFactory);
    }

    /**
     * Propriété de la valeur de ce {@code ModelComp}
     * @return La propriété de la valeur
     */
    public DoubleProperty valueProperty() {
        return spinnerWrapper.valueProperty();
    }

    /**
     * Propriété du nom du compartiment
     * @return La propriété du nom
     */
    public StringProperty nameProperty() {
        return name.textProperty();
    }

    /**
     * Définie le nom du compartiment
     * @param newName Le nom du compartiment
     */
    public void setName(String newName) {
        name.setText(newName);
    }
}
