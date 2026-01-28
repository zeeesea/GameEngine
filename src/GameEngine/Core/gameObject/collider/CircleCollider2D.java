package GameEngine.Core.gameObject.collider;

import GameEngine.Core.gameObject.GameObject;
import GameEngine.Core.util.Vector2;

public class CircleCollider2D extends Collider2D {

    private float radius;

    public CircleCollider2D(GameObject owner) {
        super(owner);
        this.radius = (owner.transform.scale.x + owner.transform.scale.y) / 4;
        boundingRadius = radius;
    }

    @Override
    public boolean intersects(Collider2D other) {
        if (other instanceof CircleCollider2D) {
            Vector2 centerA = getCenter();
            Vector2 centerB = ((CircleCollider2D) other).getCenter();
            float rSum = radius + ((CircleCollider2D) other).radius;
            return centerA.distance(centerB) < rSum;
        }
        if (other instanceof BoxCollider2D) {
            return wouldCollideAtBox(((BoxCollider2D) other).getBounds());
        }
        return false;
    }
    /** Prüft Kollision mit Box */
    public boolean wouldCollideAtBox(java.awt.Rectangle box) {
        Vector2 closest = new Vector2(
                clamp(getCenter().x, box.x, box.x + box.width),
                clamp(getCenter().y, box.y, box.y + box.height)
        );
        return getCenter().distance(closest) < radius;
    }
    @Override
    public boolean wouldCollide(Vector2 newPos) {
        return wouldCollide(newPos, null);
    }
    @Override
    public boolean wouldCollide(Vector2 newPos, String[] ignoreTags) {
        Vector2 oldPos = owner.transform.position.copy();
        owner.transform.position = newPos;
        boolean collision = false;
        if (owner.getGameObjectManager() != null) {
            for (GameObject obj : owner.getGameObjectManager().getAll()) {
                if (obj == owner || obj.collider == null) continue;
                if (ignoreTags != null) {
                    boolean skip = false;
                    for (String tag : ignoreTags) {
                        if (obj.tag.equals(tag)) { skip = true; break; }
                    }
                    if (skip) continue;
                }
                if (intersects(obj.collider)) {
                    collision = true;
                    break;
                }
            }
        }
        owner.transform.position = oldPos;
        return collision;
    }
    @Override
    public boolean collidesWithPoint(Vector2 point) {
        return getCenter().distance(point) <= radius;
    }

    @Override
    public void updateBounds() {
        radius = (owner.transform.scale.x + owner.transform.scale.y) / 4;
        boundingRadius = radius;
    }

    public Vector2 getCenter() {
        return new Vector2(
                owner.transform.position.x + owner.transform.scale.x / 2,
                owner.transform.position.y + owner.transform.scale.y / 2
        );
    }
    private float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }
    public float getRadius() {
        return radius;
    }
}