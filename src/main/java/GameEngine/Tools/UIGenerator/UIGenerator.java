package GameEngine.Tools.UIGenerator;

import GameEngine.Core.GameEngine;
import GameEngine.Core.gameObject.GameObject;
import GameEngine.Core.gameObject.Obj.Button;
import GameEngine.Core.gameObject.Obj.Dropdown;
import GameEngine.Core.gameObject.Obj.Slider;
import GameEngine.Core.gameObject.Obj.Text;
import GameEngine.Core.gameObject.Obj.TextField;
import GameEngine.Core.input.Input;
import GameEngine.Core.scenes.SceneManager;
import GameEngine.Core.util.ColorPicker;
import GameEngine.Core.util.Vector2;
import GameEngine.Tools.MainMenu.MainMenu;
import GameEngine.Tools.UIGenerator.Blueprints.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * UI Generator Tool for visually creating UI layouts.
 * Allows placing, resizing, and configuring UI elements,
 * then exporting the generated builder code to clipboard.
 */
public class UIGenerator extends GameEngine {

    public static void main(String[] args) {
        GameEngine.launch(new UIGenerator());
    }

    // === Layout Constants ===
    private static final int SIDEBAR_WIDTH = 320;
    private static final int TOP_BAR_HEIGHT = 60;
    private static final int BOTTOM_BAR_HEIGHT = 60;
    private static final int PADDING = 15;
    private static final int ELEMENT_HEIGHT = 38;
    private static final int ELEMENT_SPACING = 10;
    private static final int CORNER_RADIUS = 8;

    // === Colors (Modern Dark Theme) ===
    private static final Color BG_COLOR = new Color(25, 25, 30);
    private static final Color SIDEBAR_COLOR = new Color(35, 35, 40);
    private static final Color CANVAS_BG = new Color(45, 45, 50);
    private static final Color ACCENT_COLOR = new Color(0, 150, 255);
    private static final Color ACCENT_HOVER = new Color(30, 170, 255);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color TEXT_SECONDARY = new Color(180, 180, 180);
    private static final Color BORDER_COLOR = new Color(60, 60, 65);

    // === Canvas ===
    private Rectangle canvasBounds;

    // === UI Elements ===
    private Button backButton;
    private Dropdown elementTypeDropdown;
    private Button addElementButton;
    private Button deleteElementButton;
    private Button exportButton;
    private Text statusText;

    // === Detail Editor Elements ===
    private Text detailTitle;
    private TextField varNameField;
    private TextField tagField;
    private TextField textField;
    private Button colorButton;
    private Button textColorButton;
    private Slider fontSizeSlider;
    private Slider cornerRadiusSlider;
    private Text fontSizeLabel;
    private Text cornerRadiusLabel;

    // === State ===
    private List<UIBlueprint> blueprints = new ArrayList<>();
    private UIBlueprint selectedBlueprint = null;
    private int blueprintCounter = 0;

    // === Element Types ===
    private enum ElementType {
        BUTTON("Button"),
        SLIDER("Slider"),
        TEXT("Text"),
        CHECKBOX("CheckBox");

        private final String name;
        ElementType(String name) { this.name = name; }
        public String getName() { return name; }

        public static ElementType fromName(String name) {
            for (ElementType t : values()) {
                if (t.name.equals(name)) return t;
            }
            return BUTTON;
        }
    }
    private ElementType currentElementType = ElementType.BUTTON;

    @Override
    public void init() {
        setTitle("UI Generator");
        setBackground(BG_COLOR);

        // Calculate canvas bounds
        canvasBounds = new Rectangle(
            SIDEBAR_WIDTH + PADDING,
            TOP_BAR_HEIGHT + PADDING,
            getScreenWidth() - SIDEBAR_WIDTH - PADDING * 2,
            getScreenHeight() - TOP_BAR_HEIGHT - BOTTOM_BAR_HEIGHT - PADDING * 2
        );

        // Add background first (renders behind everything)
        UIGeneratorBackground background = new UIGeneratorBackground(
            SIDEBAR_WIDTH, TOP_BAR_HEIGHT, BOTTOM_BAR_HEIGHT, CORNER_RADIUS,
            SIDEBAR_COLOR, CANVAS_BG, BORDER_COLOR, canvasBounds
        );
        objectManager.add(background);

        setupTopBar();
        setupSidebar();
        setupBottomBar();
    }

