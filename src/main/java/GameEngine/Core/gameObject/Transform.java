package GameEngine.Core.gameObject;

import GameEngine.Core.util.Vector2;

import java.awt.*;

/**
 * Represents the position, rotation, and scale of a GameObject.
 * Used to define the spatial properties of objects in the game world.
 */
public class Transform {

    /** The position of the object in 2D space. */
    public Vector2 position;
    /** The rotation angle in degrees. */
    public float rotation;
    /** The scale (width and height) of the object. */
    public Vector2 scale;

    /**
     * Creates a default Transform at origin with no rotation and unit scale.
     */
    public Transform() {
        this.position = new Vector2(0, 0);
        this.rotation = 0f;
        this.scale = new Vector2(1, 1);
    }

    /**
     * Creates a Transform at the specified position with no rotation and unit scale.
     *
     * @param position The initial position
     */
    public Transform(Vector2 position) {
        this.position = new Vector2(position);
        this.rotation = 0f;
        this.scale = new Vector2(1, 1);
    }

    /**
     * Creates a Transform from a Rectangle's bounds.
     *
     * @param bounds The rectangle defining position and size
     */
    public Transform(Rectangle bounds) {
        position = new Vector2(bounds.x, bounds.y);
        scale = new Vector2(bounds.width, bounds.height);
        rotation = 0f;
    }

    /**
     * Creates a Transform with specified position, rotation, and scale.
     *
     * @param position The initial position
     * @param rotation The initial rotation in degrees
     * @param scale The initial scale
     */
    public Transform(Vector2 position, float rotation, Vector2 scale) {
        this.position = new Vector2(position);
        this.rotation = rotation;
        this.scale = new Vector2(scale);
    }

    /**
     * Moves the transform by the given delta.
     *
     * @param delta The amount to move
     */
    public void translate(Vector2 delta) {
        position = position.add(delta);
    }

    /**
     * Rotates the transform by the given angle.
     *
     * @param angle The angle to rotate in degrees
     */
    public void rotate(float angle) {
        rotation += angle;
    }

    /**
     * Sets the position directly.
     *
     * @param x The x coordinate
     * @param y The y coordinate
     */
    public void setPosition(float x, float y) {
        position.x = x;
        position.y = y;
    }

    /**
     * Sets the scale directly.
     *
     * @param sx The x scale (width)
     * @param sy The y scale (height)
     */
    public void setScale(float sx, float sy) {
        scale.x = sx;
        scale.y = sy;
    }

    /**
     * Scales the transform while keeping the center position fixed.
     *
     * @param factorX The x scale factor
     * @param factorY The y scale factor
     */
    public void scaleCentered(float factorX, float factorY) {
        float oldWidth = scale.x;
        float oldHeight = scale.y;

        float newWidth = scale.x * factorX;
        float newHeight = scale.y * factorY;

        // Adjust position to keep center fixed
        position.x -= (newWidth - oldWidth) / 2f;
        position.y -= (newHeight - oldHeight) / 2f;

        scale.x = newWidth;
        scale.y = newHeight;
    }

    /**
     * Scales the transform while keeping the center position fixed.
     *
     * @param factor The scale factor as Vector2
     */
    public void scaleCentered(Vector2 factor) {
        scaleCentered(factor.x, factor.y);
    }

    /**
     * Sets the scale while keeping the center position fixed.
     *
     * @param sx The new x scale (width)
     * @param sy The new y scale (height)
     */
    public void setScaleCentered(float sx, float sy) {
        float deltaX = sx - scale.x;
        float deltaY = sy - scale.y;

        // Adjust position to keep center fixed
        position.x -= deltaX / 2f;
        position.y -= deltaY / 2f;

        scale.x = sx;
        scale.y = sy;
    }

    /**
     * Sets the scale while keeping the center position fixed.
     *
     * @param newScale The new scale as Vector2
     */
    public void setScaleCentered(Vector2 newScale) {
        setScaleCentered(newScale.x, newScale.y);
    }

    /**
     * Creates a copy of this transform.
     *
     * @return A new Transform with the same values
     */
    public Transform copy() {
        return new Transform(position.copy(), rotation, scale.copy());
    }

    @Override
    public String toString() {
        return "Transform(pos=" + position + ", rot=" + rotation + ", scale=" + scale + ")";
    }

    /**
     * Sets the position so that the given point becomes the center.
     *
     * @param v The center position
     */
    public void setPositionCentered(Vector2 v) {
        position = new Vector2(
                v.x - scale.x / 2,
                v.y - scale.y / 2
        );
    }

    /**
     * Sets the position so that the given point becomes the center.
     *
     * @param x The x coordinate of the center
     * @param y The y coordinate of the center
     */
    public void setPositionCentered(float x, float y) {
        setPositionCentered(new Vector2(x, y));
    }

    /**
     * Returns the center position of the transform.
     *
     * @return The center position as Vector2
     */
    public Vector2 getCenteredPosition() {
        return new Vector2(position.x + scale.x / 2, position.y + scale.y / 2);
    }
}