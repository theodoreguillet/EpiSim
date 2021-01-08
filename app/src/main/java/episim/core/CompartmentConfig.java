package episim.core;

import java.io.Serializable;

/**
 * Configuration d'un compartiment du modèle épidémique
 */
public class CompartmentConfig implements Serializable, Cloneable {
    /**
     * Le paramètre de transition
     */
    private double param;
    /**
     * Le nom donné au compartiment
     */
    private String name;
    /**
     * La couleur pour l'affichage
     */
    private String color;

    public CompartmentConfig() {
        this.param = 0.0;
        this.name = "";
        this.color = "#000000";
    }

    /**
     * Crée et retourne une copie profonde de cet objet
     */
    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch(CloneNotSupportedException err) {
            // Ne doit pas arriver puisqu'on implémente Cloneable
            err.printStackTrace(System.err);
        }
        return null;
    }

    public double getParam() {
        return param;
    }

    public void setParam(double param) {
        this.param = param;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
