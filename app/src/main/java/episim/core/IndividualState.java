package episim.core;

/**
 * L'état d'un individu de la simulation
 */
public class IndividualState {
    /**
     * L'identifiant du compartiment de l'individu
     * -1 si l'individu est supprimé
     */
    public final int compartmentId;
    /**
     * La coordonée horizontale de la position de l'individu dans sa zone
     */
    public final double posX;
    /**
     * La coordonée verticale de la position de l'individu dans sa zone
     */
    public final double posY;

    public IndividualState(int compartmentId, double posX, double posY) {
        this.compartmentId = compartmentId;
        this.posX = posX;
        this.posY = posY;
    }
}
