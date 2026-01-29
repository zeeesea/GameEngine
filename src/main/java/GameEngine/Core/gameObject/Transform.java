package GameEngine.Core.gameObject;

import GameEngine.Core.util.Vector2;

import java.awt.*;

public class Transform {

    public Vector2 position;
    public float rotation;
    public Vector2 scale;

    public Transform() {
        this.position = new Vector2(0, 0);
        this.rotation = 0f;
        this.scale = new Vector2(1, 1);
    }

    public Transform(Vector2 position) {
        this.position = new Vector2(position);
        this.rotation = 0f;
        this.scale = new Vector2(1, 1);
    }

    public Transform(Rectangle bounds) {
        position = new Vector2(bounds.x, bounds.y);
        scale = new Vector2(bounds.width, bounds.height);
        rotation = 0f;
    }

    public Transform(Vector2 position, float rotation, Vector2 scale) {
        this.position = new Vector2(position);
        this.rotation = rotation;
        this.scale = new Vector2(scale);
    }

    public void translate(Vector2 delta) {
        position = position.add(delta);
    }

    public void rotate(float angle) {
        rotation += angle;
    }

    public void setPosition(float x, float y) {
        position.x = x;
        position.y = y;
    }

    public void setScale(float sx, float sy) {
        scale.x = sx;
        scale.y = sy;
    }
    public void scaleCentered(float factorX, float factorY) {
        float oldWidth = scale.x;
        float oldHeight = scale.y;

        float newWidth = scale.x * factorX;
        float newHeight = scale.y * factorY;

        // Position anpassen, damit die Mitte gleich bleibt
        position.x -= (newWidth - oldWidth) / 2f;
        position.y -= (newHeight - oldHeight) / 2f;

        scale.x = newWidth;
        scale.y = newHeight;
    }
    public void scaleCentered(Vector2 factor) {
        scaleCentered(factor.x, factor.y);
    }
    public void setScaleCentered(float sx, float sy) {
        float deltaX = sx - scale.x;
        float deltaY = sy - scale.y;

        // Position anpassen, damit die Mitte gleich bleibt
        position.x -= deltaX / 2f;
        position.y -= deltaY / 2f;

        // Scale setzen
        scale.x = sx;
        scale.y = sy;
    }

    public void setScaleCentered(Vector2 newScale) {
        setScaleCentered(newScale.x, newScale.y);
    }



    public Transform copy() {
        return new Transform(position.copy(), rotation, scale.copy());
    }

    @Override
    public String toString() {
        return "Transform(pos=" + position + ", rot=" + rotation + ", scale=" + scale + ")";
    }
}