package GameEngine.Core.gameObject.collider;

import GameEngine.Core.gameObject.GameObject;
import GameEngine.Core.util.Vector2;

public abstract class Collider2D {
    protected final GameObject owner;
    protected float boundingRadius;

    public Collider2D(GameObject owner) {
        this.owner = owner;
    }

    /** Prüft Kollision mit anderem Collider */
    public abstract boolean intersects(Collider2D other);

    /** Prüft, ob Bewegung an newPos zu Kollision führen würde */
    public abstract boolean wouldCollide(Vector2 newPos);
    public abstract boolean wouldCollide(Vector2 newPos, String[] ignoreTags);

    /** Prüft, ob ein Punkt innerhalb des Colliders liegt */
    public abstract boolean collidesWithPoint(Vector2 point);

    public GameObject getOwner() {
        return owner;
    }
    public float getBoundingRadius() {
        return boundingRadius;
    }
    public abstract void updateBounds();
}