    // ==================== SETUP METHODS ====================

    private void setupTopBar() {
        // Back Button
        backButton = new Button.Builder()
                .rect(new Rectangle(PADDING, PADDING, 100, 35))
                .text("← Back")
                .color(SIDEBAR_COLOR)
                .textColor(TEXT_COLOR)
                .font(new Font("Arial", Font.BOLD, 14))
                .cornerRadius(CORNER_RADIUS)
                .smoothHover(5f, 150f)
                .onClick(() -> SceneManager.loadScene(new MainMenu()))
                .build();
        objectManager.add(backButton);

        // Title
        Text title = new Text.Builder("UI Generator")
                .position(new Vector2(PADDING + 120, PADDING + 24))
                .color(TEXT_COLOR)
                .font(new Font("Arial", Font.BOLD, 22))
                .build();
        objectManager.add(title);
    }

    private void setupSidebar() {
        int y = TOP_BAR_HEIGHT + PADDING;

        // === Element Type Section ===
        Text typeLabel = new Text.Builder("Add Element")
                .position(new Vector2(PADDING, y + 4))
                .color(TEXT_SECONDARY)
                .font(new Font("Arial", Font.BOLD, 12))
                .build();
        objectManager.add(typeLabel);
        y += 25;

        // Element Type Dropdown
        String[] typeOptions = {"Button", "Slider", "Text", "CheckBox"};
        elementTypeDropdown = new Dropdown.Builder()
                .rect(new Rectangle(PADDING, y, SIDEBAR_WIDTH - PADDING * 2 - 80, ELEMENT_HEIGHT))
                .options(typeOptions)
                .font(new Font("Arial", Font.PLAIN, 14))
                .backgroundColor(new Color(50, 50, 55))
                .borderColor(BORDER_COLOR)
                .textColor(TEXT_COLOR)
                .cornerRadius(CORNER_RADIUS)
                .onSelectionChanged(name -> currentElementType = ElementType.fromName(name))
                .build();
        objectManager.add(elementTypeDropdown);

        // Add Button
        addElementButton = new Button.Builder()
                .rect(new Rectangle(SIDEBAR_WIDTH - PADDING - 70, y, 70, ELEMENT_HEIGHT))
                .text("+ Add")
                .color(ACCENT_COLOR)
                .textColor(TEXT_COLOR)
                .font(new Font("Arial", Font.BOLD, 13))
                .cornerRadius(CORNER_RADIUS)
                .smoothHover(3f, 150f)
                .onClick(this::addNewElement)
                .build();
        objectManager.add(addElementButton);
        y += ELEMENT_HEIGHT + PADDING * 2;

        // === Detail Editor Section ===
        detailTitle = new Text.Builder("Properties")
                .position(new Vector2(PADDING, y + 4))
                .color(TEXT_SECONDARY)
                .font(new Font("Arial", Font.BOLD, 12))
                .build();
        objectManager.add(detailTitle);
        y += 28;

        // Variable Name
        Text varLabel = new Text.Builder("Variable Name")
                .position(new Vector2(PADDING, y))
                .color(TEXT_SECONDARY)
                .font(new Font("Arial", Font.PLAIN, 11))
                .build();
        objectManager.add(varLabel);
        y += 18;

        varNameField = new TextField.Builder()
                .pos(new Vector2(PADDING, y))
                .size(new Vector2(SIDEBAR_WIDTH - PADDING * 2, ELEMENT_HEIGHT))
                .placeholder("variableName")
                .font(new Font("Arial", Font.PLAIN, 14))
                .backgroundColor(new Color(50, 50, 55))
                .borderColor(BORDER_COLOR)
                .focusedBorderColor(ACCENT_COLOR)
                .textColor(TEXT_COLOR)
                .cornerRadius(CORNER_RADIUS)
                .onSubmit(this::updateVarName)
                .onUnfocus(() -> updateVarName(varNameField.getText()))
                .build();
        objectManager.add(varNameField);
        y += ELEMENT_HEIGHT + ELEMENT_SPACING;

        // Tag
        Text tagLabel = new Text.Builder("Tag")
                .position(new Vector2(PADDING, y))
                .color(TEXT_SECONDARY)
                .font(new Font("Arial", Font.PLAIN, 11))
                .build();
        objectManager.add(tagLabel);
        y += 18;

        tagField = new TextField.Builder()
                .pos(new Vector2(PADDING, y))
                .size(new Vector2(SIDEBAR_WIDTH - PADDING * 2, ELEMENT_HEIGHT))
                .placeholder("Tag")
                .font(new Font("Arial", Font.PLAIN, 14))
                .backgroundColor(new Color(50, 50, 55))
                .borderColor(BORDER_COLOR)
                .focusedBorderColor(ACCENT_COLOR)
                .textColor(TEXT_COLOR)
                .cornerRadius(CORNER_RADIUS)
                .onSubmit(this::updateTag)
                .onUnfocus(() -> updateTag(tagField.getText()))
                .build();
        objectManager.add(tagField);
        y += ELEMENT_HEIGHT + ELEMENT_SPACING;

        // Text Content
        Text textLabel = new Text.Builder("Text Content")
                .position(new Vector2(PADDING, y))
                .color(TEXT_SECONDARY)
                .font(new Font("Arial", Font.PLAIN, 11))
                .build();
        objectManager.add(textLabel);
        y += 18;

        textField = new TextField.Builder()
                .pos(new Vector2(PADDING, y))
                .size(new Vector2(SIDEBAR_WIDTH - PADDING * 2, ELEMENT_HEIGHT))
                .placeholder("Button text...")
                .font(new Font("Arial", Font.PLAIN, 14))
                .backgroundColor(new Color(50, 50, 55))
                .borderColor(BORDER_COLOR)
                .focusedBorderColor(ACCENT_COLOR)
                .textColor(TEXT_COLOR)
                .cornerRadius(CORNER_RADIUS)
                .onSubmit(this::updateText)
                .onUnfocus(() -> updateText(textField.getText()))
                .build();
        objectManager.add(textField);
        y += ELEMENT_HEIGHT + ELEMENT_SPACING;

        // Colors Row
        Text colorsLabel = new Text.Builder("Colors")
                .position(new Vector2(PADDING, y))
                .color(TEXT_SECONDARY)
                .font(new Font("Arial", Font.PLAIN, 11))
                .build();
        objectManager.add(colorsLabel);
        y += 18;

        colorButton = new Button.Builder()
                .rect(new Rectangle(PADDING, y, (SIDEBAR_WIDTH - PADDING * 3) / 2, ELEMENT_HEIGHT))
                .text("Background")
                .color(Color.WHITE)
                .textColor(Color.BLACK)
                .font(new Font("Arial", Font.BOLD, 11))
                .cornerRadius(CORNER_RADIUS)
                .onClick(this::selectBackgroundColor)
                .build();
        objectManager.add(colorButton);

        textColorButton = new Button.Builder()
                .rect(new Rectangle(PADDING * 2 + (SIDEBAR_WIDTH - PADDING * 3) / 2, y, (SIDEBAR_WIDTH - PADDING * 3) / 2, ELEMENT_HEIGHT))
                .text("Text Color")
                .color(Color.BLACK)
                .textColor(Color.WHITE)
                .font(new Font("Arial", Font.BOLD, 11))
                .cornerRadius(CORNER_RADIUS)
                .onClick(this::selectTextColor)
                .build();
        objectManager.add(textColorButton);
        y += ELEMENT_HEIGHT + ELEMENT_SPACING;

        // Font Size Slider
        fontSizeLabel = new Text.Builder("Font Size: 20")
                .position(new Vector2(PADDING, y))
                .color(TEXT_SECONDARY)
                .font(new Font("Arial", Font.PLAIN, 11))
                .build();
        objectManager.add(fontSizeLabel);
        y += 18;

        fontSizeSlider = new Slider.Builder()
                .position(PADDING, y)
                .size(SIDEBAR_WIDTH - PADDING * 2, 20)
                .range(8, 48)
                .startValue(20)
                .backgroundColor(new Color(50, 50, 55))
                .fillColor(ACCENT_COLOR)
                .handleColor(Color.WHITE)
                .cornerRadius(5)
                .onValueChanged(this::updateFontSize)
                .build();
        objectManager.add(fontSizeSlider);
        y += 30;

        // Corner Radius Slider
        cornerRadiusLabel = new Text.Builder("Corner Radius: 8")
                .position(new Vector2(PADDING, y))
                .color(TEXT_SECONDARY)
                .font(new Font("Arial", Font.PLAIN, 11))
                .build();
        objectManager.add(cornerRadiusLabel);
        y += 18;

        cornerRadiusSlider = new Slider.Builder()
                .position(PADDING, y)
                .size(SIDEBAR_WIDTH - PADDING * 2, 20)
                .range(0, 30)
                .startValue(8)
                .backgroundColor(new Color(50, 50, 55))
                .fillColor(ACCENT_COLOR)
                .handleColor(Color.WHITE)
                .cornerRadius(5)
                .onValueChanged(this::updateCornerRadius)
                .build();
        objectManager.add(cornerRadiusSlider);
        y += 40;

        // Delete Button
        deleteElementButton = new Button.Builder()
                .rect(new Rectangle(PADDING, y, SIDEBAR_WIDTH - PADDING * 2, ELEMENT_HEIGHT))
                .text("Delete Selected")
                .color(new Color(180, 60, 60))
                .textColor(TEXT_COLOR)
                .font(new Font("Arial", Font.BOLD, 13))
                .cornerRadius(CORNER_RADIUS)
                .smoothHover(3f, 150f)
                .onClick(this::deleteSelected)
                .build();
        objectManager.add(deleteElementButton);
    }

