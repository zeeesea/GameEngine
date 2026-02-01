package GameEngine.Tools.MainMenu;

import GameEngine.Core.GameEngine;
import GameEngine.Core.GameEngineFrame;
import GameEngine.Core.gameObject.FuncInt.FuncInt;
import GameEngine.Core.gameObject.Obj.Button;
import GameEngine.Core.gameObject.Obj.Text;
import GameEngine.Core.scenes.SceneManager;
import GameEngine.Core.util.Console.Console;
import GameEngine.Core.util.Vector2;
import GameEngine.Core.util.ClipBoard;
import GameEngine.Tools.SpriteEditor.SpriteEditor;
import GameEngine.Tools.UIGenerator.Blueprints.ButtonBP;
import GameEngine.Tools.UIGenerator.UIGenerator;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MainMenu extends GameEngine {

    public static void main(String[] args) {
        new GameEngineFrame(new MainMenu());
    }

    //Tool Buttons
    private Button startSpriteEditorBtn;
    private Button startUIGeneratorBtn;
    Color toolBtnColor = Color.BLUE;
    Font toolBtnFont = new Font("Arial", Font.BOLD, 24);
    private final Map<Button, FuncInt> buttonActions = new HashMap<>();

    //Text
    private Text titleText;

    //Positions
    private int toolButtonsY = 130;
    private int toolButtonsX = 100;
    private int buttonSpacingX = 50;
    private int buttonWidth = 200;
    private int buttonHeight = 60;


    @Override
    public void init() {
        setTitle("Main Menu");
        setupTitleText();
        setupToolButtons();
    }

    private void setupTitleText() {
        String title = "Main Menu";
        Font font = new Font("Arial", Font.BOLD, 50);
        titleText = new Text.Builder(title)
                .position(new Vector2(getScreenWidth() / 2, 100))
                .font(font)
                .alignment(Text.TextAlignment.CENTER)
                .build();
        objectManager.add(titleText);
    }
    private void setupToolButtons() {
        startSpriteEditorBtn = new Button.Builder()
                .rect(new Rectangle(toolButtonsX, toolButtonsY, buttonWidth, buttonHeight))
                .color(toolBtnColor)
                .text("Sprite Editor")
                .font(toolBtnFont)
                .textColor(Color.WHITE)
                .smoothHover(10,150)
                .onClick(this::openTool)
                .build();
        startUIGeneratorBtn = new Button.Builder()
                .rect(new Rectangle(toolButtonsX + buttonWidth + buttonSpacingX, toolButtonsY, buttonWidth, buttonHeight))
                .color(toolBtnColor)
                .text("UI Generator")
                .font(toolBtnFont)
                .textColor(Color.WHITE)
                .smoothHover(10,150)
                .onClick(this::openTool)
                .build();

        objectManager.add(startSpriteEditorBtn);
        objectManager.add(startUIGeneratorBtn);

        buttonActions.put(startSpriteEditorBtn, () -> SceneManager.loadScene(new SpriteEditor()));
        buttonActions.put(startUIGeneratorBtn, () -> SceneManager.loadScene(new UIGenerator()));
    }

    @Override
    protected void update() {
        objectManager.update(deltaTime);
    }

    private void openTool(Button button) {
        FuncInt action = buttonActions.get(button);
        if (action != null) action.call();
    }

    @Override
    protected void draw(Graphics2D g) {
        objectManager.draw(g);
    }
}
