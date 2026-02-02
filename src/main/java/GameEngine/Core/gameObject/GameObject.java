package GameEngine.Core.gameObject;

import GameEngine.Core.GameEngine;
import GameEngine.Core.gameObject.collider.*;
import GameEngine.Core.graphics.Animation;
import GameEngine.Core.util.Vector2;
import GameEngine.Core.input.Input;
import GameEngine.Core.util.Camera;
import GameEngine.Core.util.Console.*;
import GameEngine.Tools.SpriteEditor.AnimationManager;
import GameEngine.Tools.SpriteEditor.SpriteManager;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * Base class for all game objects in the engine.
 * Provides transform, collision, sprite rendering, animation, and input handling.
 * Extend this class to create custom game entities.
 */
public abstract class GameObject {
    //<editor-fold desc="VARIABLES">
    /** The transform component containing position, scale, and rotation. */
    public Transform transform = new Transform();
    /** The collider component for collision detection. */
    public Collider2D collider = null;
    /** Whether this object is active and should be updated/drawn. */
    protected boolean active = true;
    /** The render order (higher values render on top). */
    public int renderOrder = 0;
    /** A tag for identifying and grouping objects. */
    public String tag = "GameObject";
    /** Reference to the object manager. */
    protected GameObjectManager objectManager;
    private Graphics2D g;

    // ===== SPRITE SYSTEM =====
    private Color[][] defaultSprite = null;
    private Color[][] currentFrame = null;
    private Float customSpriteScale = null;
    protected static SpriteManager spriteManager;

    // ===== SPRITE CACHE (per instance) =====
    private final Map<String, Color[][]> spriteCache = new HashMap<>();

    // ===== ANIMATION SYSTEM =====
    protected static AnimationManager animationManager;
    private final Map<String, Animation> animations = new HashMap<>();
    private Animation currentAnimation = null;
    private String currentAnimationName = null;

    // ===== DRAGGING =====
    private boolean dragging = false;
    private Vector2 dragStartOffset;
    private boolean wasMousePressedLastFrame = false;
    //</editor-fold>

    public GameObject() {
    }

    //<editor-fold desc="GETTERS/SETTERS">
    public static void setSpriteManager(SpriteManager manager) {
        spriteManager = manager;
    }
    public static void setAnimationManager(AnimationManager manager) {
        animationManager = manager;
    }
    public void setObjectManager(GameObjectManager objectManager) {
        this.objectManager = objectManager;
    }
    public GameObjectManager getObjectManager() {
        return objectManager;
    }
    public static void setEngine(GameEngine e) {
        engine = e;
    }
    protected static GameEngine engine;
    protected int getScreenWidth() {
        return engine.getScreenWidth();
    }
    protected int getScreenHeight() {
        return engine.getScreenHeight();
    }
    protected Vector2 getScreenSize() {
        return engine.getScreenSize();
    }
    protected int getFPS() {
        return engine.getFPS();
    }
    public Vector2 getCenterPosition() {
        return new Vector2(
                transform.position.x + transform.scale.x / 2,
                transform.position.y + transform.scale.y / 2
        );
    }
    public void setActive(boolean active) {
        this.active = active;
    }
    public boolean isActive() {
        return active;
    }
    protected Camera getCamera() {
        return engine.getCamera();
    }
    protected void setCameraFollowTarget() {
        engine.getCamera().setFollowTarget(this);
    }
    protected boolean isOutOfScreen() {
        return (
                transform.position.x < 0 ||
                        transform.position.y < 0 ||
                        transform.position.x > getScreenWidth() - transform.scale.x ||
                        transform.position.y > getScreenHeight() - transform.scale.y
        );
    }
    public boolean isDragging() {
        return dragging;
    }
    protected boolean wasMousePressedLastFrame() {
        return wasMousePressedLastFrame;
    }
    //</editor-fold>

    //<editor-fold desc="ABSTRACT METHODS">
    public void callDraw(Graphics2D g) {
        this.g = g;
        draw(g);
    }

    public abstract void init();
    public abstract void update(double deltaTime);
    public abstract void draw(Graphics2D g);
    public abstract void onCollision(GameObject collider);
    //</editor-fold>

