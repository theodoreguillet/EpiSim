package episim.util;

import javafx.util.StringConverter;

/**
 * Convertit un double flottant en chaine de caractère et inversement
 * Autorise la virgule ',' ou le point '.' pour définir la partie décimale
 */
public class StringDoubleConverter extends StringConverter<Double> {
    @Override
    public String toString(Double object) {
        if(object.doubleValue() == object.intValue()) {
            return Integer.toString(object.intValue());
        } else {
            return object.toString().replace('.',',');
        }
    }

    @Override
    public Double fromString(String string) {
        return string.isEmpty() ? 0.0 : Double.parseDouble(string.replace(',','.'));
    }
}
