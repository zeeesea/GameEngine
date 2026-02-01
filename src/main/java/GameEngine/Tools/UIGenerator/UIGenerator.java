package GameEngine.Tools.UIGenerator;

import GameEngine.Core.GameEngine;
import GameEngine.Core.gameObject.Obj.Button;
import GameEngine.Core.scenes.SceneManager;
import GameEngine.Tools.MainMenu.MainMenu;

import java.awt.*;


public class UIGenerator extends GameEngine {
    public static void main(String[] args) {
        GameEngine.launch(new UIGenerator());
    }
    //General Buttons
    Button backToMenuButton;

    //Tool Buttons

    //Detail Editor Buttons

    @Override
    public void init() {
        setTitle("UI Generator");

        setupBackButton();
    }
    private void setupBackButton() {
        backToMenuButton = new Button.Builder()
                .rect(new Rectangle(10, 10, 150, 40))
                .color(Color.WHITE)
                .text("Back to Menu")
                .font(new Font("Arial", Font.BOLD, 16))
                .textColor(Color.BLACK)
                .smoothHover(10,150)
                .onClick(() -> SceneManager.loadScene(new MainMenu()))
                .build();
        objectManager.add(backToMenuButton);
    }

    @Override
    protected void update() {

    }

    @Override
    protected void draw(Graphics2D g) {

    }
}
