package GameEngine.Core.gameObject;

import GameEngine.Core.util.Timer.TimerSystem;
import GameEngine.Core.util.Vector2;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GameObjectManager {
    private List<GameObject> gameObjects = new ArrayList<>();
    private List<GameObject> toAdd = new ArrayList<>();
    private List<GameObject> toRemove = new ArrayList<>();

    private TimerSystem timersystem;

    public void add(GameObject obj) {
        obj.setObjectManager(this);
        toAdd.add(obj);
        obj.init();
    }

    public void remove(GameObject obj) {
        toRemove.add(obj);
    }

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

    public void draw(Graphics2D g) {
        // SORTIEREN nach renderOrder!
        List<GameObject> sorted = new ArrayList<>(gameObjects);
        sorted.sort(Comparator.comparingInt(obj -> obj.renderOrder));

        // Niedrigster renderOrder zuerst (Hintergrund)
        for (GameObject obj : sorted) {
            if (obj.active) {
                obj.callDraw(g);
            }
        }
    }

    public <T extends GameObject> T get(Class<T> type) {
        for (GameObject obj : gameObjects) {
            if (type.isInstance(obj)) {
                return type.cast(obj);
            }
        }
        return null;
    }
    public List<GameObject> getAll() {
        return new ArrayList<>(gameObjects);
    }
    public void setTimersystem(TimerSystem timersystem) {
        this.timersystem = timersystem;
    }
    public <T extends GameObject> List<T> getAll(Class<T> type) {
        List<T> result = new ArrayList<>();
        for (GameObject obj : gameObjects) {
            if (type.isInstance(obj)) {
                result.add(type.cast(obj));
            }
        }
        return result;
    }

    public void checkCollisions() {
        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject a = gameObjects.get(i);
            if (a.collider == null) continue;

            for (int j = i + 1; j < gameObjects.size(); j++) {

                GameObject b = gameObjects.get(j);
                if (b.collider == null) continue;

                Vector2 diff = a.getCenterPosition().subtract(b.getCenterPosition());
                float r = a.collider.getBoundingRadius() + b.collider.getBoundingRadius();
                if (diff.sqrLength() > r * r) continue; // keine Kollision möglich


                if (a.collider.intersects(b.collider)) {
                    a.onCollision(b);
                    b.onCollision(a);
                }
            }
        }
    }

    public void onKeyPressed(int keyCode) {
        for (GameObject obj : new ArrayList<>(gameObjects)) {
            obj.onKeyPressed(keyCode);
        }
    }
    public void onKeyReleased(int keyCode) {
        for (GameObject obj : new ArrayList<>(gameObjects)) {
            obj.onKeyReleased(keyCode);
        }
    }
    public void onMousePressed(int x, int y, int button) {
        for (GameObject obj : new ArrayList<>(gameObjects)) {
            obj.onMousePressed(x, y, button);
        }
    }
    public void onWindowClosing() {
        for (GameObject obj : new ArrayList<>(gameObjects)) {
            obj.onWindowClosing();
        }
    }
    public void onWindowMinimized() {
        for (GameObject obj : new ArrayList<>(gameObjects)) {
            obj.onWindowMinimized();
        }
    }
    public void onWindowRestored() {
        for (GameObject obj : new ArrayList<>(gameObjects)) {
            obj.onWindowRestored();
        }
    }
    public void onWindowResized(int width, int height) {
        for (GameObject obj : new ArrayList<>(gameObjects)) {
            obj.onWindowResized(width, height);
        }
    }
}