package GameEngine.Core.util;


import GameEngine.Core.GameEngine;
import GameEngine.Core.gameObject.GameObject;

import java.util.Random;

public class Camera {
    private Vector2 position;
    private Vector2 shakeOffset;
    private float shakeIntensity;
    private float shakeDuration;
    private float shakeTimer;
    private Random random;
    private GameObject followTarget;
    private GameEngine engine;

    public Camera(GameEngine engine) {
        position = new Vector2(0, 0);
        shakeOffset = new Vector2(0, 0);
        random = new Random();

        this.engine = engine;
    }

    public void shake(float intensity, float duration) {
        this.shakeIntensity = intensity;
        this.shakeDuration = duration;
        this.shakeTimer = 0;
    }

    public void update(double deltaTime) {
        if (shakeTimer < shakeDuration) {
            shakeTimer += deltaTime;

            // Zufälliger Offset basierend auf Intensität
            float progress = shakeTimer / shakeDuration;
            float currentIntensity = shakeIntensity * (1 - progress); // Fade out

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

    public void setFollowTarget(GameObject target) {
        followTarget = target;
    }
    public void clearFollowTarget() {
        followTarget = null;
    }

    public Vector2 getShakeOffset() {
        return shakeOffset;
    }
    public Vector2 getPosition() {
        return new Vector2(position);
    }

    public boolean isShaking() {
        return shakeTimer < shakeDuration;
    }
    public Vector2 getRenderOffset() {
        return position.add(shakeOffset);
    }
}