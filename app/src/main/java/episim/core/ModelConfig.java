package episim.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Configuration d'un modèle épidémique
 */
public class ModelConfig implements Serializable, Cloneable {
    /**
     * Compartiments du modèle épidémique dans l'ordre
     * des états-transitions
     */
    private ArrayList<CompartmentConfig> compartments;
    /**
     * Nom du modèle épidémique
     */
    private String name;
    /**
     * Facteur de naissance
     */
    private double birth;
    /**
     * Facteur de décès (naturel)
     */
    private double death;

    public ModelConfig() {
        this.compartments = new ArrayList<>();
        this.name = "";
        this.birth = 0;
        this.death = 0;
    }
    public ModelConfig(ArrayList<CompartmentConfig> compartments, String name, double birth, double death) {
        this.compartments = compartments;
        this.name = name;
        this.birth = birth;
        this.death = death;
    }

    /**
     * Crée et retourne une copie profonde de cet objet
     */
    @Override
    public Object clone() {
        try {
            ModelConfig o = (ModelConfig)super.clone();
            o.compartments = new ArrayList<>(compartments.size());
            for(int i = 0; i < compartments.size(); i++) {
                o.compartments.add(i, (CompartmentConfig)compartments.get(i).clone());
            }
            return o;
        } catch(CloneNotSupportedException err) {
            // Ne doit pas arriver puisqu'on implémente Cloneable
            err.printStackTrace(System.err);
        }
        return null;
    }

    public List<CompartmentConfig> getCompartments() {
        return Collections.unmodifiableList(compartments);
    }

    public void addCompartment(CompartmentConfig compartment) {
        this.compartments.add(compartment);
    }

    public void addCompartment(int index, CompartmentConfig compartment) {
        this.compartments.add(index, compartment);
    }

    public void removeCompartment(int index) {
        this.compartments.remove(index);
    }

    public void clearCompartments() {
        this.compartments.clear();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBirth() {
        return birth;
    }

    public void setBirth(double birth) {
        this.birth = birth;
    }

    public double getDeath() {
        return death;
    }

    public void setDeath(double death) {
        this.death = death;
    }
}
