package episim.core;

import java.io.*;
import java.util.*;

/**
 * Configuration de la simulation d'épidémie
 */
public class SimulationConfig implements Cloneable {
    /**
     * Les modèles épidémiques
     */
    private ArrayList<ModelConfig> models;
    /**
     * L'index du modèle sélectionné
     */
    private int selectedModelId;
    /**
     * La taille de la population
     */
    private int populationSize;
    /**
     * Proportion de la population initialement infectieuse
     */
    private double initialInfectious;
    /**
     * La distance minimum pour transmettre le virus
     */
    private double infectionRadius;
    /**
     * Activation d'une zone centrale où la population se concentre.
     */
    private boolean enableCenterZone;
    /**
     * Activation de zones multiples entre lesquelles les individus peuvent voyager
     */
    private boolean enableMultiZone;
    /**
     * Probabilité qu'un individu voyage vers/depuis la zone centrale par jour
     */
    private double centerZoneTravelProb;
    /**
     * Probabilité qu'un individu voyage vers une autre zone par jour
     */
    private double multiZoneTravelProb;
    /**
     * Règle de confinement
     */
    private SimulationRuleConfig confinement;
    /**
     * Règle de port du masque
     */
    private SimulationRuleConfig maskWear;
    /**
     * Règle de mise en quarantaine des individus infectieux
     */
    private SimulationRuleConfig quarantine;
    /**
     * Règle de distantiation sociale (par rapport à {@link #infectionRadius la distance distance de contamination})
     */
    private SimulationRuleConfig socialDistancing;


    SimulationConfig() {
        this.models = new ArrayList<>();
        this.selectedModelId = 0;
        this.populationSize = 0;
        this.initialInfectious = 0;
        this.infectionRadius = 0;
        this.enableCenterZone = false;
        this.enableMultiZone = false;
        this.centerZoneTravelProb = 0;
        this.multiZoneTravelProb = 0;
        this.confinement = new SimulationRuleConfig();
        this.maskWear = new SimulationRuleConfig();
        this.quarantine = new SimulationRuleConfig();
        this.socialDistancing = new SimulationRuleConfig();
    }
    SimulationConfig(ArrayList<ModelConfig> models, int populationSize, double initialInfectious, double infectionRadius,
                     boolean enableCenterZone, boolean enableMultiZone, double centerZoneTravelProb, double multiZoneTravelProb,
                     SimulationRuleConfig confinement, SimulationRuleConfig maskWear, SimulationRuleConfig quarantine,
                     SimulationRuleConfig socialDistancing
    ) {
        this.models = models;
        this.selectedModelId = 0;
        this.populationSize = populationSize;
        this.initialInfectious = initialInfectious;
        this.infectionRadius = infectionRadius;
        this.enableCenterZone = enableCenterZone;
        this.enableMultiZone = enableMultiZone;
        this.centerZoneTravelProb = centerZoneTravelProb;
        this.multiZoneTravelProb = multiZoneTravelProb;
        this.confinement = confinement;
        this.maskWear = maskWear;
        this.quarantine = quarantine;
        this.socialDistancing = socialDistancing;
    }

    public static SimulationConfig load(String path) {
        try {
            File file = new File(path);
            ObjectInputStream stream = new ObjectInputStream(new FileInputStream(file));
            return (SimulationConfig)stream.readObject();
        } catch (Exception err) {
            // TODO: Report error to user
            err.printStackTrace(System.err);
        }
        return null;
    }

    public void save(String path) {
        try {
            File file = new File(path);
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(this);
        } catch (Exception err) {
            // TODO: Report error to user
            err.printStackTrace(System.err);
        }
    }

    /**
     * Crée et retourne une copie profonde de cet objet
     */
    @Override
    public Object clone() {
        try {
            SimulationConfig o = (SimulationConfig)super.clone();
            o.models = new ArrayList<>(models.size());
            for(int i = 0; i < models.size(); i++) {
                o.models.add(i, (ModelConfig)models.get(i).clone());
            }
            return o;
        } catch(CloneNotSupportedException err) {
            // Ne doit pas arriver puisqu'on implémente Cloneable
            err.printStackTrace(System.err);
        }
        return null;
    }

