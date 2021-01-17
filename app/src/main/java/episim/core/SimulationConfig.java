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
     * La taille de la population (par zone)
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
     * Probabilité qu'un individu entre dans la zone centrale par jour
     */
    private double centerZoneEnterProb;
    /**
     * Probabilité qu'un individu sorte de la zone centrale par jour
     */
    private double centerZoneExitProb;
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
     * Facteur d'efficacité du masque
     */
    private double maskWearEfficacity;
    /**
     * Règle de mise en quarantaine des individus infectieux
     */
    private SimulationRuleConfig quarantine;
    /**
     * Règle de distantiation sociale (par rapport à {@link #infectionRadius la distance distance de contamination})
     */
    private SimulationRuleConfig socialDistancing;
    /**
     * Facteur de distantiation (par rapport à la distance de contamination)
     */
    private double socialDistancingFactor;
    /**
     * Règle de vaccination, chaque jour une proportion de la population respecte la règle et se fait vacciner,
     * passant dans l'état R
     */
    private SimulationRuleConfig vaccination;


    SimulationConfig() {
        this.models = new ArrayList<>();
        this.selectedModelId = 0;
        this.populationSize = 0;
        this.initialInfectious = 0;
        this.infectionRadius = 0;
        this.enableCenterZone = false;
        this.enableMultiZone = false;
        this.centerZoneEnterProb = 0;
        this.centerZoneExitProb = 0;
        this.multiZoneTravelProb = 0;
        this.confinement = new SimulationRuleConfig();
        this.maskWear = new SimulationRuleConfig();
        this.maskWearEfficacity = 0;
        this.quarantine = new SimulationRuleConfig();
        this.socialDistancing = new SimulationRuleConfig();
        this.socialDistancingFactor = 0;
        this.vaccination = new SimulationRuleConfig();
    }
    SimulationConfig(ArrayList<ModelConfig> models, int populationSize, double initialInfectious, double infectionRadius,
                     boolean enableCenterZone, boolean enableMultiZone, double centerZoneEnterProb, double centerZoneExitProb,
                     double multiZoneTravelProb, SimulationRuleConfig confinement, SimulationRuleConfig maskWear,
                     double maskWearEfficacity, SimulationRuleConfig quarantine, SimulationRuleConfig socialDistancing,
                     double socialDistancingFactor, SimulationRuleConfig vaccination
    ) {
        this.models = models;
        this.selectedModelId = 0;
        this.populationSize = populationSize;
        this.initialInfectious = initialInfectious;
        this.infectionRadius = infectionRadius;
        this.enableCenterZone = enableCenterZone;
        this.enableMultiZone = enableMultiZone;
        this.centerZoneEnterProb = centerZoneEnterProb;
        this.centerZoneExitProb = centerZoneExitProb;
        this.multiZoneTravelProb = multiZoneTravelProb;
        this.confinement = confinement;
        this.maskWear = maskWear;
        this.maskWearEfficacity = maskWearEfficacity;
        this.quarantine = quarantine;
        this.socialDistancing = socialDistancing;
        this.socialDistancingFactor = socialDistancingFactor;
        this.vaccination = vaccination;
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
        } catch (IOException err) {
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

    public double getCenterZoneEnterProb() {
        return centerZoneEnterProb;
    }

    public void setCenterZoneEnterProb(double centerZoneEnterProb) {
        this.centerZoneEnterProb = centerZoneEnterProb;
    }

    public double getCenterZoneExitProb() {
        return centerZoneExitProb;
    }

    public void setCenterZoneExitProb(double centerZoneExitProb) {
        this.centerZoneExitProb = centerZoneExitProb;
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

    public double getMaskWearEfficacity() {
        return maskWearEfficacity;
    }

    public void setMaskWearEfficacity(double maskWearEfficacity) {
        this.maskWearEfficacity = maskWearEfficacity;
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

    public double getSocialDistancingFactor() {
        return socialDistancingFactor;
    }

    public void setSocialDistancingFactor(double socialDistancingFactor) {
        this.socialDistancingFactor = socialDistancingFactor;
    }

    public SimulationRuleConfig getVaccination() {
        return vaccination;
    }

    public void setVaccination(SimulationRuleConfig vaccination) {
        this.vaccination = vaccination;
    }


    public static SimulationConfig getDefault() {
        return new SimulationConfig(
                new ArrayList<>(Arrays.asList(
                        new ModelConfig(
                                new ArrayList<>(Arrays.asList(
                                        new CompartmentConfig(0.06, "S", "#00ff00"),
                                        new CompartmentConfig(0.04, "I", "#ff0000"),
                                        new CompartmentConfig(0, "R", "#0000ff")
                                )),
                                "SIR",
                                0,
                                0
                        ),
                        new ModelConfig(
                                new ArrayList<>(Arrays.asList(
                                        new CompartmentConfig(0.06, "S", "#00ff00"),
                                        new CompartmentConfig(0.25, "E", "#ff7f00"),
                                        new CompartmentConfig(0.04, "I", "#ff0000"),
                                        new CompartmentConfig(0, "R", "#0000ff")
                                )),
                                "SEIR",
                                0,
                                0
                        ),
                        new ModelConfig(
                                new ArrayList<>(Arrays.asList(
                                        new CompartmentConfig(0.06, "S", "#00ff00"),
                                        new CompartmentConfig(0.25, "E", "#ff7f00"),
                                        new CompartmentConfig(0.04, "I", "#ff0000"),
                                        new CompartmentConfig(0, "R", "#0000ff")
                                )),
                                "SEIR evolutive",
                                0.01,
                                0.01
                        )
                )),
                200,
                0.01,
                10,
                false,
                false,
                0.24,
                0.1,
                0.03,
                new SimulationRuleConfig(),
                new SimulationRuleConfig(),
                0,
                new SimulationRuleConfig(),
                new SimulationRuleConfig(),
                0,
                new SimulationRuleConfig()
        );
    }
}
