package GameEngine.Tools.MainMenu;

import GameEngine.Core.GameEngine;
import GameEngine.Core.GameEngineFrame;
import GameEngine.Core.gameObject.Obj.Button;
import GameEngine.Core.scenes.SceneManager;
import GameEngine.Core.util.Console.Console;
import GameEngine.Tools.SpriteEditor.SpriteEditor;

import java.awt.*;

public class MainMenu extends GameEngine {

    public static void main(String[] args) {
        new GameEngineFrame(new MainMenu());
    }

    Button startSpriteEditorBtn;

    @Override
    public void init() {
        startSpriteEditorBtn = new Button.Builder()
                .rect(new Rectangle(100, 100, 200, 60))
                .color(Color.BLUE)
                .text("Sprite Editor")
                .font(new Font("Arial", Font.BOLD, 24))
                .textColor(Color.WHITE)
                .onClick(this::openSpriteEditor)
                .build();

        objectManager.add(startSpriteEditorBtn);
    }

    @Override
    protected void update() {
        objectManager.update(deltaTime);
    }

    private void openSpriteEditor() {
        //Open Sprite Editor
        SceneManager.loadScene(new SpriteEditor());
    }

    @Override
    protected void draw(Graphics2D g) {
        objectManager.draw(g);
    }
}
