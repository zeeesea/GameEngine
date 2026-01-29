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
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public abstract class GameObject {
    public Transform transform = new Transform();
    public Collider2D collider = null;
    public boolean active = true;
    public int renderOrder = 0;
    public String tag = "GameObject";
    protected GameObjectManager gameObjectManager;
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

    public GameObject() {
        init();
    }

    public static void setSpriteManager(SpriteManager manager) {
        spriteManager = manager;
    }

    public static void setAnimationManager(AnimationManager manager) {
        animationManager = manager;
    }

    public void setGameObjectManager(GameObjectManager gameObjectManager) {
        this.gameObjectManager = gameObjectManager;
    }

    public GameObjectManager getGameObjectManager() {
        return gameObjectManager;
    }

    protected static GameEngine engine;

    public static void setEngine(GameEngine e) {
        engine = e;
    }

    public void callDraw(Graphics2D g) {
        this.g = g;
        draw(g);
    }

    public abstract void init();
    public abstract void update(double deltaTime);
    public abstract void draw(Graphics2D g);
    public abstract void onCollision(GameObject collider);

    public void destroy() {
        gameObjectManager.remove(this);
    }

    // ===== SPRITE METHODS =====

    /**
     * Lädt ein Sprite und setzt es als Default (nur einmal laden)
     * @param spriteName Name des Sprites im SpriteManager
     */
    protected void loadSprite(String spriteName) {
        if (spriteManager == null) {
            Console.log(ConsoleTag.ERROR, "SpriteManager nicht initialisiert!");
            return;
        }

        // Check ob schon im lokalen Cache
        if (!spriteCache.containsKey(spriteName)) {
            Color[][] sprite = spriteManager.loadSprite(spriteName);
            if (sprite == null) {
                Console.log(ConsoleTag.ERROR, "Sprite nicht gefunden: " + spriteName);
                return;
            }
            spriteCache.put(spriteName, sprite);
        }

        // Setze als Default
        defaultSprite = spriteCache.get(spriteName);
        if (currentAnimation == null || !currentAnimation.isPlaying()) {
            currentFrame = defaultSprite;
        }
    }

    /**
     * Lädt ein Sprite in den Cache (ohne es zu setzen)
     * Nützlich wenn du mehrere Sprites brauchst
     */
    protected void preloadSprite(String spriteName) {
        if (spriteManager == null) {
            Console.log(ConsoleTag.ERROR, "SpriteManager nicht initialisiert!");
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
     * Wechselt zum geladenen Sprite (muss vorher geladen sein!)
     * @param spriteName Name des Sprites
     */
    protected void switchSprite(String spriteName) {
        Color[][] sprite = spriteCache.get(spriteName);
        if (sprite == null) {
            Console.log(ConsoleTag.WARNING, "Sprite nicht im Cache: " + spriteName + " - Nutze preloadSprite() oder loadSprite()");
            return;
        }

        defaultSprite = sprite;
        if (currentAnimation == null || !currentAnimation.isPlaying()) {
            currentFrame = sprite;
        }
    }

    /**
     * Gibt das aktuell gesetzte Sprite zurück
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
     * Entfernt Sprite aus dem Cache (Memory sparen)
     */
    protected void unloadSprite(String spriteName) {
        spriteCache.remove(spriteName);
        Console.log(ConsoleTag.SPRITE, "Unloaded sprite: " + spriteName);
    }

    /**
     * Setzt ein Sprite direkt (ohne SpriteManager)
     */
    protected void setSprite(Color[][] pixels) {
        this.defaultSprite = pixels;
        if (currentAnimation == null || !currentAnimation.isPlaying()) {
            this.currentFrame = pixels;
        }
    }

    protected void clearSprite() {
        this.defaultSprite = null;
        this.currentFrame = null;
    }

    protected void setSpriteScale(float scale) {
        this.customSpriteScale = Math.max(0.1f, scale);
    }

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

    protected float getSpriteScale() {
        return getEffectiveSpriteScale();
    }

    protected boolean hasSprite() {
        return currentFrame != null;
    }

    // ===== ANIMATION METHODS =====

    /**
     * Lädt und registriert eine Animation
     * @param name Name der Animation (z.B. "walk", "jump", "attack")
     * @param animationName Name im AnimationManager
     * @param fps Frames pro Sekunde
     * @param loop Soll die Animation loopen?
     */
    protected void loadAnimation(String name, String animationName, int fps, boolean loop) {
        if (animationManager == null) {
            Console.log(ConsoleTag.ERROR, "AnimationManager nicht initialisiert!");
            return;
        }

        List<Color[][]> frames = animationManager.loadAllFrames(animationName);
        if (frames.isEmpty()) {
            Console.log(ConsoleTag.ERROR, "Animation nicht gefunden: " + animationName);
            return;
        }

        Animation anim = new Animation(frames, fps, loop);
        animations.put(name, anim);
        Console.log(ConsoleTag.ANIMATION, "Loaded animation: " + name + " (" + frames.size() + " frames)");
    }

    /**
     * Spielt eine Animation ab
     * @param name Name der Animation
     */
    protected void playAnimation(String name) {
        Animation anim = animations.get(name);
        if (anim == null) {
            Console.log(ConsoleTag.WARNING, "Animation nicht geladen: " + name);
            return;
        }

        // Wenn gleiche Animation bereits läuft, nichts tun
        if (currentAnimation == anim && currentAnimation.isPlaying()) {
            return;
        }

        currentAnimation = anim;
        currentAnimationName = name;
        currentAnimation.playFromStart();
        Console.log(ConsoleTag.ANIMATION, "Playing animation: " + name);
    }

    /**
     * Spielt eine Animation ab, auch wenn sie bereits läuft (startet von vorne)
     */
    protected void playAnimationForce(String name) {
        Animation anim = animations.get(name);
        if (anim == null) {
            Console.log(ConsoleTag.WARNING, "Animation nicht geladen: " + name);
            return;
        }

        currentAnimation = anim;
        currentAnimationName = name;
        currentAnimation.playFromStart();
        Console.log(ConsoleTag.ANIMATION, "Force playing animation: " + name);
    }

    /**
     * Stoppt die aktuelle Animation und kehrt zum Default-Sprite zurück
     */
    protected void stopAnimation() {
        if (currentAnimation != null) {
            currentAnimation.stop();
            currentFrame = defaultSprite;
            Console.log(ConsoleTag.ANIMATION, "Stopped animation: " + currentAnimationName);
        }
    }

    /**
     * Prüft ob eine Animation läuft
     */
    protected boolean isAnimationPlaying() {
        return currentAnimation != null && currentAnimation.isPlaying();
    }

    /**
     * Prüft ob eine bestimmte Animation läuft
     */
    protected boolean isAnimationPlaying(String name) {
        return currentAnimationName != null &&
                currentAnimationName.equals(name) &&
                currentAnimation != null &&
                currentAnimation.isPlaying();
    }

    /**
     * Gibt den Namen der aktuell laufenden Animation zurück
     */
    protected String getCurrentAnimationName() {
        return currentAnimationName;
    }

    /**
     * Update der Animation (MUSS in update() aufgerufen werden!)
     */
    protected void updateAnimation(float deltaTime) {
        if (currentAnimation != null) {
            currentAnimation.update(deltaTime);

            if (currentAnimation.isPlaying()) {
                // Animation läuft - aktuelles Frame setzen
                currentFrame = currentAnimation.getCurrentFrame();
            } else if (currentAnimation.isFinished()) {
                // Animation fertig - zurück zum Default-Sprite
                currentFrame = defaultSprite;
                Console.log(ConsoleTag.ANIMATION, "Animation finished: " + currentAnimationName);
                currentAnimation = null;
                currentAnimationName = null;
            }
        }
    }

    /**
     * Zeichnet das GameObject als Sprite mit Rotation
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

    protected void drawSpriteAt(float x, float y) {
        drawSpriteAt(x, y, transform.rotation);
    }

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

    // Helper Methods
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
    protected void shakeCamera(float intensity, float duration) {
        if (engine != null) {
            engine.getCamera().shake(intensity, duration);
        }
    }
    protected Camera getCamera() {
        return engine.getCamera();
    }
    protected void setCameraFollowTarget() {
        engine.getCamera().setFollowTarget(this);
    }
    protected void clearCameraFollowTarget() {
        engine.getCamera().clearFollowTarget();
    }
    protected boolean isOutOfScreen() {
        return (
                        transform.position.x < 0 ||
                        transform.position.y < 0 ||
                        transform.position.x > getScreenWidth() - transform.scale.x ||
                        transform.position.y > getScreenHeight() - transform.scale.y
                );
    }
    protected void clampPositionToScreen() {
        transform.position = transform.position.clamp(
                0, getScreenWidth() - transform.scale.x,
                0, getScreenHeight() - transform.scale.y
        );
    }
    public boolean collidesWith(GameObject gameObject) {
        if (collider != null && gameObject.collider != null) {
            return collider.intersects(gameObject.collider);
        }
        return gameObject.transform.position.x >= transform.position.x &&
                gameObject.transform.position.x <= transform.position.x + transform.scale.x &&
                gameObject.transform.position.y >= transform.position.y &&
                gameObject.transform.position.y <= transform.position.y + transform.scale.y;
    }
    public boolean collidesWith(Vector2 point) {
        if (collider != null) {
            return collider.collidesWithPoint(point);
        }
        return point.x >= transform.position.x &&
                point.x <= transform.position.x + transform.scale.x &&
                point.y >= transform.position.y &&
                point.y <= transform.position.y + transform.scale.y;
    }

    //Drawing Methods
    protected void drawGOasFilledRect(Color color) {
        g.setColor(color);
        g.fillRect((int)transform.position.x, (int)transform.position.y, (int)transform.scale.x, (int)transform.scale.y);
    }
    protected void drawGOasFilledCircle(Color color) {
        g.setColor(color);
        g.fillOval((int)transform.position.x, (int)transform.position.y, (int)transform.scale.x, (int)transform.scale.y);
    }

    //Draw Grid
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
    protected void drawGridbySize(Color color, Vector2 cellSize, int thickness) {
        g.setColor(color);
        g.setStroke(new BasicStroke(thickness));
        drawGrid(cellSize);
    }
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

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
    public boolean equalsClassOf(Class<?> other) {
        return this.getClass().equals(other);
    }
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
    protected boolean isDragging() {
        return dragging;
    }
    protected boolean wasJustReleased(Input.MouseCode mouseButton) {
        return !Input.getMouseButton(mouseButton) && wasMousePressedLastFrame;
    }

    public void onKeyPressed(int keyCode) {}
    public void onKeyReleased(int keyCode) {}
    public void onMousePressed(int x, int y, int button) {}

    public void onWindowClosing() {}
    public void onWindowMinimized() {}
    public void onWindowRestored() {}
    public void onWindowResized(int width, int height) {}
}