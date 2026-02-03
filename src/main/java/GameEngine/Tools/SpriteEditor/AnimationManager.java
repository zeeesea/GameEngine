package GameEngine.Tools.SpriteEditor;

import GameEngine.Core.util.Console.Console;
import GameEngine.Core.util.Console.ConsoleTag;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Verwaltet Animationen als Ordner mit mehreren Frames
 * Struktur: assets/sprites/animations/{animName}/{animName}_0.sprite, {animName}_1.sprite, ...
 */
public class AnimationManager {

    private String animationsDirectory = "assets/sprites/animations/";
    private SpriteManager spriteManager;

    public AnimationManager(SpriteManager spriteManager) {
        this.spriteManager = spriteManager;

        // Erstelle Ordner falls nicht vorhanden
        File dir = new File(animationsDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * Erstellt eine neue Animation (leerer Ordner)
     */
    public void createAnimation(String animationName) {
        File animDir = new File(animationsDirectory + animationName);
        if (!animDir.exists()) {
            animDir.mkdirs();
            Console.log(ConsoleTag.ANIMATION,"Created animation: " + animationName);
        }
    }

    /**
     * Speichert einen Frame der Animation
     */
    public void saveFrame(String animationName, int frameIndex, Color[][] pixels) {
        File animDir = new File(animationsDirectory + animationName);
        if (!animDir.exists()) {
            animDir.mkdirs();
        }

        String frameName = animationName + "_" + frameIndex;
        String framePath = animationsDirectory + animationName + "/" + frameName + ".sprite";

        try {
            SpriteManager.SavedSprite sprite = new SpriteManager.SavedSprite(frameName, pixels);
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(framePath));
            out.writeObject(sprite);
            out.close();
            Console.log(ConsoleTag.ANIMATION,"Frame saved: " + framePath);
        } catch (IOException e) {
            Console.log(ConsoleTag.ERROR,"Failed to save frame: " + framePath);
            e.printStackTrace();
        }
    }

    /**
     * Lädt einen Frame der Animation
     */
    public Color[][] loadFrame(String animationName, int frameIndex) {
        String frameName = animationName + "_" + frameIndex;
        String framePath = animationsDirectory + animationName + "/" + frameName + ".sprite";

        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(framePath));
            SpriteManager.SavedSprite sprite = (SpriteManager.SavedSprite) in.readObject();
            in.close();
            return sprite.pixels;
        } catch (IOException | ClassNotFoundException e) {
            Console.log(ConsoleTag.ERROR,"Failed to load frame: " + framePath);
            return null;
        }
    }

    /**
     * Löscht einen Frame und renummeriert alle nachfolgenden Frames
     */
    public void deleteFrame(String animationName, int frameIndex) {
        File animDir = new File(animationsDirectory + animationName);
        if (!animDir.exists()) return;

        int frameCount = getFrameCount(animationName);

        // Lösche den Frame
        File frameFile = new File(animationsDirectory + animationName + "/" + animationName + "_" + frameIndex + ".sprite");
        if (frameFile.exists()) {
            frameFile.delete();
        }

        // Renummeriere alle nachfolgenden Frames
        for (int i = frameIndex + 1; i < frameCount; i++) {
            File oldFile = new File(animationsDirectory + animationName + "/" + animationName + "_" + i + ".sprite");
            File newFile = new File(animationsDirectory + animationName + "/" + animationName + "_" + (i - 1) + ".sprite");

            if (oldFile.exists()) {
                oldFile.renameTo(newFile);
            }
        }
        Console.log(ConsoleTag.ANIMATION,"Deleted frame " + frameIndex + " from " + animationName);
    }

    /**
     * Gibt die Anzahl der Frames einer Animation zurück
     */
    public int getFrameCount(String animationName) {
        File animDir = new File(animationsDirectory + animationName);
        if (!animDir.exists()) return 0;

        File[] files = animDir.listFiles((d, name) -> name.endsWith(".sprite"));
        return files != null ? files.length : 0;
    }

    /**
     * Gibt alle Animation-Namen zurück
     */
    public List<String> getAnimationNames() {
        List<String> names = new ArrayList<>();
        File dir = new File(animationsDirectory);

        if (dir.exists() && dir.isDirectory()) {
            File[] subdirs = dir.listFiles(File::isDirectory);
            if (subdirs != null) {
                for (File subdir : subdirs) {
                    names.add(subdir.getName());
                }
            }
        }

        return names;
    }

    /**
     * Löscht eine komplette Animation
     */
    public void deleteAnimation(String animationName) {
        File animDir = new File(animationsDirectory + animationName);
        if (animDir.exists()) {
            deleteDirectory(animDir);
            Console.log(ConsoleTag.ANIMATION,"Animation deleted: " + animationName);
        }
    }

    /**
     * Hilfsmethode zum rekursiven Löschen eines Ordners
     */
    private void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }

    /**
     * Prüft ob eine Animation existiert
     */
    public boolean animationExists(String animationName) {
        File animDir = new File(animationsDirectory + animationName);
        return animDir.exists() && animDir.isDirectory();
    }

    /**
     * Dupliziert einen Frame
     */
    public void duplicateFrame(String animationName, int frameIndex) {
        Color[][] pixels = loadFrame(animationName, frameIndex);
        if (pixels != null) {
            int frameCount = getFrameCount(animationName);
            saveFrame(animationName, frameCount, pixels);
        }
    }

    /**
     * Gibt alle Frames einer Animation zurück
     */
    public List<Color[][]> loadAllFrames(String animationName) {
        List<Color[][]> frames = new ArrayList<>();
        int frameCount = getFrameCount(animationName);

        for (int i = 0; i < frameCount; i++) {
            Color[][] frame = loadFrame(animationName, i);
            if (frame != null) {
                frames.add(frame);
            }
        }

        return frames;
    }

    /**
     * Exportiert eine Animation als einzelne Sprites in den single-Ordner
     */
    public void exportToSingleSprites(String animationName) {
        int frameCount = getFrameCount(animationName);

        for (int i = 0; i < frameCount; i++) {
            Color[][] frame = loadFrame(animationName, i);
            if (frame != null) {
                String spriteName = animationName + "_frame_" + i;
                spriteManager.saveSprite(spriteName, frame);
            }
        }
        Console.log(ConsoleTag.ANIMATION,"Exported " + frameCount + " frames from " + animationName);
    }
}