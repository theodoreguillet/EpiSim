package episim.view.component;

import episim.util.MathUtils;
import episim.util.StringDoubleConverter;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Composant contenant un spinner et un curseur connectés
 */
public class SpinnerSlider implements Initializable {
    /**
     * Crée un {@code SpinnerSlider}
     * @return retourne le {@code FXMLLoader}
     * @throws IOException Génère une erreur si la ressource est inaccessible
     */
    public static FXMLLoader load() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ModelComp.class.getResource("/episim/view/component/SpinnerSlider.fxml"));
        fxmlLoader.load();
        return fxmlLoader;
    }

    @FXML
    private Spinner<Double> spinner;

    @FXML
    private Slider slider;

    private DoubleProperty value = new SimpleDoubleProperty(0);
    private double step = 0;
    private double min = 0;
    private double max = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        var valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 0);
        valueFactory.setConverter(new StringDoubleConverter());
        spinner.setValueFactory(valueFactory);

        spinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            value.setValue(newValue);
        });
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(slider.isFocused()) { // Allow user to enter arbitrary value in the spinner
                adjustValue(newValue.doubleValue());
            }
        });
        value.addListener((observable, oldValue, newValue) -> {
            spinner.getValueFactory().setValue(newValue.doubleValue());
            slider.valueProperty().setValue(newValue);
        });

        setMin(0);
        setMax(100);
        setStep(0.1);
        setValue(50);
    }

    /**
     * Propriété de la valeur de ce {@code SpinnerSlider}
     * @return La propriété de la valeur
     */
    public DoubleProperty valueProperty() {
        return value;
    }

    /**
     * Définie la valeur
     * @param newValue La nouvelle valeur
     */
    public void setValue(double newValue) {
        value.set(newValue);
    }

    /**
     * Ajuste {@link #valueProperty() value} pour correspondre à <code>newValue</code>.
     * @param newValue La valeur ajustée
     */
    public void adjustValue(double newValue) {
        setValue(MathUtils.snapToTicks(newValue, min, max, step));
    }

    /**
     * @return La valeur actuelle
     */
    public double getValue() {
        return value.get();
    }

    /**
     * Définie la valeur minimum, doit être plus petite que {@link #setMax(double) max}.
     * @param newMin La nouvelle valeur minimim
     */
    public void setMin(double newMin) {
        min = newMin;
        if(value.get() < min) {
            value.set(min);
        }
        ((SpinnerValueFactory.DoubleSpinnerValueFactory)spinner.getValueFactory()).setMin(min);
        slider.setMin(min);
    }

    /**
     * Définie la valeur maximum, doit être plus petite que {@link #setMin(double) min}.
     * @param newMax La nouvelle valeur maximum
     */
    public void setMax(double newMax) {
        max = newMax;
        if(value.get() > max) {
            value.set(max);
        }
        ((SpinnerValueFactory.DoubleSpinnerValueFactory)spinner.getValueFactory()).setMax(max);
        slider.setMax(max);
    }

    /**
     * Définie l'écart entre les valeurs et l'incrément.
     * @param newStep Le nouvel écart
     */
    public void setStep(double newStep) {
        step = newStep;
        slider.setBlockIncrement(step);
        slider.setMajorTickUnit(step);
        ((SpinnerValueFactory.DoubleSpinnerValueFactory)spinner.getValueFactory()).setAmountToStepBy(step);
    }
}
