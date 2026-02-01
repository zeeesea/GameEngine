package GameEngine.Tools.UIGenerator;

import GameEngine.Core.GameEngine;
import GameEngine.Core.gameObject.GameObject;
import GameEngine.Core.gameObject.Obj.Button;
import GameEngine.Core.gameObject.Obj.Dropdown;
import GameEngine.Core.gameObject.Obj.Text;
import GameEngine.Core.gameObject.Obj.TextField;
import GameEngine.Core.scenes.SceneManager;
import GameEngine.Core.util.ColorPicker;
import GameEngine.Core.util.Vector2;
import GameEngine.Tools.MainMenu.MainMenu;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


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

    //Tool Buttons
    private Button backToMenuButton;
    private Button selectObjectButton;
    private Dropdown objectDropdown;
    private Button resetButton;

    //Detail Editor (General)
    private GameObject currentObject;
    private Text detailEditorTitle;
    private Button selectColorButton;
    private TextField varNameField;
    private TextField varTagField;

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


    @Override
    public void init() {
        setTitle("UI Generator");

        setupBackButton();
        setupToolButtons();
        setupGeneralDetailEditor();
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
        String[] toolOptions = Arrays.stream(Tool.values()).map(Tool::getName).toArray(String[]::new);

        objectDropdown = new Dropdown.Builder()
                .rect(new Rectangle(beginOffsetX, beginOffsetY, (int)(toolWidth * (0.6f)), buttonHeight))
                .options(toolOptions)
                .font(new Font("Arial", Font.BOLD, 20))
                .onSelectionChanged(this::setTool)
                .build();
        objectManager.add(objectDropdown);

        resetButton = new Button.Builder()
                .rect(new Rectangle(beginOffsetX + (int)(toolWidth * (0.7f)), beginOffsetY, (int)(toolWidth * (0.3f)), buttonHeight))
                .text("Reset")
                .onClick(this::resetObject)
                .build();
        objectManager.add(resetButton);
    }
    private void setupGeneralDetailEditor() {
        int detailEditorStartY = beginOffsetY + buttonHeight + spacingY + 40;
        detailEditorTitle = new Text.Builder("Detail Editor - " + currentTool.getName())
                .position(new Vector2(beginOffsetX, detailEditorStartY))
                .font(new Font("Arial", Font.BOLD, 20))
                .build();
        objectManager.add(detailEditorTitle);

        varNameField = new TextField.Builder()
                .pos(new Vector2(beginOffsetX, detailEditorStartY + 30))
                .size(new Vector2((int)(toolWidth * (1.5/3.0)), buttonHeight))
                .placeholder("Name")
                .maxLength(30)
                .font(new Font("Arial", Font.BOLD, 20))
                .focusedColor(new Color(40, 40, 40))
                .focusedBorderColor(new Color(191, 191, 191))
                .onSubmit(this::setObjectText)
                .onUnfocus(this::setObjectText)
                .build();
        objectManager.add(varNameField);

        varTagField = new TextField.Builder()
                .pos(new Vector2(beginOffsetX + (int)(toolWidth * (1.6/3.0)), detailEditorStartY + 30))
                .size(new Vector2((int)(toolWidth * (1.0/3.0)), buttonHeight))
                .placeholder("Tag")
                .maxLength(30)
                .font(new Font("Arial", Font.BOLD, 20))
                .focusedColor(new Color(40, 40, 40))
                .focusedBorderColor(new Color(191, 191, 191))
                .onSubmit(this::setObjectText)
                .onUnfocus(this::setObjectText)
                .build();
        objectManager.add(varTagField);

        selectColorButton = new Button.Builder()
                .rect(new Rectangle(beginOffsetX, detailEditorStartY + 30 + buttonHeight + 18, buttonHeight, buttonHeight))
                .text("C")
                .font(new Font("Arial", Font.BOLD, 30))
                .onClick(this::selectColor)
                .build();
        objectManager.add(selectColorButton);
    }

    private void toolChange(Tool newTool) {
        if (newTool == null) return;
        currentTool = newTool;
        detailEditorTitle.setText("Detail Editor - " + currentTool.name);
    }
    private void setTool(String toolName) {
        Tool t = Tool.getToolWithName(toolName);
        if (t == null) return;
        toolChange(t);
    }
    private void resetObject() {}
    private void selectColor() {
        ColorPicker.openColorPicker(this::setColor);
    }
    private void setColor(Color color) {
        if (currentObject == null) return;
        if (currentObject.equalsClassOf(Button.class))
        {
            Button b = (Button) currentObject;
            b.setColor(color);
        }

    }
    private void setObjectText() {
        setObjectText(varNameField.getText());
    }
    private void setObjectText(String text) {
        if (text == null || currentObject == null) return;
        if (currentObject.equalsClassOf(Button.class)) {
            Button b = (Button) currentObject;
            b.setText(text);
        }
    }


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
