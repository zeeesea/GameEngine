package GameEngine.Tools.UIGenerator;

import GameEngine.Core.GameEngine;
import GameEngine.Core.gameObject.Obj.Button;
import GameEngine.Core.gameObject.Obj.Text;
import GameEngine.Core.scenes.SceneManager;
import GameEngine.Core.util.Vector2;
import GameEngine.Tools.MainMenu.MainMenu;

import java.awt.*;
import java.util.Arrays;


public class UIGenerator extends GameEngine {
    public static void main(String[] args) {
        GameEngine.launch(new UIGenerator());
    }

    //General Positions
    private int topY = 80;

    //Screen Positions
    private int screenOffsetX = 350;
    private int screenOffsetY = topY;
    private int screenWidth = 1150;
    private int screenHeight = 647;

    //Tool Positions/Sizes
    private int buttonHeight = 40;
    private int spacingY = 12;

    private int toolWidth = 300;
    private int beginOffsetX = 25;
    private int beginOffsetY = topY;

    //Buttons
    private Button backToMenuButton;
    private Button selectObjectButton;
    private Button resetButton;

    //Detail Editor (General)
    private Text detailEditorTitle;
    private Button colorPicker;

    //Tools
    private enum Tool {
        BUTTON ("Button"),
        SLIDER ("Slider"),
        TEXT ("Text");

        private final String name;

        Tool(String name) {
            this.name = name;
        }
        public String getName() {return name;}
        public static Tool getToolWithName(String name) {
            for (Tool tool : Tool.values()) {
                if (tool.getName().equals(name)) {
                    return tool;
                }
            }
            return null;
        }
    }
    private Tool currentTool = Tool.BUTTON;




    //Detail Editor Buttons


    @Override
    public void init() {
        setTitle("UI Generator");

        setupBackButton();
        setupToolButtons();
        setupDetailEditor();
    }

    //Setup
    private void setupBackButton() {
        backToMenuButton = new Button.Builder()
                .preset(Button.ButtonPreset.BACK_BUTTON)
                .onClick(() -> SceneManager.loadScene(new MainMenu()))
                .build();
        objectManager.add(backToMenuButton);
    }
    private void setupToolButtons() {
        selectObjectButton = new Button.Builder()
                .rect(new Rectangle(beginOffsetX, beginOffsetY, (int)(toolWidth * (0.6f)), buttonHeight))
                .text(Tool.BUTTON.name)
                .onClick(this::selectObject)
                .build();
        objectManager.add(selectObjectButton);

        resetButton = new Button.Builder()
                .rect(new Rectangle(beginOffsetX + (int)(toolWidth * (0.7f)), beginOffsetY, (int)(toolWidth * (0.3f)), buttonHeight))
                .text("Reset")
                .onClick(this::resetObject)
                .build();
        objectManager.add(resetButton);
    }
    private void setupDetailEditor() {
        detailEditorTitle = new Text.Builder("Detail Editor - " + currentTool.getName())
                .position(new Vector2(beginOffsetX, beginOffsetY + buttonHeight + spacingY + 25))
                .font(new Font("Arial", Font.BOLD, 20))
                .build();
        objectManager.add(detailEditorTitle);
    }

    //Update
    private void toolChange(Tool currentTool) {
        detailEditorTitle.setText("Detail Editor - " + currentTool.name);
    }

    private void selectObject() {
        String[] toolOptions = Arrays.stream(Tool.values())
                .map(Tool::getName)
                .toArray(String[]::new);
        String selected = (String) javax.swing.JOptionPane.showInputDialog(
                null,
                "Select Sprite:",
                "Load Sprite",
                javax.swing.JOptionPane.PLAIN_MESSAGE,
                null,
                toolOptions,
                toolOptions[0]
        );
        if (selected != null) {
            Tool t = Tool.getToolWithName(selected);
            if (t == null) return;
            currentTool = t;
        }

        selectObjectButton.setText(currentTool.name);
        toolChange(currentTool);
    }
    private void resetObject() {}

    @Override
    protected void update() {

    }

    @Override
    protected void draw(Graphics2D g) {
        //Draw Border
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(3));
        g.drawRect(screenOffsetX, screenOffsetY, screenWidth, screenHeight);
        g.setStroke(new BasicStroke(1));
    }
}
