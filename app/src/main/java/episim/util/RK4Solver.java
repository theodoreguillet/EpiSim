package episim.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Résout un système différentiel avec la méthode de Runge-Kutta d'ordre 4
 */
public abstract class RK4Solver {
    public interface DerivativeFunc {
        List<Double> compute(double t, List<Double> y);
    }
    public interface ValueLimit {
        List<Double> limit(List<Double> y);
    }
    public static class Data {
        private final double x;
        private final List<Double> y;

        Data(double x, List<Double> y){
            this.x = x;
            this.y = y;
        }

        public double getX() {
            return x;
        }

        public List<Double> getY() {
            return y;
        }
    }

    /**
     * Résout un système différentiel avec la méthode de Runge-Kutta d'ordre 4
     * @param tmax La temps maximum de l'intervalle [0, tmax]
     * @param ssize Le nombre de sous-intervalles
     * @param y0 La valeur initiale du système
     * @param func La fonction dérivée du système
     * @param limiter Réduit les valeurs prise par le système (permet d'éviter que le calcul numérique sort des
     *              valeurs autorisée pour le système)
     * @return La liste des points du système résolut
     */
    public static List<Data> solve(double tmax, int ssize, List<Double> y0, DerivativeFunc func, ValueLimit limiter) {
        ArrayList<Data> data = new ArrayList<>(ssize);
        data.add(new Data(0, y0));

        double dt = tmax / (double)ssize;

        for(int n = 0; n < ssize - 1; n++) {
            double t = data.get(n).x;
            var y = data.get(n).y;

            List<Double> k1 = mul(dt, func.compute(t, y));
            List<Double> k2 = mul(2 * dt, func.compute(t + dt/2, add(y, div(k1,2))));
            List<Double> k3 = mul(2 * dt, func.compute(t + dt/2, add(y, div(k2,2))));
            List<Double> k4 = mul(dt, func.compute(t + dt, add(y, k3)));
            List<Double> y1 = add(y, div(add(k1, k2, k3, k4), 6));

            y1 = limiter.limit(y1);

            double t1 = dt * (double)(n + 1);
            data.add(new Data(t1, y1));
        }
        return data;
    }

    private static List<Double> mul(double scalar, List<Double> vect) {
        List<Double> res = new ArrayList<>(vect.size());
        for(var val : vect) {
            res.add(scalar * val);
        }
        return res;
    }

    private static List<Double> div(List<Double> vect, double scalar) {
        List<Double> res = new ArrayList<>(vect.size());
        for(var val : vect) {
            res.add(val / scalar);
        }
        return res;
    }

    @SafeVarargs
    private static List<Double> add(List<Double> ...vects) {
        assert(vects.length > 0);
        int size = vects[0].size();
        List<Double> res = new ArrayList<>(size);
        for(int i = 0; i < size; i++) {
            double val = 0.0;
            for (List<Double> vect : vects) {
                val += vect.get(i);
            }
            res.add(val);
        }
        return res;
    }
}
