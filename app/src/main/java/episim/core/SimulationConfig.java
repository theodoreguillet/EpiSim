package episim.core;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Configuration de la simulation d'épidémie
 */
public class SimulationConfig {
    /**
     * Les modèles épidémiques
     */
    private ArrayList<ModelConfig> models;

    public final String uuid = UUID.randomUUID().toString();

    SimulationConfig() {
        this.models = new ArrayList<>();
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
}
