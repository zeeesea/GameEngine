package GameEngine.Core;

import GameEngine.Core.gameObject.*;
import GameEngine.Core.util.*;
import GameEngine.Core.input.*;
import GameEngine.Core.util.Console.Console;
import GameEngine.Core.util.Console.ConsoleTag;
import GameEngine.Tools.SpriteEditor.AnimationManager;
import GameEngine.Tools.SpriteEditor.SpriteManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public abstract class GameEngine extends JPanel implements ActionListener {

    //<editor-fold desc="Variables">
    // Standard-Einstellungen
    protected static int SCREEN_WIDTH = 1536; //80% of FHD
    protected static int SCREEN_HEIGHT = 864; //80% of FHD
    protected static int FPS = 60;

    // Input-States
    protected boolean key_W, key_A, key_S, key_D, key_SPACE;
    protected boolean key_UP, key_DOWN, key_LEFT, key_RIGHT;

    // Other
    protected java.util.Random random = new java.util.Random();
    protected boolean running = false;
    protected float deltaTime = 0;
    private JFrame parentFrame;
    protected Camera camera;
    private boolean fullscreen;
    private long last;

    protected GameObjectManager objectManager;
    protected SpriteManager spriteManager;
    protected AnimationManager animationManager;

    //</editor-fold>

    public GameEngine() {
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);

        setReferences();
    }
    public void setParentFrame(JFrame frame) {
        parentFrame = frame;

        // WindowListener für Close, Minimize, Restore
        frame.addWindowListener(new WindowEventListener(this));

        // ComponentListener für Resize
        frame.addComponentListener(new WindowResizeListener(this));
    }

    private void setReferences() {
        Console.log(ConsoleTag.SYSTEM, "Initializing Game Engine...");
        //Add Listeners
        InputKeyListener keyListener = new InputKeyListener(this);
        InputMouseListener mouseListener = new InputMouseListener(this);

        this.addKeyListener(keyListener);
        this.addMouseListener(mouseListener);
        this.addMouseMotionListener(mouseListener);

        camera = new Camera(this);
        if (objectManager == null) {objectManager = new GameObjectManager();}
        if (spriteManager == null) {spriteManager = new SpriteManager();}
        if (animationManager == null) {animationManager = new AnimationManager(spriteManager);}

        GameObject.setEngine(this);
        GameObject.setSpriteManager(spriteManager);
        GameObject.setAnimationManager(animationManager);
        Console.log(ConsoleTag.SYSTEM, "Initialisation complete");
    }

    //<editor-fold desc="Setter/Toggle Methods">
    protected void setWindowSize(int width, int height) {
        SCREEN_WIDTH = width;
        SCREEN_HEIGHT = height;

        this.setPreferredSize(new Dimension(width, height));
        this.revalidate(); // Panel anpassen

        if (parentFrame != null) {
            parentFrame.pack(); // JFrame an Panel-Größe anpassen
            parentFrame.setLocationRelativeTo(null); // optional: zentrieren
        }
    }
    protected void setWindowTitle(String title) {
        parentFrame.setTitle(title);
    }
    protected void setResizable(boolean resizable) {
        if (parentFrame == null) return;
        parentFrame.setResizable(resizable);
    }
    protected void setFullScreen(boolean fullScreen) {
        if (parentFrame == null) return;

        Console.log(ConsoleTag.SYSTEM, "Setting Fullscreen to " + fullScreen);

        if (!fullScreen) {
            parentFrame.setExtendedState(JFrame.NORMAL);
        } else {
            parentFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
        this.fullscreen = fullScreen;
    }
    protected void toggleFullscreen() {
        if (parentFrame == null) return;

        fullscreen = !fullscreen;
        if (!fullscreen) {
            parentFrame.setExtendedState(JFrame.NORMAL);
        } else {
            parentFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
        Console.log(ConsoleTag.SYSTEM, "Setting Fullscreen to " + fullscreen);
    }
    protected void setFPS(int fps) {
        FPS = fps;
    }
    public void setRunning(boolean running) {
        this.running = running;
    }
    public void callWindowResized(int width, int height) {
        SCREEN_WIDTH = getWidth();
        SCREEN_HEIGHT = getHeight();
        Console.log(ConsoleTag.SYSTEM, "Window resized to " + width + "," + height);
        onWindowResized(width, height);
    }
    //</editor-fold>

    //<editor-fold desc="Getter Methods">
    public int getScreenWidth() {
        return SCREEN_WIDTH;
    }
    public int getScreenHeight() {
        return SCREEN_HEIGHT;
    }
    public Vector2 getScreenSize() {
        return new Vector2(SCREEN_WIDTH, SCREEN_HEIGHT);
    }
    public int getFPS() {
        if (deltaTime == 0) return 0;
        return Math.round(1f / deltaTime);
    }
    public float getDeltaTime() {
        return deltaTime;
    }
    public JFrame getParentFrame() {
        return parentFrame;
    }
    public Camera getCamera() {
        return camera;
    }
    public boolean isFullscreen() {
        return fullscreen;
    }
    public boolean isRunning() {
        return running;
    }
    //</editor-fold>


    public void start() {
        running = true;

        new Thread(() -> {
            last = System.nanoTime();
            while (running) {

                updateGameEngine();

                try {
                    Thread.sleep(1000 / FPS); // Sleep in ms
                } catch (InterruptedException ignored) {}
            }
        }).start();
    }
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Vector2 offset = camera.getRenderOffset();
        g.translate(-(int)offset.x, -(int)offset.y);

        draw((Graphics2D) g);

        g.translate((int)offset.x, (int)offset.y);
    }
    private void updateGameEngine() {
        long now = System.nanoTime();
        deltaTime = (now - last) / 1_000_000_000f;
        last = now;

        Input.updateSmoothInput(deltaTime);
        camera.update(deltaTime);

        update();
        repaint();


        Input.update();
    }

    //<editor-fold desc="Template Methods">
    public abstract void init();
    protected abstract void update();
    protected abstract void draw(Graphics2D g);

    public void actionPerformed(ActionEvent e) {
    }
    public void onKeyPressed(int keyCode) {
        objectManager.onKeyPressed(keyCode);
    }
    public void onKeyReleased(int keyCode) {
        objectManager.onKeyReleased(keyCode);
    }
    public void onMousePressed(int x, int y, int button) {
        objectManager.onMousePressed(x, y, button);
    }
    public void onWindowClosing() {
        objectManager.onWindowClosing();
    }
    public void onWindowMinimized() {
        objectManager.onWindowMinimized();
    }
    public void onWindowRestored() {
        objectManager.onWindowRestored();
    }
    public void onWindowResized(int width, int height) {
        objectManager.onWindowResized(width, height);
    }

    //</editor-fold>
}