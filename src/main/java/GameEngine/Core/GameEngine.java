package GameEngine.Core;

import GameEngine.Core.gameObject.*;
import GameEngine.Core.util.*;
import GameEngine.Core.input.*;
import GameEngine.Core.util.Console.Console;
import GameEngine.Core.util.Console.ConsoleTag;
import GameEngine.Core.util.Timer.Timer;
import GameEngine.Core.util.Timer.TimerSystem;
import GameEngine.Tools.SpriteEditor.AnimationManager;
import GameEngine.Tools.SpriteEditor.SpriteManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public abstract class GameEngine extends JPanel implements ActionListener {

    //<editor-fold desc="Variables">
    // Standard-Einstellungen
    protected static int SCREEN_WIDTH = 1536;
    protected static int SCREEN_HEIGHT = 864;
    protected static int FPS = 60;

    // Input-States
    protected boolean key_W, key_A, key_S, key_D, key_SPACE;
    protected boolean key_UP, key_DOWN, key_LEFT, key_RIGHT;

    // Other
    protected java.util.Random random = new java.util.Random();
    protected float deltaTime = 0;
    private GameEngineFrame parentFrame;
    protected Camera camera;
    private boolean fullscreen;

    protected GameObjectManager objectManager;
    protected SpriteManager spriteManager;
    protected AnimationManager animationManager;
    protected TimerSystem timerSystem;

    // Listener-Referenzen speichern
    private InputKeyListener keyListener;
    private InputMouseListener mouseListener;

    //</editor-fold>

    public GameEngine() {
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);

        setReferences();
    }

    public void setParentFrame(GameEngineFrame frame) {
        parentFrame = frame;
        frame.addWindowListener(new WindowEventListener(this));
        frame.addComponentListener(new WindowResizeListener(this));
    }

    private void setReferences() {
        Console.log(ConsoleTag.SYSTEM, "Initializing Game Engine...");

        // Listeners
        keyListener = new InputKeyListener(this);
        mouseListener = new InputMouseListener(this);

        this.addKeyListener(keyListener);
        this.addMouseListener(mouseListener);
        this.addMouseMotionListener(mouseListener);

        //Creating Managers
        camera = new Camera(this);
        if (objectManager == null) objectManager = new GameObjectManager();
        if (spriteManager == null) spriteManager = new SpriteManager();
        if (animationManager == null) animationManager = new AnimationManager(spriteManager);
        if (timerSystem == null) timerSystem = new TimerSystem();

        //Setting References
        GameObject.setEngine(this);
        GameObject.setSpriteManager(spriteManager);
        GameObject.setAnimationManager(animationManager);
        Timer.setTimerSystem(timerSystem);
        objectManager.setTimersystem(timerSystem);

        Console.log(ConsoleTag.SYSTEM, "Initialisation complete");
    }

    public void updateFromSceneManager(float deltaTime) {
        this.deltaTime = deltaTime;

        Input.updateSmoothInput(deltaTime);
        camera.update(deltaTime);

        update();
        if (objectManager != null) objectManager.update(deltaTime);
        repaint();

        Input.update();
    }

    public InputKeyListener getKeyListener() {
        return keyListener;
    }

    public InputMouseListener getMouseListener() {
        return mouseListener;
    }

    //<editor-fold desc="Setter/Toggle Methods">
    public static void launch(GameEngine engine) {
        new GameEngineFrame(engine);
    }

    protected void setTitle(String title) {
        if (parentFrame == null) return;
        parentFrame.setTitle(title);
    }

    protected void setWindowSize(int width, int height) {
        SCREEN_WIDTH = width;
        SCREEN_HEIGHT = height;

        this.setPreferredSize(new Dimension(width, height));
        this.revalidate();

        if (parentFrame != null) {
            parentFrame.pack();
            parentFrame.setLocationRelativeTo(null);
        }
    }

    protected void setWindowTitle(String title) {
        if (parentFrame != null) {
            parentFrame.setTitle(title);
        }
    }

    protected void setResizable(boolean resizable) {
        if (parentFrame == null) return;
        parentFrame.setResizable(resizable);
    }

    protected void setFullScreen(boolean fullScreen) {
        if (parentFrame == null) return;
        if (this.fullscreen == fullScreen) return;

        Console.log(ConsoleTag.SYSTEM, "Setting Fullscreen to " + fullScreen);

        GraphicsDevice device = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice();

        if (fullScreen) {
            parentFrame.dispose();
            parentFrame.setUndecorated(true);
            parentFrame.setVisible(true);
            device.setFullScreenWindow(parentFrame);
        } else {
            device.setFullScreenWindow(null);
            parentFrame.dispose();
            parentFrame.setUndecorated(false);
            parentFrame.setVisible(true);
            parentFrame.pack();
            parentFrame.setLocationRelativeTo(null);
        }
        this.fullscreen = fullScreen;
    }

    protected void toggleFullscreen() {
        setFullScreen(!fullscreen);
    }

    protected void setFPS(int fps) {
        FPS = fps;
    }

    public void callWindowResized(int width, int height) {
        SCREEN_WIDTH = getWidth();
        SCREEN_HEIGHT = getHeight();
        Console.log(ConsoleTag.SYSTEM, "Window resized to " + width + "," + height);
        onWindowResized(width, height);
    }

    public void setIconFromSprite(String spriteName) {
        if (parentFrame != null) {
            parentFrame.setIconFromSprite(spriteName);
        }
    }

    public void setIconFromFile(String filePath) {
        if (parentFrame != null) {
            parentFrame.setIconFromFile(filePath);
        }
    }

    public void setIconFromPixels(Color[][] pixels) {
        if (parentFrame != null) {
            parentFrame.setIconFromPixels(pixels);
        }
    }

    public void setMultipleIconsFromSprite(String spriteName) {
        if (parentFrame != null) {
            parentFrame.setMultipleIconsFromSprite(spriteName);
        }
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
    //</editor-fold>

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Vector2 offset = camera.getRenderOffset();
        g.translate(-(int)offset.x, -(int)offset.y);

        if (objectManager != null) objectManager.draw((Graphics2D) g);

        draw((Graphics2D) g);

        g.translate((int)offset.x, (int)offset.y);
    }

    //<editor-fold desc="Template Methods">
    public abstract void init();
    protected abstract void update();
    protected abstract void draw(Graphics2D g);

    public void actionPerformed(ActionEvent e) {
    }

    public void onKeyPressed(int keyCode) {
        if (objectManager != null) {
            objectManager.onKeyPressed(keyCode);
        }
    }

    public void onKeyReleased(int keyCode) {
        if (objectManager != null) {
            objectManager.onKeyReleased(keyCode);
        }
    }

    public void onMousePressed(int x, int y, int button) {
        if (objectManager != null) {
            objectManager.onMousePressed(x, y, button);
        }
    }

    public void onWindowClosing() {
        if (objectManager != null) {
            objectManager.onWindowClosing();
        }
    }

    public void onWindowMinimized() {
        if (objectManager != null) {
            objectManager.onWindowMinimized();
        }
    }

    public void onWindowRestored() {
        if (objectManager != null) {
            objectManager.onWindowRestored();
        }
    }

    public void onWindowResized(int width, int height) {
        if (objectManager != null) {
            objectManager.onWindowResized(width, height);
        }
    }
    //</editor-fold>
}