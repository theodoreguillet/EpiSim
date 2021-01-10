package episim.util;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

/**
 * Encapsule un composant {@ref Spinner} de javafx
 */
public class SpinnerWrapper {
    private final Spinner<Double> spinner;
    private final DoubleProperty valueProperty = new SimpleDoubleProperty();

    /**
     * Encapsule un composant {@ref Spinner} de javafx
     * @param spinner Le {@ref Spinner}
     * @param min La valeur minimum autorisée
     * @param max La valeur maximum autorisée
     * @param value La valeur initiale
     * @param step La valeur de l'incrément ou du décrément à chaque étape
     */
    public SpinnerWrapper(Spinner<Double> spinner, double min, double max, double value, double step) {
        this.spinner = spinner;

        var valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(min, max, value, step);
        valueFactory.setConverter(new StringDoubleConverter());
        spinner.setValueFactory(valueFactory);

        // On fait le lien manuellement a cause d'un bug du lien sur le Spinner
        spinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            valueProperty.setValue(newValue);
        });
        valueProperty.addListener((observable, oldValue, newValue) -> {
            spinner.getValueFactory().setValue(newValue.doubleValue());
        });
    }

    /**
     * Propriété de la valeur du {@code Spinner}
     * @return La propriété de la valeur
     */
    public DoubleProperty valueProperty() {
        return valueProperty;
    }

    /**
     * Le composant {@code Spinner} sous-jacent
     * @return Le composant {@code Spinner}
     */
    public Spinner<Double> getSpinner() {
        return spinner;
    }
}
