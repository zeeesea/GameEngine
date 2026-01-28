package GameEngine.Core;

import GameEngine.Core.scenes.SceneManager;
import GameEngine.Core.util.Console.Console;
import GameEngine.Core.util.Console.ConsoleColor;
import GameEngine.Core.util.Console.ConsoleTag;

import javax.swing.*;

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
}