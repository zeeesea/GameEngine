package GameEngine.Tools.MainMenu;

import GameEngine.Core.GameEngine;
import GameEngine.Core.GameEngineFrame;
import GameEngine.Core.gameObject.*;
import GameEngine.Core.scenes.SceneManager;
import GameEngine.Tools.SpriteEditor.SpriteEditor;

import java.awt.*;

public class MainMenu extends GameEngine {

    public static void main(String[] args) {
        new GameEngineFrame(new MainMenu());
    }

    ButtonObj startSpriteEditorBtn;

    @Override
    public void init() {
        startSpriteEditorBtn = new ButtonObj(
                new Rectangle(100, 100, 200, 60),
                Color.BLUE,
                this::openSpriteEditor,
                "Sprite Editor",
                new Font("Arial", Font.BOLD, 24),
                Color.WHITE
        );
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
