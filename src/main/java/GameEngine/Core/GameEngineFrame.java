package GameEngine.Core;

import GameEngine.Core.scenes.SceneManager;
import GameEngine.Core.util.Console.Console;
import GameEngine.Core.util.Console.ConsoleTag;
import GameEngine.Tools.SpriteEditor.SpriteManager;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class GameEngineFrame extends JFrame {
    public GameEngineFrame(GameEngine panel, String title, boolean fullscreen) {
        Console.log(ConsoleTag.SYSTEM,"Setting up Game Engine Frame...");

        this.add(panel);
        this.setTitle(title);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setResizable(false);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        if (fullscreen) {
            this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }

        panel.setParentFrame(this);
        SceneManager.setFrame(this);

        panel.init();
        panel.start();
        Console.log(ConsoleTag.SYSTEM,"Game Engine Frame setup complete");
    }

    public GameEngineFrame(GameEngine panel) {
        Console.log(ConsoleTag.SYSTEM,"Setting up Game Engine Frame...");

        this.add(panel);
        this.setTitle("MY GAME");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setResizable(false);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        panel.setParentFrame(this);
        SceneManager.setFrame(this);

        panel.init();
        panel.start();
        Console.log(ConsoleTag.SYSTEM,"Game Engine Frame setup complete");
    }

    /**
     * Setzt das Icon des Fensters aus einem gespeicherten Sprite
     * @param spriteName Name des Sprites (muss in sprites/single/ existieren)
     */
    public void setIconFromSprite(String spriteName) {
        SpriteManager spriteManager = new SpriteManager();
        Color[][] pixels = spriteManager.loadSprite(spriteName);

        if (pixels == null) {
            Console.log(ConsoleTag.ERROR, "Failed to load sprite for icon: " + spriteName);
            return;
        }

        BufferedImage icon = createImageFromPixels(pixels);
        this.setIconImage(icon);
        Console.log(ConsoleTag.SYSTEM, "Icon set from sprite: " + spriteName);
    }

    /**
     * Setzt das Icon des Fensters aus einer Bilddatei
     * @param imagePath Pfad zur Bilddatei (z.B. "resources/icon.png")
     */
    public void setIconFromFile(String imagePath) {
        try {
            Image icon = Toolkit.getDefaultToolkit().getImage(imagePath);
            this.setIconImage(icon);
            Console.log(ConsoleTag.SYSTEM, "Icon set from file: " + imagePath);
        } catch (Exception e) {
            Console.log(ConsoleTag.ERROR, "Failed to load icon from file: " + imagePath);
            e.printStackTrace();
        }
    }

    /**
     * Setzt das Icon direkt aus einem Color[][] Array
     * @param pixels Das Pixel-Array des Sprites
     */
    public void setIconFromPixels(Color[][] pixels) {
        BufferedImage icon = createImageFromPixels(pixels);
        this.setIconImage(icon);
        Console.log(ConsoleTag.SYSTEM, "Icon set from pixel array");
    }

    /**
     * Konvertiert ein Color[][] Array zu einem BufferedImage
     */
    private BufferedImage createImageFromPixels(Color[][] pixels) {
        int width = pixels.length;
        int height = pixels[0].length;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color c = pixels[x][y];
                image.setRGB(x, y, c.getRGB());
            }
        }

        return image;
    }

    /**
     * Setzt mehrere Icons in verschiedenen Größen (für beste Darstellung)
     * Windows zeigt verschiedene Größen in Taskleiste (16x16) und Alt+Tab (32x32, 48x48)
     */
    public void setMultipleIconsFromSprite(String spriteName) {
        SpriteManager spriteManager = new SpriteManager();
        Color[][] pixels = spriteManager.loadSprite(spriteName);

        if (pixels == null) {
            Console.log(ConsoleTag.ERROR, "Failed to load sprite for icons: " + spriteName);
            return;
        }

        BufferedImage originalIcon = createImageFromPixels(pixels);

        // Erstelle verschiedene Größen für optimale Darstellung
        java.util.List<Image> icons = new java.util.ArrayList<>();
        icons.add(scaleImage(originalIcon, 16, 16));   // Taskleiste klein
        icons.add(scaleImage(originalIcon, 20, 20));   // Taskleiste Windows 10
        icons.add(scaleImage(originalIcon, 24, 24));   // Taskleiste macOS
        icons.add(scaleImage(originalIcon, 32, 32));   // Alt+Tab
        icons.add(scaleImage(originalIcon, 48, 48));   // Fenster-Header
        icons.add(scaleImage(originalIcon, 64, 64));   // Hochauflösende Displays

        this.setIconImages(icons);
        Console.log(ConsoleTag.SYSTEM, "Multiple icons set from sprite: " + spriteName);
    }

    /**
     * Skaliert ein BufferedImage auf eine neue Größe
     */
    private BufferedImage scaleImage(BufferedImage original, int width, int height) {
        BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = scaled.createGraphics();

        // Für Pixel-Art: Nearest Neighbor (keine Glättung)
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        // Alternative für normale Bilder (mit Glättung):
        // g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        g.drawImage(original, 0, 0, width, height, null);
        g.dispose();

        return scaled;
    }
}