    private void setupBottomBar() {
        int y = getScreenHeight() - BOTTOM_BAR_HEIGHT + PADDING;

        // Export Button
        exportButton = new Button.Builder()
                .rect(new Rectangle(getScreenWidth() - PADDING - 180, y, 180, 35))
                .text("📋 Export to Clipboard")
                .color(ACCENT_COLOR)
                .textColor(TEXT_COLOR)
                .font(new Font("Arial", Font.BOLD, 13))
                .cornerRadius(CORNER_RADIUS)
                .smoothHover(5f, 150f)
                .onClick(this::exportCode)
                .build();
        objectManager.add(exportButton);

        // Status Text
        statusText = new Text.Builder("Click 'Add' to create UI elements")
                .position(new Vector2(SIDEBAR_WIDTH + PADDING, y + 10))
                .color(TEXT_SECONDARY)
                .font(new Font("Arial", Font.PLAIN, 13))
                .build();
        objectManager.add(statusText);
    }

    // ==================== ACTION METHODS ====================

    private void addNewElement() {
        blueprintCounter++;
        String varName = currentElementType.getName().toLowerCase() + blueprintCounter;

        // Calculate position in center of canvas
        Vector2 pos = new Vector2(
            canvasBounds.x + canvasBounds.width / 2 - 75,
            canvasBounds.y + canvasBounds.height / 2 - 25
        );

        UIBlueprint newBp = null;

        switch (currentElementType) {
            case BUTTON:
                newBp = new ButtonBP.Builder()
                        .pos(pos)
                        .size(new Vector2(150, 50))
                        .canvasBounds(canvasBounds)
                        .varName(varName)
                        .build();
                break;
            // TODO: Add other element types
            default:
                newBp = new ButtonBP.Builder()
                        .pos(pos)
                        .size(new Vector2(150, 50))
                        .canvasBounds(canvasBounds)
                        .varName(varName)
                        .build();
        }

        if (newBp != null) {
            blueprints.add(newBp);
            objectManager.add((GameObject) newBp);
            selectBlueprint(newBp);
            setStatus("Added " + currentElementType.getName() + ": " + varName);
        }
    }

