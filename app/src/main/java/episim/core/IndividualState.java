package episim.core;

import java.util.UUID;

/**
 * L'état d'un individu de la simulation
 */
public class IndividualState {
    /**
     * L'identifiant de l'individu (unique)
     */
    public final UUID uuid;
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
    /**
     * La direction du mouvement de l'individu
     */
    public final double direction;

    public IndividualState(int compartmentId, double posX, double posY, double direction) {
        this(UUID.randomUUID(), compartmentId, posX, posY, direction);
    }
    public IndividualState(UUID uuid, int compartmentId, double posX, double posY, double direction) {
        this.uuid = uuid;
        this.compartmentId = compartmentId;
        this.posX = posX;
        this.posY = posY;
        this.direction = direction;
    }
}
