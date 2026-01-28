// SceneManager.java - NEU
package GameEngine.Core.scenes;

import GameEngine.Core.GameEngine;
import GameEngine.Core.GameEngineFrame;

public class SceneManager {
    private static GameEngineFrame frame;
    private static GameEngine currentScene;

    public static void setFrame(GameEngineFrame f) {
        frame = f;
    }

    /**
     * Lädt eine neue Scene
     */
    public static void loadScene(GameEngine newScene) {
        // Alte Scene stoppen
        if (currentScene != null) {
            currentScene.setRunning(false);
        }

        currentScene = newScene;

        // Fenster updaten
        if (frame != null) {
            frame.getContentPane().removeAll();
            frame.add(newScene);
            frame.pack();
            frame.revalidate();
            frame.repaint();

            newScene.setParentFrame(frame);
            newScene.init();
            newScene.start();
        }
    }

    public static GameEngine getCurrentScene() {
        return currentScene;
    }
}