    private void selectBlueprint(UIBlueprint bp) {
        // Deselect previous
        if (selectedBlueprint != null) {
            selectedBlueprint.setSelected(false);
        }

        selectedBlueprint = bp;

        if (bp != null) {
            bp.setSelected(true);
            updateDetailEditor();
        }
    }

    private void updateDetailEditor() {
        if (selectedBlueprint == null) return;

        varNameField.setText(selectedBlueprint.getVarName());

        if (selectedBlueprint instanceof ButtonBP) {
            ButtonBP btn = (ButtonBP) selectedBlueprint;
            tagField.setText(btn.getTag());
            textField.setText(btn.getText());
            colorButton.setColor(btn.getColor());
            textColorButton.setColor(btn.getTextColor());
            fontSizeSlider.setValue(btn.getFontSize());
            cornerRadiusSlider.setValue(btn.getCornerRadius());

            // Update button text colors for visibility
            Color bgColor = btn.getColor();
            float brightness = (bgColor.getRed() * 299 + bgColor.getGreen() * 587 + bgColor.getBlue() * 114) / 1000f;
            colorButton.setTextColor(brightness > 128 ? Color.BLACK : Color.WHITE);

            Color txtColor = btn.getTextColor();
            brightness = (txtColor.getRed() * 299 + txtColor.getGreen() * 587 + txtColor.getBlue() * 114) / 1000f;
            textColorButton.setTextColor(brightness > 128 ? Color.BLACK : Color.WHITE);
        }
    }

