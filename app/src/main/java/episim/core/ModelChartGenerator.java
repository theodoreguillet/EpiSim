package episim.core;

import episim.util.EulerSolver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ModelChartGenerator{

    public static class CompData{
        private final List<Double> x;
        private final List<Double> y;

        CompData(int size){
            this(new ArrayList<>(size), new ArrayList<>(size));
        }
        CompData(List<Double> x, List<Double> y){
            this.x = x;
            this.y = y;
        }

        public List<Double> getX() {
            return Collections.unmodifiableList(x);
        }

        public List<Double> getY() {
            return Collections.unmodifiableList(y);
        }
    }

    private final ModelConfig modelconfig;
    private final double initialInfectious;
    private final double initPopulationSize;
    private final double tmax;
    private final int npoints;
    private final int ncomps;
    private int susceptibleCompId;
    private int infectiousCompId;
    private int recoveredCompId;
    private double populationSize;

    /**
     * Génère la courbe épidémique (sans spatialisation) d'un modèle.
     * @param modelconfig La configuration du modèle
     * @param initialInfectious La proportion de la population initialement infectieuse
     * @param populationSize La taille de la population
     * @param tmax Le temps maximum de l'intervalle étudié [0, tmax]
     * @param npoints Le nombre de points de la courbe
     */
    public ModelChartGenerator(ModelConfig modelconfig, double initialInfectious, double populationSize,
                               double tmax, int npoints) {
        this.modelconfig = modelconfig;
        this.initialInfectious = initialInfectious;
        this.initPopulationSize = populationSize;
        this.tmax = tmax;
        this.npoints = npoints;
        ncomps = modelconfig.getCompartments().size();
    }

    /**
     * Génère la courbe épidémique (sans spatialisation) du modèle.
     * @return Pour chaque compartiment, la proportion de la population en fonction du temps
     * @throws RuntimeException Si le modèle est mal configuré (abscence de compartiment S, I ou R)
     */
    public ArrayList<CompData> generate() throws RuntimeException {
        ArrayList<Double> initialValues = new ArrayList<>(ncomps);

        susceptibleCompId = 0;
        infectiousCompId = -1;
        recoveredCompId = ncomps - 1;
        populationSize = initPopulationSize;

        for(int i = 0; i < ncomps; i++) {
            var comp = modelconfig.getCompartments().get(i);
            double value = 0;
            if(i == susceptibleCompId) {
                value = initPopulationSize - initialInfectious * initPopulationSize;
            } else if(comp.getName().equals(CompartmentConfig.INFECTIOUS)) {
                value = initialInfectious * initPopulationSize;
                infectiousCompId = i;
            }
            initialValues.add(value);
        }

        if(infectiousCompId == -1 || infectiousCompId == susceptibleCompId || infectiousCompId == recoveredCompId) {
            throw new RuntimeException("Bad model config");
        }

        // On utilise la méthode d'euler explicite car elle offre la meilleur stabilité
        var res = EulerSolver.solve(tmax, npoints, initialValues, this::derivativeFunc, this::valueLimiter);

        ArrayList<CompData> datas = new ArrayList<>(ncomps);
        for(int i = 0; i < ncomps; i++) {
            datas.add(new CompData(npoints));
        }
        for(var point : res) {
            for(int i = 0; i < ncomps; i++) {
                datas.get(i).x.add(point.getX());
                datas.get(i).y.add(point.getY().get(i));
            }
        }

        return datas;
    }

    private List<Double> derivativeFunc(double t, List<Double> y) {
        double compOutPop = 0;
        ArrayList<Double> res = new ArrayList<>(ncomps);

        for(int i = 0; i < ncomps; i++) {
            var comp = modelconfig.getCompartments().get(i);
            double compPop = y.get(i);

            double death = -modelconfig.getDeath() * compPop;
            double birth = 0;
            double fval = compOutPop + death;

            if(i == susceptibleCompId) { // Susceptible
                birth = modelconfig.getBirth() * populationSize;
                fval += birth;

                double infecPop = y.get(infectiousCompId);
                compOutPop = comp.getParam() * compPop * infecPop;
            } else {
                compOutPop = comp.getParam() * compPop;
            }
            fval -= compOutPop;
            populationSize += birth - death;

            res.add(fval);
        }

        return res;
    }

    private List<Double> valueLimiter(List<Double> y) {
        ArrayList<Double> res = new ArrayList<>(y.size());
        for(double val : y) {
            res.add(Math.max(0, Math.min(val, populationSize)));
        }
        return res;
    }
}