    //<editor-fold desc="VISUAL">
    //<editor-fold desc="SRITE METHODS">

    /**
     * Loads a sprite and sets it as the default sprite (loads only once).
     *
     * @param spriteName The name of the sprite in the SpriteManager
     */
    protected void loadSprite(String spriteName) {
        if (spriteManager == null) {
            Console.log(ConsoleTag.ERROR, "SpriteManager not initialized!");
            return;
        }

        // Check if already in local cache
        if (!spriteCache.containsKey(spriteName)) {
            Color[][] sprite = spriteManager.loadSprite(spriteName);
            if (sprite == null) {
                Console.log(ConsoleTag.ERROR, "Sprite not found: " + spriteName);
                return;
            }
            spriteCache.put(spriteName, sprite);
        }

        // Set as default
        defaultSprite = spriteCache.get(spriteName);
        if (currentAnimation == null || !currentAnimation.isPlaying()) {
            currentFrame = defaultSprite;
        }
    }

    /**
     * Preloads a sprite into the cache without setting it as active.
     * Useful when you need multiple sprites.
     *
     * @param spriteName The name of the sprite to preload
     */
    protected void preloadSprite(String spriteName) {
        if (spriteManager == null) {
            Console.log(ConsoleTag.ERROR, "SpriteManager not initialized!");
            return;
        }

        if (!spriteCache.containsKey(spriteName)) {
            Color[][] sprite = spriteManager.loadSprite(spriteName);
            if (sprite != null) {
                spriteCache.put(spriteName, sprite);
            } else {
                Console.log(ConsoleTag.ERROR, "Failed to preload sprite: " + spriteName);
            }
        }
    }

    /**
     * Switches to a previously loaded sprite (must be loaded first).
     *
     * @param spriteName The name of the sprite to switch to
     */
    protected void switchSprite(String spriteName) {
        Color[][] sprite = spriteCache.get(spriteName);
        if (sprite == null) {
            Console.log(ConsoleTag.WARNING, "Sprite not in cache: " + spriteName + " - Use preloadSprite() or loadSprite()");
            return;
        }

        defaultSprite = sprite;
        if (currentAnimation == null || !currentAnimation.isPlaying()) {
            currentFrame = sprite;
        }
    }

