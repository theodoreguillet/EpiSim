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

    /**
     * Calcule le modulo x % y pour des nombres flottant de la même manière que {@code Math.floorMod}
     * @param x x
     * @param y y
     * @return x % y
     */
    public static double floatMod(double x, double y){
        return (x - Math.floor(x/y) * y);
    }

    /**
     * Retourne un angle modulo 2pi compris entre -pi (exclus) et pi (inclus)
     * @param angle Un angle en radian
     * @return Un angle compris entre -pi (exclus) et pi (inclus)
     */
    public static double angleMod(double angle) {
        angle = floatMod(angle, 2 * Math.PI);
        if(angle > Math.PI) {
            angle = angle - (2 * Math.PI);
        }
        return angle;
    }

    /**
     * Retourne le carré de la distance entre (x1, y1) et (x2, y2)
     * @param x1 x1
     * @param y1 y1
     * @param x2 x2
     * @param y2 y2
     * @return Le carré de la distance entre (x1, y1) et (x2, y2)
     */
    public static double dst2(double x1, double y1, double x2, double y2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        return dx * dx + dy * dy;
    }
}
