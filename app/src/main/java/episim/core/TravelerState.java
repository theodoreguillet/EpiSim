package episim.core;

import java.util.UUID;

/**
 * L'état d'un individu voyagant d'un point à un autre du monde
 */
public class TravelerState extends IndividualState {
    /**
     * L'identifiant de la zone de départ
     * -1 pour la zone de quarantaine
     */
    public final int zoneId;
    /**
     * L'identifiant de la zone d'arrivée
     * -1 pour la zone de quarantaine
     */
    public final int dstZoneId;
    /**
     * La coordonée horizontale du point d'arrivée
     */
    public final double dstX;
    /**
     * La coordonée verticale du point d'arrivée
     */
    public final double dstY;
    /**
     * La proportion du trajet effectué.
     * La position de l'individu est interpolée entre 0 le point de départ et 1 le point d'arrivée.
     */
    public final double ratio;

    public TravelerState(UUID uuid, int compartmentId, int zoneId, double posX, double posY, double direction,
                         int dstZoneId, double dstX, double dstY, double ratio) {
        super(uuid, compartmentId, posX, posY, direction);
        this.zoneId = zoneId;
        this.dstZoneId = dstZoneId;
        this.dstX = dstX;
        this.dstY = dstY;
        this.ratio = ratio;
    }
}