    public List<ModelConfig> getModels() {
        return Collections.unmodifiableList(models);
    }

    public void addModel(ModelConfig model) {
        this.models.add(model);
    }

    public void clearModels() {
        this.models.clear();
    }

    public int getSelectedModelId() {
        return selectedModelId;
    }

    public ModelConfig getSelectedModel() {
        return models.get(selectedModelId);
    }

    public void setSelectedModelId(int selectedModelId) {
        this.selectedModelId = selectedModelId;
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

    public double getInitialInfectious() {
        return initialInfectious;
    }

    public void setInitialInfectious(double initialInfectious) {
        this.initialInfectious = initialInfectious;
    }

    public double getInfectionRadius() {
        return infectionRadius;
    }

    public void setInfectionRadius(double infectionRadius) {
        this.infectionRadius = infectionRadius;
    }

    public boolean isEnableCenterZone() {
        return enableCenterZone;
    }

    public void setEnableCenterZone(boolean enableCenterZone) {
        this.enableCenterZone = enableCenterZone;
    }

    public boolean isEnableMultiZone() {
        return enableMultiZone;
    }

    public void setEnableMultiZone(boolean enableMultiZone) {
        this.enableMultiZone = enableMultiZone;
    }

    public double getCenterZoneTravelProb() {
        return centerZoneTravelProb;
    }

    public void setCenterZoneTravelProb(double centerZoneTravelProb) {
        this.centerZoneTravelProb = centerZoneTravelProb;
    }

    public double getMultiZoneTravelProb() {
        return multiZoneTravelProb;
    }

    public void setMultiZoneTravelProb(double multiZoneTravelProb) {
        this.multiZoneTravelProb = multiZoneTravelProb;
    }

    public SimulationRuleConfig getConfinement() {
        return confinement;
    }

    public void setConfinement(SimulationRuleConfig confinement) {
        this.confinement = confinement;
    }

    public SimulationRuleConfig getMaskWear() {
        return maskWear;
    }

    public void setMaskWear(SimulationRuleConfig maskWear) {
        this.maskWear = maskWear;
    }

    public SimulationRuleConfig getQuarantine() {
        return quarantine;
    }

    public void setQuarantine(SimulationRuleConfig quarantine) {
        this.quarantine = quarantine;
    }

    public SimulationRuleConfig getSocialDistancing() {
        return socialDistancing;
    }

    public void setSocialDistancing(SimulationRuleConfig socialDistancing) {
        this.socialDistancing = socialDistancing;
    }

    public static SimulationConfig getDefault() {
        return new SimulationConfig(
                new ArrayList<>(Arrays.asList(
                        new ModelConfig(
                                new ArrayList<>(Arrays.asList(
                                        new CompartmentConfig(0.5, "S", "#00ff00"),
                                        new CompartmentConfig(0.5, "I", "#ff0000"),
                                        new CompartmentConfig(0, "R", "#0000ff")
                                )),
                                "SIR",
                                0,
                                0
                        ),
                        new ModelConfig(
                                new ArrayList<>(Arrays.asList(
                                        new CompartmentConfig(0.5, "S", "#00ff00"),
                                        new CompartmentConfig(0.5, "E", "#ff7f00"),
                                        new CompartmentConfig(0.5, "I", "#ff0000"),
                                        new CompartmentConfig(0, "R", "#0000ff")
                                )),
                                "SEIR",
                                0,
                                0
                        ),
                        new ModelConfig(
                                new ArrayList<>(Arrays.asList(
                                        new CompartmentConfig(0.5, "S", "#00ff00"),
                                        new CompartmentConfig(0.5, "E", "#ff7f00"),
                                        new CompartmentConfig(0.5, "I", "#ff0000"),
                                        new CompartmentConfig(0, "R", "#0000ff")
                                )),
                                "SEIR evolutive",
                                0.01,
                                0.01
                        )
                )),
                100,
                0.05,
                10,
                false,
                false,
                0,
                0,
                new SimulationRuleConfig(),
                new SimulationRuleConfig(),
                new SimulationRuleConfig(),
                new SimulationRuleConfig()
        );
    }
}
