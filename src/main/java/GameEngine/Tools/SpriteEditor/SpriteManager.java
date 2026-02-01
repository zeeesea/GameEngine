package GameEngine.Tools.SpriteEditor;

import GameEngine.Core.util.Console.Console;
import GameEngine.Core.util.Console.ConsoleColor;
import GameEngine.Core.util.Console.ConsoleTag;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Verwaltet gespeicherte Sprites
 * Speichert als .sprite Datei mit Color[][]
 * Neue Struktur: src/Game/sprites/single/ für einzelne Sprites
 */
public class SpriteManager {

    private List<SavedSprite> sprites;
    private String saveDirectory = "sprites/single/";
    private Map<String, Color[][]> loadedSpriteCache = new java.util.HashMap<>();

    public SpriteManager() {
        sprites = new ArrayList<>();

        // Erstelle Ordner falls nicht vorhanden
        File dir = new File(saveDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        loadAllSprites();
    }

    /**
     * Speichert ein Sprite
     */
    public void saveSprite(String name, Color[][] pixels) {
        SavedSprite sprite = new SavedSprite(name, pixels);

        try {
            String filename = saveDirectory + name + ".sprite";
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename));
            out.writeObject(sprite);
            out.close();

            // Zu Liste hinzufügen (wenn noch nicht vorhanden)
            boolean exists = false;
            for (int i = 0; i < sprites.size(); i++) {
                if (sprites.get(i).name.equals(name)) {
                    sprites.set(i, sprite); // Update
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                sprites.add(sprite);
            }

            // Cache updaten
            loadedSpriteCache.put(name, sprite.pixels);

            Console.log(ConsoleTag.SPRITE,"Sprite saved: " + filename);

        } catch (IOException e) {
            Console.log(ConsoleTag.ERROR,"Failed to save sprite: " + name);
            e.printStackTrace();
        }
    }

    /**
     * Lädt ein Sprite nach Name
     */
    public Color[][] loadSprite(String name) {
        return loadSpriteFromPath(name, saveDirectory);
    }

    public Color[][] loadSpriteFromPath(String name, String path) {
        try {
            if (loadedSpriteCache.containsKey(name)) {
                return loadedSpriteCache.get(name);
            }
            String filename = path + name + ".sprite";
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename));
            SavedSprite sprite = (SavedSprite) in.readObject();
            in.close();

            loadedSpriteCache.put(name, sprite.pixels);

            Console.log(ConsoleTag.SPRITE,"Sprite loaded: " + filename);
            return sprite.pixels;

        } catch (IOException | ClassNotFoundException e) {
            Console.log(ConsoleTag.ERROR,"Failed to load sprite: " + name);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lädt alle Sprites aus dem Ordner
     */
    public void loadAllSprites() {
        sprites.clear();
        File dir = new File(saveDirectory);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".sprite"));

        if (files != null) {
            for (File file : files) {
                try {
                    ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
                    SavedSprite sprite = (SavedSprite) in.readObject();
                    sprites.add(sprite);
                    in.close();
                } catch (IOException | ClassNotFoundException e) {
                    Console.log("ERROR","Failed to load: " + file.getName(), ConsoleColor.RED);
                }
            }
        }
        Console.log(ConsoleTag.SPRITE,"Loaded " + sprites.size() + " sprites");
    }

    /**
     * Löscht ein Sprite
     */
    public void deleteSprite(String name) {
        String filename = saveDirectory + name + ".sprite";
        File file = new File(filename);

        if (file.delete()) {
            sprites.removeIf(s -> s.name.equals(name));
            loadedSpriteCache.remove(name);
            Console.log(ConsoleTag.SPRITE,"Sprite deleted: " + name);
        } else {
            Console.log(ConsoleTag.ERROR,"Failed to delete sprite: " + name);
        }
    }

    /**
     * Gibt alle gespeicherten Sprite-Namen zurück
     */
    public List<String> getSpriteNames() {
        List<String> names = new ArrayList<>();
        for (SavedSprite sprite : sprites) {
            names.add(sprite.name);
        }
        return names;
    }

    /**
     * Gibt alle Sprites zurück
     */
    public List<SavedSprite> getAllSprites() {
        return new ArrayList<>(sprites);
    }

    /**
     * Prüft ob ein Sprite existiert
     */
    public boolean spriteExists(String name) {
        for (SavedSprite sprite : sprites) {
            if (sprite.name.equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Dupliziert ein Sprite
     */
    public void duplicateSprite(String name, String newName) {
        Color[][] pixels = loadSprite(name);
        if (pixels != null) {
            saveSprite(newName, pixels);
        }
    }

    /**
     * Exportiert ein Sprite als PNG (optional für später)
     */
    public void exportAsPNG(String name, String outputPath) {
        // Könnte später implementiert werden
    }

    // ===== SAVED SPRITE CLASS =====
    public static class SavedSprite implements Serializable {
        private static final long serialVersionUID = 1L;

        public String name;
        public Color[][] pixels;
        public int width;
        public int height;
        public long timestamp; // Wann gespeichert

        public SavedSprite(String name, Color[][] pixels) {
            this.name = name;
            this.width = pixels.length;
            this.height = pixels[0].length;
            this.timestamp = System.currentTimeMillis();

            // Deep copy des Arrays
            this.pixels = new Color[width][height];
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    Color c = pixels[x][y];
                    // Color serialisierbar machen
                    this.pixels[x][y] = new Color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
                }
            }
        }

        /**
         * Zeichnet das Sprite als Vorschau
         */
        public void drawPreview(Graphics2D g, int x, int y, int scale) {
            for (int py = 0; py < height; py++) {
                for (int px = 0; px < width; px++) {
                    g.setColor(pixels[px][py]);
                    g.fillRect(x + px * scale, y + py * scale, scale, scale);
                }
            }
        }
    }
}