package GameEngine.Core.gameObject;

import GameEngine.Core.util.Timer.TimerSystem;
import GameEngine.Core.util.Vector2;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Manages all GameObjects in a scene.
 * Handles adding, removing, updating, drawing, and collision detection.
 */
public class GameObjectManager {
    private List<GameObject> gameObjects = new ArrayList<>();
    private List<GameObject> toAdd = new ArrayList<>();
    private List<GameObject> toRemove = new ArrayList<>();

    private TimerSystem timersystem;

    /**
     * Adds a GameObject to be managed.
     * The object's init() method will be called.
     *
     * @param obj The GameObject to add
     */
    public void add(GameObject obj) {
        obj.setObjectManager(this);
        toAdd.add(obj);
        obj.init();
    }

    /**
     * Marks a GameObject for removal.
     * The object will be removed at the end of the current update cycle.
     *
     * @param obj The GameObject to remove
     */
    public void remove(GameObject obj) {
        toRemove.add(obj);
    }

    /**
     * Updates all active GameObjects and processes collisions.
     *
     * @param dt The delta time since last frame
     */
    public void update(float dt) {
        if (!toAdd.isEmpty()) {
            gameObjects.addAll(toAdd);
            toAdd.clear();
        }

        checkCollisions();

        for (GameObject obj : gameObjects) {
            if (obj.active) {
                obj.update(dt);
            }
        }

        if (timersystem != null) timersystem.update(dt);

        if (toRemove.isEmpty()) return;
        for (GameObject obj : toRemove) {
            obj.setActive(false);
        }
        gameObjects.removeAll(toRemove);
        toRemove.clear();
    }

    /**
     * Draws all active GameObjects sorted by render order.
     *
     * @param g The Graphics2D context to draw to
     */
    public void draw(Graphics2D g) {
        // Sort by renderOrder
        List<GameObject> sorted = new ArrayList<>(gameObjects);
        sorted.sort(Comparator.comparingInt(obj -> obj.renderOrder));

        // Lowest renderOrder first (background)
        for (GameObject obj : sorted) {
            if (obj.active) {
                obj.callDraw(g);
            }
        }
    }

    /**
     * Gets the first GameObject of the specified type.
     *
     * @param type The class type to search for
     * @param <T> The GameObject subclass type
     * @return The first matching object, or null if not found
     */
    public <T extends GameObject> T get(Class<T> type) {
        for (GameObject obj : gameObjects) {
            if (type.isInstance(obj)) {
                return type.cast(obj);
            }
        }
        return null;
    }

    /**
     * Returns all managed GameObjects.
     *
     * @return A copy of the object list
     */
    public List<GameObject> getAll() {
        return new ArrayList<>(gameObjects);
    }

    /**
     * Sets the timer system for this manager.
     *
     * @param timersystem The timer system to use
     */
    public void setTimersystem(TimerSystem timersystem) {
        this.timersystem = timersystem;
    }

    /**
     * Gets all GameObjects of the specified type.
     *
     * @param type The class type to search for
     * @param <T> The GameObject subclass type
     * @return A list of all matching objects
     */
    public <T extends GameObject> List<T> getAll(Class<T> type) {
        List<T> result = new ArrayList<>();
        for (GameObject obj : gameObjects) {
            if (type.isInstance(obj)) {
                result.add(type.cast(obj));
            }
        }
        return result;
    }

    /**
     * Checks and processes collisions between all GameObjects with colliders.
     */
    public void checkCollisions() {
        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject a = gameObjects.get(i);
            if (a.collider == null) continue;

            for (int j = i + 1; j < gameObjects.size(); j++) {

                GameObject b = gameObjects.get(j);
                if (b.collider == null) continue;

                Vector2 diff = a.getCenterPosition().subtract(b.getCenterPosition());
                float r = a.collider.getBoundingRadius() + b.collider.getBoundingRadius();
                if (diff.sqrLength() > r * r) continue; // No collision possible


                if (a.collider.intersects(b.collider)) {
                    a.onCollision(b);
                    b.onCollision(a);
                }
            }
        }
    }

    /**
     * Forwards key pressed events to all GameObjects.
     *
     * @param keyCode The key code of the pressed key
     */
    public void onKeyPressed(int keyCode) {
        for (GameObject obj : new ArrayList<>(gameObjects)) {
            obj.onKeyPressed(keyCode);
        }
    }

    /**
     * Forwards key released events to all GameObjects.
     *
     * @param keyCode The key code of the released key
     */
    public void onKeyReleased(int keyCode) {
        for (GameObject obj : new ArrayList<>(gameObjects)) {
            obj.onKeyReleased(keyCode);
        }
    }

    /**
     * Forwards mouse pressed events to all GameObjects.
     *
     * @param x The x coordinate
     * @param y The y coordinate
     * @param button The mouse button
     */
    public void onMousePressed(int x, int y, int button) {
        for (GameObject obj : new ArrayList<>(gameObjects)) {
            obj.onMousePressed(x, y, button);
        }
    }

    /**
     * Forwards window closing events to all GameObjects.
     */
    public void onWindowClosing() {
        for (GameObject obj : new ArrayList<>(gameObjects)) {
            obj.onWindowClosing();
        }
    }

    /**
     * Forwards window minimized events to all GameObjects.
     */
    public void onWindowMinimized() {
        for (GameObject obj : new ArrayList<>(gameObjects)) {
            obj.onWindowMinimized();
        }
    }

    /**
     * Forwards window restored events to all GameObjects.
     */
    public void onWindowRestored() {
        for (GameObject obj : new ArrayList<>(gameObjects)) {
            obj.onWindowRestored();
        }
    }

    /**
     * Forwards window resized events to all GameObjects.
     *
     * @param width The new window width
     * @param height The new window height
     */
    public void onWindowResized(int width, int height) {
        for (GameObject obj : new ArrayList<>(gameObjects)) {
            obj.onWindowResized(width, height);
        }
    }
}