package episim.view.component;

import episim.util.SpinnerWrapper;
import episim.util.StringDoubleConverter;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
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
    private ColorPicker colorPicker;

    @FXML
    private Spinner<Double> spinner;
    private SpinnerWrapper spinnerWrapper;

    @FXML
    private Rectangle rectangle;
    private final ObjectProperty<Color> color = new SimpleObjectProperty<>(Color.WHITE);

    @FXML
    private Group mainContainer;
    @FXML
    private Group spinnerContainer;

    @FXML
    private void onColorPaneMouseEntered() {
        rectangle.setStroke(Color.BLACK);
        rectangle.setStrokeWidth(1);
    }

    @FXML
    private void onColorPaneMouseExited() {
        rectangle.setStroke(Color.BLACK);
        rectangle.setStrokeWidth(0.5);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        spinnerWrapper = new SpinnerWrapper(spinner, 0, 10, 0.5, 0.01);
        var valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 10, 0.5, 0.01);
        valueFactory.setConverter(new StringDoubleConverter());
        spinner.setValueFactory(valueFactory);

        colorPicker.valueProperty().bindBidirectional(color);
        color.addListener((observable, oldValue, newValue) -> {
            setCompColor(newValue);
        });
        setCompColor(color.get());

        rectangle.setStrokeWidth(0.5);
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

    /**
     * Propriété de la couleur du compartiment
     * @return La propriété de la couleur
     */
    public ObjectProperty<Color> colorProperty() {
        return color;
    }

    /**
     * Définie si le valeur du compartiment est éditable ou non
     * @param show true si il est éditable, false sinon
     */
    public void showParam(boolean show) {
        if(show) {
            if(!mainContainer.getChildren().contains(spinnerContainer)) {
                mainContainer.getChildren().add(spinnerContainer);
            }
        } else {
            mainContainer.getChildren().remove(spinnerContainer);
        }
    }

    private void setCompColor(Color color) {
        var gradiant = new RadialGradient(0, 0, 0.5, 0.5, 0.6, true,
                CycleMethod.NO_CYCLE,
                new Stop(0, Color.WHITE),
                new Stop(1, color)
        );
        rectangle.setFill(gradiant);
    }
}
