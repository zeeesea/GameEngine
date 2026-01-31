package GameEngine.Core.scenes;

import GameEngine.Core.GameEngine;
import GameEngine.Core.GameEngineFrame;
import GameEngine.Core.util.Console.Console;
import GameEngine.Core.util.Console.ConsoleTag;

public class SceneManager {
    private static GameEngineFrame frame;
    private static GameEngine currentScene;
    private static boolean isLoading = false;
    private static long lastLoadTime = 0;
    private static final long LOAD_COOLDOWN = 60;

    // GAME LOOP - Central Thread for all Scenes
    private static Thread gameThread;
    private static boolean running = false;
    private static int FPS = 60;

    public static void setFrame(GameEngineFrame f) {
        frame = f;
        Console.log(ConsoleTag.SCENE, "SceneManager: Frame set");
    }

    /**
     * Startet den zentralen Game Loop
     * Wird nur EINMAL beim Programmstart aufgerufen
     */
    public static void startGameLoop() {
        if (running) {
            Console.log(ConsoleTag.SCENE, "SceneManager: Game loop already running");
            return;
        }

        running = true;
        gameThread = new Thread(() -> {
            Console.log(ConsoleTag.SCENE, "SceneManager: Game loop started");
            long last = System.nanoTime();

            while (running) {
                // Update der aktuellen Scene
                if (currentScene != null && !isLoading) {
                    long now = System.nanoTime();
                    float deltaTime = (now - last) / 1_000_000_000f;
                    last = now;

                    currentScene.updateFromSceneManager(deltaTime);
                }

                // Sleep für FPS-Limiting
                try {
                    Thread.sleep(1000 / FPS);
                } catch (InterruptedException e) {
                    Console.log(ConsoleTag.ERROR, "SceneManager: Game loop interrupted");
                    break;
                }
            }

            Console.log(ConsoleTag.SCENE, "SceneManager: Game loop stopped");
        }, "SceneManager-GameLoop");

        gameThread.start();
    }

    /**
     * Stoppt den Game Loop komplett (bei Programm-Ende)
     */
    public static void stopGameLoop() {
        running = false;
        if (gameThread != null) {
            try {
                gameThread.join(1000); // Warte max 1 Sekunde
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Lädt eine neue Scene
     */
    public static void loadScene(GameEngine newScene) {
        // COOLDOWN CHECK
        long currentTime = System.currentTimeMillis();
        if (isLoading || (currentTime - lastLoadTime) < LOAD_COOLDOWN) {
            Console.log(ConsoleTag.SCENE, "SceneManager: Load blocked (cooldown active)");
            return;
        }

        isLoading = true;
        lastLoadTime = currentTime;


        // Alte Scene aufräumen (KEIN setRunning mehr nötig!)
        if (currentScene != null) {

            // Listener entfernen
            if (currentScene.getKeyListener() != null) {
                currentScene.removeKeyListener(currentScene.getKeyListener());
            }
            if (currentScene.getMouseListener() != null) {
                currentScene.removeMouseListener(currentScene.getMouseListener());
                currentScene.removeMouseMotionListener(currentScene.getMouseListener());
            }
        }

        // Neue Scene setzen
        currentScene = newScene;

        // UI updaten
        if (frame != null) {
            frame.getContentPane().removeAll();
            frame.add(newScene);

            newScene.setParentFrame(frame);

            frame.pack();
            frame.revalidate();
            frame.repaint();

            newScene.requestFocusInWindow();

            newScene.init();

            Console.log(ConsoleTag.SCENE, "SceneManager: Scene loaded successfully!");
        } else {
            Console.log(ConsoleTag.ERROR, "SceneManager: Frame is null!");
        }

        isLoading = false;
    }

    /**
     * Setzt die FPS für den Game Loop
     */
    public static void setFPS(int fps) {
        FPS = fps;
        Console.log(ConsoleTag.SCENE, "SceneManager: FPS set to " + fps);
    }

    // Getter
    public static GameEngine getCurrentScene() {
        return currentScene;
    }

    public static GameEngineFrame getFrame() {
        return frame;
    }

    public static boolean isRunning() {
        return running;
    }

    public static int getFPS() {
        return FPS;
    }
}