package episim.core;

import java.io.*;
import java.util.*;

/**
 * Configuration de la simulation d'épidémie
 */
public class SimulationConfig {
    /**
     * Les modèles épidémiques
     */
    private ArrayList<ModelConfig> models;
    /**
     * La taille de la population
     */
    private int populationSize;
    /**
     * Proportion de la population initialement infectée
     */
    private double initialInfectious;

    SimulationConfig() {
        this.models = new ArrayList<>();
        this.populationSize = 0;
        this.initialInfectious = 0;
    }
    SimulationConfig(ArrayList<ModelConfig> models, int populationSize, double initialInfectious) {
        this.models = models;
        this.populationSize = populationSize;
        this.initialInfectious = initialInfectious;
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


    public static SimulationConfig getDefault() {
        return new SimulationConfig(
                new ArrayList<>(Arrays.asList(
                        new ModelConfig(
                                new ArrayList<>(Arrays.asList(
                                        new CompartmentConfig(0.5, "S", "#00ff00"),
                                        new CompartmentConfig(0.5, "I", "#ff0000")
                                )),
                                "SIR",
                                0,
                                0
                        ),
                        new ModelConfig(
                                new ArrayList<>(Arrays.asList(
                                        new CompartmentConfig(0.5, "S", "#00ff00"),
                                        new CompartmentConfig(0.5, "E", "#ff7f00"),
                                        new CompartmentConfig(0.5, "I", "#ff0000")
                                )),
                                "SEIR",
                                0,
                                0
                        ),
                        new ModelConfig(
                                new ArrayList<>(Arrays.asList(
                                        new CompartmentConfig(0.5, "S", "#00ff00"),
                                        new CompartmentConfig(0.5, "E", "#ff7f00"),
                                        new CompartmentConfig(0.5, "I", "#ff0000")
                                )),
                                "SEIR evolutive",
                                0.01,
                                0.01
                        )
                )),
                100,
                0.05
        );
    }
}
