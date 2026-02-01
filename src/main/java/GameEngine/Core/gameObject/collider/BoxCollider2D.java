package GameEngine.Core.gameObject.collider;

import GameEngine.Core.gameObject.GameObject;
import GameEngine.Core.util.Vector2;

import java.awt.Rectangle;

public class BoxCollider2D extends Collider2D {
    private final Rectangle bounds;

    public BoxCollider2D(GameObject owner) {
        super(owner);
        bounds = new Rectangle(
                (int) owner.transform.position.x,
                (int) owner.transform.position.y,
                owner.transform.scale.xToInt(),
                owner.transform.scale.yToInt()
        );
        boundingRadius = (float) (0.5 * Math.sqrt(owner.transform.scale.x * owner.transform.scale.x + owner.transform.scale.y * owner.transform.scale.y));
    }
    @Override
    public boolean intersects(Collider2D other) {
        if (other instanceof BoxCollider2D) {
            updateBounds();
            ((BoxCollider2D) other).updateBounds();
            return bounds.intersects(((BoxCollider2D) other).bounds);
        }
        // CircleCollider2D prüfen
        if (other instanceof CircleCollider2D) {
            return ((CircleCollider2D) other).intersects(this);
        }
        return false;
    }
    @Override
    public boolean wouldCollide(Vector2 newPos) {
        return wouldCollide(newPos, null);
    }
    @Override
    public boolean wouldCollide(Vector2 newPos, String[] ignoreTags) {
        Rectangle future = new Rectangle(
                (int)newPos.x,
                (int)newPos.y,
                owner.transform.scale.xToInt(),
                owner.transform.scale.yToInt()
        );

        if (owner.getObjectManager() == null) return false;

        for (GameObject obj : owner.getObjectManager().getAll()) {
            if (obj == owner || obj.collider == null) continue;

            // Tags ignorieren
            if (ignoreTags != null) {
                boolean ignore = false;
                for (String tag : ignoreTags) {
                    if (obj.tag.equals(tag)) {
                        ignore = true;
                        break;
                    }
                }
                if (ignore) continue;
            }

            Collider2D otherCollider = obj.collider;
            if (otherCollider instanceof BoxCollider2D) {
                ((BoxCollider2D) otherCollider).updateBounds();
                if (future.intersects(((BoxCollider2D) otherCollider).bounds)) return true;
            } else if (otherCollider instanceof CircleCollider2D) {
                if (((CircleCollider2D) otherCollider).wouldCollideAtBox(future)) return true;
            }
        }
        return false;
    }
    @Override
    public boolean collidesWithPoint(Vector2 point) {
        updateBounds();
        return bounds.contains(point.xToInt(), point.yToInt());
    }
    public void updateBounds() {
        bounds.x = (int) owner.transform.position.x;
        bounds.y = (int) owner.transform.position.y;
        bounds.width = owner.transform.scale.xToInt();
        bounds.height = owner.transform.scale.yToInt();

        boundingRadius = (float) (0.5 * Math.sqrt(owner.transform.scale.x * owner.transform.scale.x + owner.transform.scale.y * owner.transform.scale.y));
    }
    public Rectangle getBounds() {
        updateBounds();
        return bounds;
    }
}