    private void updateVarName(String name) {
        if (selectedBlueprint != null && name != null && !name.isEmpty()) {
            selectedBlueprint.setVarName(name);
        }
    }

    private void updateTag(String tag) {
        if (selectedBlueprint instanceof ButtonBP && tag != null) {
            ((ButtonBP) selectedBlueprint).setTag(tag);
        }
    }

    private void updateText(String text) {
        if (selectedBlueprint instanceof ButtonBP && text != null) {
            ((ButtonBP) selectedBlueprint).setText(text);
        }
    }

    private void selectBackgroundColor() {
        ColorPicker.openColorPicker(color -> {
            if (selectedBlueprint instanceof ButtonBP) {
                ((ButtonBP) selectedBlueprint).setColor(color);
                colorButton.setColor(color);
                float brightness = (color.getRed() * 299 + color.getGreen() * 587 + color.getBlue() * 114) / 1000f;
                colorButton.setTextColor(brightness > 128 ? Color.BLACK : Color.WHITE);
            }
        });
    }

    private void selectTextColor() {
        ColorPicker.openColorPicker(color -> {
            if (selectedBlueprint instanceof ButtonBP) {
                ((ButtonBP) selectedBlueprint).setTextColor(color);
                textColorButton.setColor(color);
                float brightness = (color.getRed() * 299 + color.getGreen() * 587 + color.getBlue() * 114) / 1000f;
                textColorButton.setTextColor(brightness > 128 ? Color.BLACK : Color.WHITE);
            }
        });
    }

    private void updateFontSize(float size) {
        fontSizeLabel.setText("Font Size: " + (int) size);
        if (selectedBlueprint instanceof ButtonBP) {
            ((ButtonBP) selectedBlueprint).setFontSize((int) size);
        }
    }

    private void updateCornerRadius(float radius) {
        cornerRadiusLabel.setText("Corner Radius: " + (int) radius);
        if (selectedBlueprint instanceof ButtonBP) {
            ((ButtonBP) selectedBlueprint).setCornerRadius((int) radius);
        }
    }

    private void deleteSelected() {
        if (selectedBlueprint != null) {
            String name = selectedBlueprint.getVarName();
            blueprints.remove(selectedBlueprint);
            selectedBlueprint.destroyBlueprint();
            selectedBlueprint = null;
            setStatus("Deleted: " + name);
        }
    }

    private void exportCode() {
        if (blueprints.isEmpty()) {
            setStatus("Nothing to export!");
            return;
        }

        String code = CodeExporter.exportToClipboard(blueprints);
        setStatus("✓ Exported " + blueprints.size() + " element(s) to clipboard!");
    }

    private void setStatus(String text) {
        statusText.setText(text);
    }

    // ==================== UPDATE & DRAW ====================

    @Override
    protected void update() {
        // Check for blueprint selection on click
        if (Input.getMouseButtonDown(Input.MouseCode.LEFT)) {
            Vector2 mousePos = Input.getMousePosition();

            // Check if click is on canvas
            if (canvasBounds.contains(mousePos.xToInt(), mousePos.yToInt())) {
                // Check if clicking on a blueprint
                boolean clickedOnBlueprint = false;

                for (int i = blueprints.size() - 1; i >= 0; i--) {
                    UIBlueprint bp = blueprints.get(i);
                    GameObject go = (GameObject) bp;
                    if (go.collidesWith(mousePos)) {
                        selectBlueprint(bp);
                        clickedOnBlueprint = true;
                        break;
                    }
                }

                // Deselect if clicked on empty canvas
                if (!clickedOnBlueprint && selectedBlueprint != null) {
                    selectedBlueprint.setSelected(false);
                    selectedBlueprint = null;
                }
            }
        }
    }

    @Override
    protected void draw(Graphics2D g) {
        // Background is drawn by UIGeneratorBackground GameObject
        // This method is kept for potential overlay drawing
    }
}

