package episim.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Classe utilitaire des fonctions de calcul
 */
public class MathUtils {
    /**
     * Renvoit la valeur arrondie à l'étape près comprise entre min et max
     * @param val La valeur à ajuster
     * @param min La valeur minimum
     * @param max La valeur maximum
     * @param step La valeur de l'écart
     * @return La valeur ajustée à l'étape près
     */
    public static double snapToTicks(double val, double min, double max, double step) {
        BigDecimal bdValue = new BigDecimal(Double.toString(val));
        BigDecimal bdStep = new BigDecimal(Double.toString(step));

        int scale = bdStep.scale();

        bdValue = bdValue.setScale(scale, RoundingMode.HALF_UP).movePointLeft(-scale);
        bdStep = bdStep.movePointLeft(-scale);

        bdValue = bdValue.subtract(bdValue.remainder(bdStep));
        bdValue = bdValue.movePointLeft(scale);

        val = bdValue.doubleValue();
        if(val > max) {
            val = max;
        } else if(val < min) {
            val = min;
        }
        return val;
    }
}
