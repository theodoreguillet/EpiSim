package episim.core;

import java.util.UUID;

/**
 * L'état d'un individu de la simulation
 */
public class IndividualState {
    public enum Respect {
        FALSE,
        TRUE,
        UNSET
    }
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
    /**
     * Définit si l'individu respecte le confinement
     */
    public final Respect confinement;
    /**
     * Définit si l'individu respecte le port du masque
     */
    public final Respect maskWear;
    /**
     * Définit si l'individu respecte la quarantaine
     */
    public final Respect quarantine;
    /**
     * Définit si l'individu respecte la distanciation sociale
     */
    public final Respect socialDistancing;
    /**
     * Définit si l'individu respecte la vaccination
     */
    public final Respect vaccination;

    public IndividualState(int compartmentId, double posX, double posY, double direction) {
        this(compartmentId, posX, posY, direction, Respect.UNSET, Respect.UNSET, Respect.UNSET, Respect.UNSET, Respect.UNSET);
    }
    public IndividualState(int compartmentId, double posX, double posY, double direction,
                           Respect confinement, Respect maskWear, Respect quarantine,
                           Respect socialDistancing, Respect vaccination
    ) {
        this(UUID.randomUUID(), compartmentId, posX, posY, direction, confinement,
                maskWear, quarantine, socialDistancing, vaccination);
    }
    public IndividualState(UUID uuid, int compartmentId, double posX, double posY, double direction,
                           Respect confinement, Respect maskWear, Respect quarantine,
                           Respect socialDistancing, Respect vaccination
    ) {
        this.uuid = uuid;
        this.compartmentId = compartmentId;
        this.posX = posX;
        this.posY = posY;
        this.direction = direction;
        this.confinement = confinement;
        this.maskWear = maskWear;
        this.quarantine = quarantine;
        this.socialDistancing = socialDistancing;
        this.vaccination = vaccination;
    }
}
