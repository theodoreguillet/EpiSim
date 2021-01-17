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
    private final double infectionFactor; // Change le facteur de transition S ->
    private int susceptibleCompId;
    private int infectiousCompId;
    private int recoveredCompId;
    private double populationSize;
    private double populationDiff;

    /**
     * Génère la courbe épidémique (sans spatialisation) d'un modèle.
     * @param modelconfig La configuration du modèle
     * @param initialInfectious La proportion de la population initialement infectieuse
     * @param populationSize La taille de la population
     * @param infectionRadius La distance minimum de transmission du virus (pour la pseudo spatialisation)
     * @param pseudoSpatialisation Simule la spatialisation
     * @param tmax Le temps maximum de l'intervalle étudié [0, tmax]
     * @param npoints Le nombre de points de la courbe
     */
    public ModelChartGenerator(ModelConfig modelconfig, double initialInfectious, double populationSize, double infectionRadius,
                               boolean pseudoSpatialisation, double tmax, int npoints) {
        this.modelconfig = modelconfig;
        this.initialInfectious = initialInfectious;
        this.initPopulationSize = populationSize;
        this.tmax = tmax;
        this.npoints = npoints;
        ncomps = modelconfig.getCompartments().size();

        if(pseudoSpatialisation) {
            // On simule grossièrement la spatialisation
            infectionFactor = Math.PI * infectionRadius * infectionRadius
                        / ((double)Simulation.ZONE_SIZE * (double) Simulation.ZONE_SIZE);
        } else {
            infectionFactor = 1.0;
        }
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

        boolean evolutive = (modelconfig.getDeath() - modelconfig.getBirth() != 0);

        populationDiff = 0;
        if(evolutive) {
            populationSize = 0;
            for(int i = 0; i < ncomps; i++) {
                populationSize += y.get(i);
            }
        } else {
            populationSize = initPopulationSize;
        }

        for(int i = 0; i < ncomps; i++) {
            var comp = modelconfig.getCompartments().get(i);
            double compPop = y.get(i);

            double death = evolutive ? -modelconfig.getDeath() * compPop : 0;
            double birth = 0;
            double fval = compOutPop + death;

            if(i == susceptibleCompId) { // Susceptible
                birth = evolutive ? modelconfig.getBirth() * populationSize : 0;
                fval += birth;

                double infecPop = y.get(infectiousCompId);
                compOutPop = comp.getParam() * compPop * infecPop * infectionFactor;
            } else {
                compOutPop = comp.getParam() * compPop;
            }
            fval -= compOutPop;
            populationDiff += birth - death;

            res.add(fval);
        }

        return res;
    }

    private List<Double> valueLimiter(double dt, List<Double> y) {
        ArrayList<Double> res = new ArrayList<>(y.size());
        for(double val : y) {
            res.add(Math.max(0, Math.min(val, populationSize + populationDiff * dt)));
        }
        return res;
    }
}
