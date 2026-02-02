package GameEngine.Core.util;


import GameEngine.Core.GameEngine;
import GameEngine.Core.gameObject.GameObject;

import java.util.Random;

/**
 * Camera class for controlling the viewport and visual effects.
 * Supports position tracking, screen shake effects, and following game objects.
 */
public class Camera {
    private Vector2 position;
    private Vector2 shakeOffset;
    private float shakeIntensity;
    private float shakeDuration;
    private float shakeTimer;
    private Random random;
    private GameObject followTarget;
    private GameEngine engine;

    /**
     * Creates a new Camera instance.
     *
     * @param engine The game engine reference
     */
    public Camera(GameEngine engine) {
        position = new Vector2(0, 0);
        shakeOffset = new Vector2(0, 0);
        random = new Random();

        this.engine = engine;
    }

    /**
     * Triggers a screen shake effect.
     *
     * @param intensity The shake intensity (how much the screen moves)
     * @param duration The duration of the shake in seconds
     */
    public void shake(float intensity, float duration) {
        this.shakeIntensity = intensity;
        this.shakeDuration = duration;
        this.shakeTimer = 0;
    }

    /**
     * Updates the camera state. Called internally each frame.
     *
     * @param deltaTime The time since last frame
     */
    public void update(double deltaTime) {
        if (shakeTimer < shakeDuration) {
            shakeTimer += deltaTime;

            // Random offset based on intensity with fade out
            float progress = shakeTimer / shakeDuration;
            float currentIntensity = shakeIntensity * (1 - progress);

            shakeOffset.x = (random.nextFloat() - 0.5f) * 2 * currentIntensity;
            shakeOffset.y = (random.nextFloat() - 0.5f) * 2 * currentIntensity;
        } else {
            shakeOffset.x = 0;
            shakeOffset.y = 0;
        }

        if (followTarget != null) {
             position = followTarget.transform.position.multiply(-1).add(new Vector2(engine.getScreenWidth()/2, engine.getScreenHeight()/2));
        }
    }

    /**
     * Sets a GameObject for the camera to follow.
     *
     * @param target The target to follow
     */
    public void setFollowTarget(GameObject target) {
        followTarget = target;
    }

    /**
     * Clears the follow target, making the camera static.
     */
    public void clearFollowTarget() {
        followTarget = null;
    }

    /**
     * Returns the current shake offset.
     *
     * @return The shake offset as Vector2
     */
    public Vector2 getShakeOffset() {
        return shakeOffset;
    }

    /**
     * Returns the camera position.
     *
     * @return The position as Vector2
     */
    public Vector2 getPosition() {
        return new Vector2(position);
    }

    /**
     * Checks if the camera is currently shaking.
     *
     * @return true if shaking
     */
    public boolean isShaking() {
        return shakeTimer < shakeDuration;
    }

    /**
     * Returns the total render offset (position + shake).
     *
     * @return The render offset as Vector2
     */
    public Vector2 getRenderOffset() {
        return position.add(shakeOffset);
    }
}