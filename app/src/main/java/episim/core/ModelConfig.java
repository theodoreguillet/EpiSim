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

    public ModelConfig() {
        this.compartments = new ArrayList<>();
        this.name = "";
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