    /**
     * Returns the name of the currently set sprite.
     *
     * @return The sprite name, or null if not found
     */
    protected String getCurrentSpriteName() {
        for (Map.Entry<String, Color[][]> entry : spriteCache.entrySet()) {
            if (entry.getValue() == defaultSprite) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Removes a sprite from the cache to save memory.
     *
     * @param spriteName The name of the sprite to unload
     */
    protected void unloadSprite(String spriteName) {
        spriteCache.remove(spriteName);
        Console.log(ConsoleTag.SPRITE, "Unloaded sprite: " + spriteName);
    }

    /**
     * Sets a sprite directly without using the SpriteManager.
     *
     * @param pixels The pixel data array for the sprite
     */
    protected void setSprite(Color[][] pixels) {
        this.defaultSprite = pixels;
        if (currentAnimation == null || !currentAnimation.isPlaying()) {
            this.currentFrame = pixels;
        }
    }

    /**
     * Clears the current sprite.
     */
    protected void clearSprite() {
        this.defaultSprite = null;
        this.currentFrame = null;
    }

    /**
     * Sets a custom scale for the sprite.
     *
     * @param scale The scale factor (minimum 0.1)
     */
    protected void setSpriteScale(float scale) {
        this.customSpriteScale = Math.max(0.1f, scale);
    }

    /**
     * Resets sprite scaling to automatic (fits to transform scale).
     */
    protected void setAutoSpriteScale() {
        this.customSpriteScale = null;
    }

    private float getEffectiveSpriteScale() {
        if (customSpriteScale != null) {
            return customSpriteScale;
        }

        if (currentFrame == null) {
            return 1.0f;
        }

        int spriteWidth = currentFrame.length;
        int spriteHeight = currentFrame[0].length;

        float scaleX = transform.scale.x / spriteWidth;
        float scaleY = transform.scale.y / spriteHeight;

        return Math.min(scaleX, scaleY);
    }

    /**
     * Returns the current effective sprite scale.
     *
     * @return The sprite scale factor
     */
    protected float getSpriteScale() {
        return getEffectiveSpriteScale();
    }

    /**
     * Checks if a sprite is currently set.
     *
     * @return true if a sprite is set, false otherwise
     */
    protected boolean hasSprite() {
        return currentFrame != null;
    }

    //</editor-fold>

    //<editor-fold desc="ANIMATION METHODS">

    /**
     * Loads and registers an animation.
     *
     * @param name The local name for the animation (e.g., "walk", "jump", "attack")
     * @param animationName The name in the AnimationManager
     * @param fps Frames per second
     * @param loop Whether the animation should loop
     */
    protected void loadAnimation(String name, String animationName, int fps, boolean loop) {
        if (animationManager == null) {
            Console.log(ConsoleTag.ERROR, "AnimationManager not initialized!");
            return;
        }

        List<Color[][]> frames = animationManager.loadAllFrames(animationName);
        if (frames.isEmpty()) {
            Console.log(ConsoleTag.ERROR, "Animation not found: " + animationName);
            return;
        }

        Animation anim = new Animation(frames, fps, loop);
        animations.put(name, anim);
        Console.log(ConsoleTag.ANIMATION, "Loaded animation: " + name + " (" + frames.size() + " frames)");
    }

    /**
     * Plays an animation by name.
     *
     * @param name The name of the animation to play
     */
    protected void playAnimation(String name) {
        Animation anim = animations.get(name);
        if (anim == null) {
            Console.log(ConsoleTag.WARNING, "Animation not loaded: " + name);
            return;
        }

        // If same animation is already playing, do nothing
        if (currentAnimation == anim && currentAnimation.isPlaying()) {
            return;
        }

        currentAnimation = anim;
        currentAnimationName = name;
        currentAnimation.playFromStart();
        Console.log(ConsoleTag.ANIMATION, "Playing animation: " + name);
    }

    /**
     * Force plays an animation, restarting even if already playing.
     *
     * @param name The name of the animation to play
     */
    protected void playAnimationForce(String name) {
        Animation anim = animations.get(name);
        if (anim == null) {
            Console.log(ConsoleTag.WARNING, "Animation not loaded: " + name);
            return;
        }

        currentAnimation = anim;
        currentAnimationName = name;
        currentAnimation.playFromStart();
        Console.log(ConsoleTag.ANIMATION, "Force playing animation: " + name);
    }

    /**
     * Stops the current animation and returns to the default sprite.
     */
    protected void stopAnimation() {
        if (currentAnimation != null) {
            currentAnimation.stop();
            currentFrame = defaultSprite;
            Console.log(ConsoleTag.ANIMATION, "Stopped animation: " + currentAnimationName);
        }
    }

    /**
     * Checks if any animation is currently playing.
     *
     * @return true if an animation is playing
     */
    protected boolean isAnimationPlaying() {
        return currentAnimation != null && currentAnimation.isPlaying();
    }

    /**
     * Checks if a specific animation is currently playing.
     *
     * @param name The animation name to check
     * @return true if the specified animation is playing
     */
    protected boolean isAnimationPlaying(String name) {
        return currentAnimationName != null &&
                currentAnimationName.equals(name) &&
                currentAnimation != null &&
                currentAnimation.isPlaying();
    }

    /**
     * Returns the name of the currently playing animation.
     *
     * @return The animation name, or null if none is playing
     */
    protected String getCurrentAnimationName() {
        return currentAnimationName;
    }

    /**
     * Updates the animation state. Must be called in update() for animations to work.
     *
     * @param deltaTime The time since last frame
     */
    protected void updateAnimation(float deltaTime) {
        if (currentAnimation != null) {
            currentAnimation.update(deltaTime);

            if (currentAnimation.isPlaying()) {
                // Animation playing - set current frame
                currentFrame = currentAnimation.getCurrentFrame();
            } else if (currentAnimation.isFinished()) {
                // Animation finished - return to default sprite
                currentFrame = defaultSprite;
                Console.log(ConsoleTag.ANIMATION, "Animation finished: " + currentAnimationName);
                currentAnimation = null;
                currentAnimationName = null;
            }
        }
    }

    //</editor-fold>

    //<editor-fold desc="DRAW METHODS">
    /**
     * Draws the GameObject as a sprite with rotation support.
     */
    protected void drawGOasSprite() {
        if (currentFrame == null) {
            // Fallback: Rotes Rechteck
            g.setColor(new Color(255, 0, 0, 100));
            g.fillRect(
                    (int)transform.position.x,
                    (int)transform.position.y,
                    (int)transform.scale.x,
                    (int)transform.scale.y
            );
            g.setColor(Color.RED);
            g.drawRect(
                    (int)transform.position.x,
                    (int)transform.position.y,
                    (int)transform.scale.x,
                    (int)transform.scale.y
            );
            return;
        }

        if (transform.rotation != 0) {
            drawSpriteRotated(transform.position.x, transform.position.y, transform.rotation);
        } else {
            drawSpriteUnrotated(transform.position.x, transform.position.y);
        }
    }

    private void drawSpriteUnrotated(float x, float y) {
        int spriteWidth = currentFrame.length;
        int spriteHeight = currentFrame[0].length;
        float pixelSize = getEffectiveSpriteScale();

        drawSpriteColors(x, y, spriteWidth, spriteHeight, pixelSize);
    }

    private void drawSpriteRotated(float x, float y, float rotation) {
        int spriteWidth = currentFrame.length;
        int spriteHeight = currentFrame[0].length;
        float pixelSize = getEffectiveSpriteScale();

        float totalWidth = spriteWidth * pixelSize;
        float totalHeight = spriteHeight * pixelSize;

        float centerX = x + totalWidth / 2;
        float centerY = y + totalHeight / 2;

        AffineTransform oldTransform = g.getTransform();
        g.rotate(Math.toRadians(rotation), centerX, centerY);

        drawSpriteColors(x, y, spriteWidth, spriteHeight, pixelSize);

        g.setTransform(oldTransform);
    }

    private void drawSpriteColors(float x, float y, int spriteWidth, int spriteHeight, float pixelSize) {
        for (int py = 0; py < spriteHeight; py++) {
            for (int px = 0; px < spriteWidth; px++) {
                Color color = currentFrame[px][py];
                if (color.getAlpha() == 0) continue;

                g.setColor(color);
                g.fillRect(
                        (int)(x + px * pixelSize),
                        (int)(y + py * pixelSize),
                        (int)Math.ceil(pixelSize),
                        (int)Math.ceil(pixelSize)
                );
            }
        }
    }

    /**
     * Draws the sprite at a specific position.
     *
     * @param x The x position
     * @param y The y position
     */
    protected void drawSpriteAt(float x, float y) {
        drawSpriteAt(x, y, transform.rotation);
    }

    /**
     * Draws the sprite at a specific position with rotation.
     *
     * @param x The x position
     * @param y The y position
     * @param rotation The rotation in degrees
     */
    protected void drawSpriteAt(float x, float y, float rotation) {
        if (currentFrame == null) return;

        if (rotation != 0) {
            float oldRot = transform.rotation;
            transform.rotation = rotation;
            drawSpriteRotated(x, y, rotation);
            transform.rotation = oldRot;
        } else {
            drawSpriteUnrotated(x, y);
        }
    }

    /**
     * Draws the GameObject as a centered sprite.
     */
    protected void drawGOasCenteredSprite() {
        if (currentFrame == null) {
            drawGOasSprite();
            return;
        }

        float pixelSize = getEffectiveSpriteScale();
        float spriteWidth = currentFrame.length * pixelSize;
        float spriteHeight = currentFrame[0].length * pixelSize;

        float centerX = transform.position.x + transform.scale.x / 2;
        float centerY = transform.position.y + transform.scale.y / 2;

        drawSpriteAt(centerX - spriteWidth / 2, centerY - spriteHeight / 2, transform.rotation);
    }

    /**
     * Draws the GameObject as a filled rectangle.
     *
     * @param color The fill color
     */
    protected void drawGOasFilledRect(Color color) {
        g.setColor(color);
        g.fillRect((int)transform.position.x, (int)transform.position.y, (int)transform.scale.x, (int)transform.scale.y);
    }

    /**
     * Draws the GameObject as a rounded rectangle.
     *
     * @param color The fill color
     * @param cornerRadius The radius of the corners
     */
    protected void drawGOasRoundedRect(Color color, int cornerRadius) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(color);

        RoundRectangle2D roundRect = new RoundRectangle2D.Float(
                transform.position.x,
                transform.position.y,
                transform.scale.x,
                transform.scale.y,
                cornerRadius * 2,
                cornerRadius * 2
        );

        g.fill(roundRect);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    }

    /**
     * Draws the GameObject as a filled circle/ellipse.
     *
     * @param color The fill color
     */
    protected void drawGOasFilledCircle(Color color) {
        g.setColor(color);
        g.fillOval((int)transform.position.x, (int)transform.position.y, (int)transform.scale.x, (int)transform.scale.y);
    }

    /**
     * Draws a grid over the screen by cell size.
     *
     * @param color The grid line color
     * @param cellSizeX The width of each cell
     * @param cellSizeY The height of each cell
     * @param thickness The line thickness
     */
    protected void drawGridbySize(Color color, int cellSizeX, int cellSizeY, int thickness) {
        g.setColor(color);
        g.setStroke(new BasicStroke(thickness));
        for (int i = 0; i < getScreenWidth(); i += cellSizeX) {
            g.drawLine(i,0,i,getScreenHeight());
        }
        for (int i = 0; i < getScreenHeight(); i += cellSizeY) {
            g.drawLine(0,i,getScreenWidth(),i);
        }
    }

    /**
     * Draws a grid over the screen by cell size.
     *
     * @param color The grid line color
     * @param cellSize The size of each cell as Vector2
     * @param thickness The line thickness
     */
    protected void drawGridbySize(Color color, Vector2 cellSize, int thickness) {
        g.setColor(color);
        g.setStroke(new BasicStroke(thickness));
        drawGrid(cellSize);
    }

    /**
     * Draws a grid over the screen by column/row count.
     *
     * @param color The grid line color
     * @param columns Number of columns
     * @param rows Number of rows
     * @param thickness The line thickness
     */
    protected void drawGridByCount(Color color, int columns, int rows, int thickness) {
        g.setColor(color);
        g.setStroke(new BasicStroke(thickness));
        drawGrid(new Vector2((float) getScreenWidth() /columns, (float) getScreenHeight() /rows));
    }
    private void drawGrid(Vector2 cellSize) {
        for (int i = 0; i < getScreenWidth(); i += cellSize.xToInt()) {
            g.drawLine(i,0,i,getScreenHeight());
        }
        for (int i = 0; i < getScreenHeight(); i += cellSize.yToInt()) {
            g.drawLine(0,i,getScreenWidth(),i);
        }
    }

    /**
     * Draws the collider bounds for debugging.
     */
    public void drawCollider() {
        Color color = Color.RED;
        float alpha = 0.3f;

        Composite original = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g.setColor(color);

        if (collider instanceof BoxCollider2D) {
            Rectangle bounds = ((BoxCollider2D) collider).getBounds();
            g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        } else if (collider instanceof CircleCollider2D c) {
            Vector2 center = c.getCenter();
            float radius = c.getRadius();
            g.fillOval((int)(center.x - radius), (int)(center.y - radius), (int)(radius*2), (int)(radius*2));
        }

        g.setComposite(original);
    }
    //</editor-fold>
    //</editor-fold>

    //<editor-fold desc="HELPER METHODS">
    /**
     * Shakes the camera with the specified intensity and duration.
     *
     * @param intensity The shake intensity
     * @param duration The shake duration in seconds
     */
    protected void shakeCamera(float intensity, float duration) {
        if (engine != null) {
            engine.getCamera().shake(intensity, duration);
        }
    }

    /**
     * Destroys this GameObject and removes it from the scene.
     */
    public void destroy() {
        objectManager.remove(this);
    }

    /**
     * Clears the camera follow target.
     */
    protected void clearCameraFollowTarget() {
        engine.getCamera().clearFollowTarget();
    }

    /**
     * Clamps the object's position to stay within screen bounds.
     */
    protected void clampPositionToScreen() {
        transform.position = transform.position.clamp(
                0, getScreenWidth() - transform.scale.x,
                0, getScreenHeight() - transform.scale.y
        );
    }

    /**
     * Checks if this object collides with another GameObject.
     *
     * @param gameObject The other GameObject to check collision with
     * @return true if the objects collide
     */
    public boolean collidesWith(GameObject gameObject) {
        if (collider != null && gameObject.collider != null) {
            return collider.intersects(gameObject.collider);
        }
        return gameObject.transform.position.x >= transform.position.x &&
                gameObject.transform.position.x <= transform.position.x + transform.scale.x &&
                gameObject.transform.position.y >= transform.position.y &&
                gameObject.transform.position.y <= transform.position.y + transform.scale.y;
    }

    /**
     * Checks if this object collides with a point.
     *
     * @param point The point to check collision with
     * @return true if the point is inside the object
     */
    public boolean collidesWith(Vector2 point) {
        if (collider != null) {
            return collider.collidesWithPoint(point);
        }
        return point.x >= transform.position.x &&
                point.x <= transform.position.x + transform.scale.x &&
                point.y >= transform.position.y &&
                point.y <= transform.position.y + transform.scale.y;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    /**
     * Checks if this object's class equals another class.
     *
     * @param other The class to compare with
     * @return true if classes are equal
     */
    public boolean equalsClassOf(Class<?> other) {
        return this.getClass().equals(other);
    }

    /**
     * Checks if this object's tag equals another object's tag.
     *
     * @param other The object to compare tags with
     * @return true if tags are equal
     */
    public boolean equalsTagOf(GameObject other) {
        return tag.equals(other.tag);
    }

    /**
     * Makes this object draggable with the specified mouse button.
     * Call this method in update() to enable dragging.
     *
     * @param mouseButton The mouse button to use for dragging
     */
    public void draggable(Input.MouseCode mouseButton) {
        boolean mousePressed = Input.getMouseButton(mouseButton);
        Vector2 mousePos = Input.getMousePosition();

        if (mousePressed && !wasMousePressedLastFrame && collidesWith(mousePos)) {
            dragStartOffset = mousePos.subtract(transform.position);
            dragging = true;
        }

        if (mousePressed && dragging) {
            transform.position = mousePos.subtract(dragStartOffset);
        }

        if (!mousePressed) {
            dragging = false;
        }

        wasMousePressedLastFrame = mousePressed;
    }

    /**
     * Checks if the mouse button was just released.
     *
     * @param mouseButton The mouse button to check
     * @return true if the button was just released
     */
    protected boolean wasJustReleased(Input.MouseCode mouseButton) {
        return !Input.getMouseButton(mouseButton) && wasMousePressedLastFrame;
    }
    //</editor-fold>

    //<editor-fold desc="EVENT METHODS">
    /**
     * Called when a key is pressed. Override to handle keyboard input.
     *
     * @param keyCode The key code of the pressed key
     */
    public void onKeyPressed(int keyCode) {}

    /**
     * Called when a key is released. Override to handle keyboard input.
     *
     * @param keyCode The key code of the released key
     */
    public void onKeyReleased(int keyCode) {}

    /**
     * Called when a mouse button is pressed. Override to handle mouse input.
     *
     * @param x The x position of the mouse
     * @param y The y position of the mouse
     * @param button The mouse button that was pressed
     */
    public void onMousePressed(int x, int y, int button) {}

    /**
     * Called when the window is closing.
     */
    public void onWindowClosing() {}

    /**
     * Called when the window is minimized.
     */
    public void onWindowMinimized() {}

    /**
     * Called when the window is restored from minimized state.
     */
    public void onWindowRestored() {}

    /**
     * Called when the window is resized.
     *
     * @param width The new window width
     * @param height The new window height
     */
    public void onWindowResized(int width, int height) {}
    //</editor-fold>
}