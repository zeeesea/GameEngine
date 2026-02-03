package GameEngine.Tools.UIGenerator;

import GameEngine.Core.GameEngine;
import GameEngine.Core.gameObject.GameObject;
import GameEngine.Core.gameObject.Obj.Button;
import GameEngine.Core.gameObject.Obj.Dropdown;
import GameEngine.Core.gameObject.Obj.Slider;
import GameEngine.Core.gameObject.Obj.Text;
import GameEngine.Core.gameObject.Obj.TextField;
import GameEngine.Core.gameObject.Obj.CheckBox;
import GameEngine.Core.input.Input;
import GameEngine.Core.scenes.SceneManager;
import GameEngine.Core.util.ColorPicker;
import GameEngine.Core.util.MathUtils;
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
    private static final int ELEMENT_HEIGHT = 32;
    private static final int ELEMENT_SPACING = 20;
    private static final int CORNER_RADIUS = 8;
    private static final int LABEL_HEIGHT = 16;

    // === Colors (Modern Dark Theme) ===
    private static final Color BG_COLOR = new Color(25, 25, 30);
    private static final Color SIDEBAR_COLOR = new Color(35, 35, 40);
    private static final Color CANVAS_BG = new Color(45, 45, 50);
    private static final Color ACCENT_COLOR = new Color(0, 150, 255);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color TEXT_SECONDARY = new Color(180, 180, 180);
    private static final Color BORDER_COLOR = new Color(60, 60, 65);

    // === Canvas ===
    private Rectangle canvasBounds;

    // === Target Resolution ===
    private int targetWidth = 1536;
    private int targetHeight = 864;
    private TextField targetWidthField;
    private TextField targetHeightField;

    // === Common Detail Editor Elements ===
    private Text detailTitle;
    private TextField varNameField;
    private Text colorsLabel;
    private Button colorButton;
    private Button colorButton2; // Secondary color (text color, border color, etc)
    private Text cornerRadiusLabel;
    private Slider cornerRadiusSlider;

    // === Type-specific fields (Row 1) ===
    private Text row1Label;
    private TextField row1Field1;
    private TextField row1Field2;
    private CheckBox row1Checkbox;

    // === Type-specific fields (Row 2) ===
    private Text row2Label;
    private Slider row2Slider;
    private Dropdown row2Dropdown1;
    private Dropdown row2Dropdown2;

    // === Type-specific fields (Row 3) ===
    private Text row3Label;
    private Slider row3Slider;
    private CheckBox row3Checkbox2;

    // === State ===
    private List<UIBlueprint> blueprints = new ArrayList<>();
    private UIBlueprint selectedBlueprint = null;
    private int blueprintCounter = 0;
    private Text statusText;

    // === Element Types ===
    private enum ElementType {
        BUTTON("Button"),
        UIRECT("UIRect"),
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

        canvasBounds = new Rectangle(
            SIDEBAR_WIDTH + PADDING,
            TOP_BAR_HEIGHT + PADDING,
            getScreenWidth() - SIDEBAR_WIDTH - PADDING * 2,
            getScreenHeight() - TOP_BAR_HEIGHT - BOTTOM_BAR_HEIGHT - PADDING * 2
        );

        UIGeneratorBackground background = new UIGeneratorBackground(
            SIDEBAR_WIDTH, TOP_BAR_HEIGHT, BOTTOM_BAR_HEIGHT, CORNER_RADIUS,
            SIDEBAR_COLOR, CANVAS_BG, BORDER_COLOR, canvasBounds
        );
        objectManager.add(background);

        setupTopBar();
        setupSidebar();
        setupBottomBar();
    }

    private void setupTopBar() {
        Button backButton = new Button.Builder()
                .rect(new Rectangle(PADDING, PADDING, 100, 35))
                .text("Back")
                .color(SIDEBAR_COLOR)
                .textColor(TEXT_COLOR)
                .font(new Font("Arial", Font.BOLD, 14))
                .cornerRadius(CORNER_RADIUS)
                .smoothHover(5f, 150f)
                .onClick(() -> SceneManager.loadScene(new MainMenu()))
                .build();
        objectManager.add(backButton);

        Text title = new Text.Builder("UI Generator")
                .position(new Vector2(PADDING + 120, PADDING + 24))
                .color(TEXT_COLOR)
                .font(new Font("Arial", Font.BOLD, 22))
                .build();
        objectManager.add(title);
    }

    private void setupSidebar() {
        int y = TOP_BAR_HEIGHT + PADDING;
        int fieldWidth = SIDEBAR_WIDTH - PADDING * 2;
        int halfWidth = (fieldWidth - PADDING) / 2;

        // === Add Element Section ===
        Text typeLabel = new Text.Builder("Add Element")
                .position(new Vector2(PADDING, y + 4))
                .color(TEXT_SECONDARY)
                .font(new Font("Arial", Font.BOLD, 12))
                .build();
        objectManager.add(typeLabel);
        y += 22;

        // Add Button first (so dropdown renders on top)
        Button addButton = new Button.Builder()
                .rect(new Rectangle(SIDEBAR_WIDTH - PADDING - 60, y, 60, ELEMENT_HEIGHT))
                .text("Add")
                .color(ACCENT_COLOR)
                .textColor(TEXT_COLOR)
                .font(new Font("Arial", Font.BOLD, 12))
                .cornerRadius(CORNER_RADIUS)
                .smoothHover(3f, 150f)
                .onClick(this::addNewElement)
                .build();
        objectManager.add(addButton);

        // Element Type Dropdown (renders on top)
        String[] typeOptions = {"Button", "UIRect", "Slider", "Text", "CheckBox"};
        Dropdown dropdown = new Dropdown.Builder()
                .rect(new Rectangle(PADDING, y, fieldWidth - 70, ELEMENT_HEIGHT))
                .options(typeOptions)
                .font(new Font("Arial", Font.PLAIN, 13))
                .backgroundColor(new Color(50, 50, 55))
                .borderColor(BORDER_COLOR)
                .textColor(TEXT_COLOR)
                .cornerRadius(CORNER_RADIUS)
                .onSelectionChanged(name -> currentElementType = ElementType.fromName(name))
                .build();
        objectManager.add(dropdown);

        y += ELEMENT_HEIGHT + PADDING + 5;

        // === Properties Section ===
        detailTitle = new Text.Builder("Properties")
                .position(new Vector2(PADDING, y + 4))
                .color(TEXT_SECONDARY)
                .font(new Font("Arial", Font.BOLD, 12))
                .build();
        objectManager.add(detailTitle);
        y += 22;

        // Variable Name
        Text varLabel = new Text.Builder("Variable Name")
                .position(new Vector2(PADDING, y))
                .color(TEXT_SECONDARY)
                .font(new Font("Arial", Font.PLAIN, 10))
                .build();
        objectManager.add(varLabel);
        y += LABEL_HEIGHT;

        varNameField = new TextField.Builder()
                .pos(new Vector2(PADDING, y))
                .size(new Vector2(fieldWidth, ELEMENT_HEIGHT))
                .placeholder("variableName")
                .font(new Font("Arial", Font.PLAIN, 13))
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

        // === Row 1: Tag/Text/Label/Range fields ===
        row1Label = new Text.Builder("Tag / Text")
                .position(new Vector2(PADDING, y))
                .color(TEXT_SECONDARY)
                .font(new Font("Arial", Font.PLAIN, 10))
                .build();
        objectManager.add(row1Label);
        y += LABEL_HEIGHT;

        row1Field1 = new TextField.Builder()
                .pos(new Vector2(PADDING, y))
                .size(new Vector2(halfWidth, ELEMENT_HEIGHT))
                .placeholder("Tag")
                .font(new Font("Arial", Font.PLAIN, 13))
                .backgroundColor(new Color(50, 50, 55))
                .borderColor(BORDER_COLOR)
                .focusedBorderColor(ACCENT_COLOR)
                .textColor(TEXT_COLOR)
                .cornerRadius(CORNER_RADIUS)
                .onSubmit(this::updateRow1Field1)
                .onUnfocus(() -> updateRow1Field1(row1Field1.getText()))
                .build();
        objectManager.add(row1Field1);

        row1Field2 = new TextField.Builder()
                .pos(new Vector2(PADDING + halfWidth + PADDING, y))
                .size(new Vector2(halfWidth, ELEMENT_HEIGHT))
                .placeholder("Text")
                .font(new Font("Arial", Font.PLAIN, 13))
                .backgroundColor(new Color(50, 50, 55))
                .borderColor(BORDER_COLOR)
                .focusedBorderColor(ACCENT_COLOR)
                .textColor(TEXT_COLOR)
                .cornerRadius(CORNER_RADIUS)
                .onSubmit(this::updateRow1Field2)
                .onUnfocus(() -> updateRow1Field2(row1Field2.getText()))
                .build();
        objectManager.add(row1Field2);

        row1Checkbox = new CheckBox.Builder()
                .pos(new Vector2(PADDING, y + 5))
                .label("Has Fill")
                .boxSize(18)
                .boxColor(new Color(50, 50, 55))
                .checkedColor(ACCENT_COLOR)
                .labelColor(TEXT_SECONDARY)
                .labelFont(new Font("Arial", Font.PLAIN, 12))
                .onChanged(this::updateRow1Checkbox)
                .build();
        objectManager.add(row1Checkbox);
        y += ELEMENT_HEIGHT + ELEMENT_SPACING;

        // === Colors Row ===
        colorsLabel = new Text.Builder("Colors")
                .position(new Vector2(PADDING, y))
                .color(TEXT_SECONDARY)
                .font(new Font("Arial", Font.PLAIN, 10))
                .build();
        objectManager.add(colorsLabel);
        y += LABEL_HEIGHT;

        colorButton = new Button.Builder()
                .rect(new Rectangle(PADDING, y, halfWidth, ELEMENT_HEIGHT))
                .text("Fill")
                .color(Color.WHITE)
                .textColor(Color.BLACK)
                .font(new Font("Arial", Font.BOLD, 11))
                .cornerRadius(CORNER_RADIUS)
                .onClick(this::selectColor1)
                .build();
        objectManager.add(colorButton);

        colorButton2 = new Button.Builder()
                .rect(new Rectangle(PADDING + halfWidth + PADDING, y, halfWidth, ELEMENT_HEIGHT))
                .text("Border")
                .color(new Color(80, 80, 85))
                .textColor(Color.WHITE)
                .font(new Font("Arial", Font.BOLD, 11))
                .cornerRadius(CORNER_RADIUS)
                .onClick(this::selectColor2)
                .build();
        objectManager.add(colorButton2);
        y += ELEMENT_HEIGHT + ELEMENT_SPACING;

        // === Row 2: Font Size / Style / Alignment / Range ===
        row2Label = new Text.Builder("Size / Style")
                .position(new Vector2(PADDING, y))
                .color(TEXT_SECONDARY)
                .font(new Font("Arial", Font.PLAIN, 10))
                .build();
        objectManager.add(row2Label);
        y += LABEL_HEIGHT;

        row2Slider = new Slider.Builder()
                .pos(new Vector2(PADDING, y))
                .size(fieldWidth, 18)
                .range(8, 48)
                .startValue(20)
                .backgroundColor(new Color(50, 50, 55))
                .fillColor(ACCENT_COLOR)
                .handleColor(Color.WHITE)
                .handleShape(Slider.HandleShape.CIRCLE)
                .cornerRadius(10)
                .onValueChanged(this::updateRow2Slider)
                .build();
        objectManager.add(row2Slider);

        String[] styleOptions = {"Plain", "Bold", "Italic"};
        row2Dropdown1 = new Dropdown.Builder()
                .rect(new Rectangle(PADDING, y + 30, halfWidth, ELEMENT_HEIGHT))
                .options(styleOptions)
                .font(new Font("Arial", Font.PLAIN, 12))
                .backgroundColor(new Color(50, 50, 55))
                .borderColor(BORDER_COLOR)
                .textColor(TEXT_COLOR)
                .cornerRadius(CORNER_RADIUS)
                .onSelectionChanged(this::updateRow2Dropdown1)
                .build();
        objectManager.add(row2Dropdown1);

        String[] alignOptions = {"LEFT", "CENTER", "RIGHT"};
        row2Dropdown2 = new Dropdown.Builder()
                .rect(new Rectangle(PADDING + halfWidth + PADDING, y + 30, halfWidth, ELEMENT_HEIGHT))
                .options(alignOptions)
                .font(new Font("Arial", Font.PLAIN, 12))
                .backgroundColor(new Color(50, 50, 55))
                .borderColor(BORDER_COLOR)
                .textColor(TEXT_COLOR)
                .cornerRadius(CORNER_RADIUS)
                .onSelectionChanged(this::updateRow2Dropdown2)
                .build();
        objectManager.add(row2Dropdown2);
        y += ELEMENT_HEIGHT + ELEMENT_SPACING;

        // === Corner Radius ===
        cornerRadiusLabel = new Text.Builder("Corner Radius: 8")
                .position(new Vector2(PADDING, y))
                .color(TEXT_SECONDARY)
                .font(new Font("Arial", Font.PLAIN, 10))
                .build();
        objectManager.add(cornerRadiusLabel);
        y += LABEL_HEIGHT;

        cornerRadiusSlider = new Slider.Builder()
                .pos(new Vector2(PADDING, y))
                .size(fieldWidth, 18)
                .range(0, 30)
                .startValue(8)
                .backgroundColor(new Color(50, 50, 55))
                .fillColor(ACCENT_COLOR)
                .handleColor(Color.WHITE)
                .handleShape(Slider.HandleShape.CIRCLE)
                .cornerRadius(10)
                .onValueChanged(this::updateCornerRadius)
                .build();
        objectManager.add(cornerRadiusSlider);
        y += ELEMENT_HEIGHT + ELEMENT_SPACING;

        // === Row 3: Additional options (checkboxes, border width, etc) ===
        row3Label = new Text.Builder("Options")
                .position(new Vector2(PADDING, y))
                .color(TEXT_SECONDARY)
                .font(new Font("Arial", Font.PLAIN, 10))
                .build();
        objectManager.add(row3Label);
        y += LABEL_HEIGHT;

        row3Slider = new Slider.Builder()
                .pos(new Vector2(PADDING, y))
                .size(fieldWidth, 18)
                .range(0, 40)
                .startValue(1)
                .backgroundColor(new Color(50, 50, 55))
                .fillColor(ACCENT_COLOR)
                .handleColor(Color.WHITE)
                .handleShape(Slider.HandleShape.CIRCLE)
                .cornerRadius(10)
                .onValueChanged(this::updateRow3Slider)
                .build();
        objectManager.add(row3Slider);


        row3Checkbox2 = new CheckBox.Builder()
                .pos(new Vector2(PADDING + halfWidth + PADDING, y + 5))
                .label("Show Value")
                .boxSize(18)
                .boxColor(new Color(50, 50, 55))
                .checkedColor(ACCENT_COLOR)
                .labelColor(TEXT_SECONDARY)
                .labelFont(new Font("Arial", Font.PLAIN, 12))
                .onChanged(this::updateRow3Checkbox2)
                .build();
        objectManager.add(row3Checkbox2);
        y += ELEMENT_HEIGHT + ELEMENT_SPACING;

        // Delete Button
        Button deleteButton = new Button.Builder()
                .rect(new Rectangle(PADDING, getScreenHeight() - BOTTOM_BAR_HEIGHT - ELEMENT_HEIGHT - PADDING, fieldWidth, ELEMENT_HEIGHT))
                .text("Delete Selected")
                .color(new Color(180, 60, 60))
                .textColor(TEXT_COLOR)
                .font(new Font("Arial", Font.BOLD, 12))
                .cornerRadius(CORNER_RADIUS)
                .smoothHover(3f, 150f)
                .onClick(this::deleteSelected)
                .build();
        objectManager.add(deleteButton);

        hideAllFields();
    }

    private void setupBottomBar() {
        int y = getScreenHeight() - BOTTOM_BAR_HEIGHT + PADDING;

        Text resLabel = new Text.Builder("Target:")
                .position(new Vector2(SIDEBAR_WIDTH + PADDING, y + 20))
                .color(TEXT_SECONDARY)
                .font(new Font("Arial", Font.PLAIN, 12))
                .build();
        objectManager.add(resLabel);

        targetWidthField = new TextField.Builder()
                .pos(new Vector2(SIDEBAR_WIDTH + PADDING + 50, y))
                .size(new Vector2(55, 30))
                .placeholder("1536")
                .font(new Font("Arial", Font.PLAIN, 11))
                .backgroundColor(new Color(50, 50, 55))
                .borderColor(BORDER_COLOR)
                .focusedBorderColor(ACCENT_COLOR)
                .textColor(TEXT_COLOR)
                .cornerRadius(CORNER_RADIUS)
                .onSubmit(this::updateTargetWidth)
                .onUnfocus(() -> updateTargetWidth(targetWidthField.getText()))
                .build();
        targetWidthField.setText("1536");
        objectManager.add(targetWidthField);

        Text xLabel = new Text.Builder("x")
                .position(new Vector2(SIDEBAR_WIDTH + PADDING + 110, y + 20))
                .color(TEXT_SECONDARY)
                .font(new Font("Arial", Font.PLAIN, 12))
                .build();
        objectManager.add(xLabel);

        targetHeightField = new TextField.Builder()
                .pos(new Vector2(SIDEBAR_WIDTH + PADDING + 120, y))
                .size(new Vector2(55, 30))
                .placeholder("864")
                .font(new Font("Arial", Font.PLAIN, 11))
                .backgroundColor(new Color(50, 50, 55))
                .borderColor(BORDER_COLOR)
                .focusedBorderColor(ACCENT_COLOR)
                .textColor(TEXT_COLOR)
                .cornerRadius(CORNER_RADIUS)
                .onSubmit(this::updateTargetHeight)
                .onUnfocus(() -> updateTargetHeight(targetHeightField.getText()))
                .build();
        targetHeightField.setText("864");
        objectManager.add(targetHeightField);

        Button exportButton = new Button.Builder()
                .rect(new Rectangle(getScreenWidth() - PADDING - 160, y, 160, 35))
                .text("Export to Clipboard")
                .color(ACCENT_COLOR)
                .textColor(TEXT_COLOR)
                .font(new Font("Arial", Font.BOLD, 14))
                .cornerRadius(CORNER_RADIUS)
                .smoothHover(5f, 150f)
                .onClick(this::exportCode)
                .build();
        objectManager.add(exportButton);

        statusText = new Text.Builder("Click 'Add' to create UI elements")
                .position(new Vector2(SIDEBAR_WIDTH + PADDING + 190, y + 20))
                .color(TEXT_SECONDARY)
                .font(new Font("Arial", Font.PLAIN, 12))
                .build();
        objectManager.add(statusText);
    }

    // ==================== VISIBILITY ====================

    private void hideAllFields() {
        row1Label.setActive(false);
        row1Field1.setActive(false);
        row1Field2.setActive(false);
        row1Checkbox.setActive(false);
        colorsLabel.setActive(false);
        colorButton.setActive(false);
        colorButton2.setActive(false);
        row2Label.setActive(false);
        row2Slider.setActive(false);
        row2Dropdown1.setActive(false);
        row2Dropdown2.setActive(false);
        cornerRadiusLabel.setActive(false);
        cornerRadiusSlider.setActive(false);
        row3Label.setActive(false);
        row3Slider.setActive(false);
        row3Checkbox2.setActive(false);
    }

    private void showFieldsForType(String type) {
        hideAllFields();

        switch (type) {
            case "UIRect":
                // Colors: Fill, Border
                colorsLabel.setActive(true);
                colorButton.setActive(true);
                colorButton.setText("Fill");
                colorButton2.setActive(true);
                colorButton2.setText("Border");
                // Corner Radius
                cornerRadiusLabel.setActive(true);
                cornerRadiusSlider.setActive(true);
                // Options: Border Width slider, Has Fill checkbox, Has Border checkbox
                row3Label.setActive(true);
                row3Label.setText("Border Width: 1");
                row3Slider.setActive(true);
                row1Checkbox.setActive(true);
                row1Checkbox.setLabel("Has Fill");
                break;

            case "Button":
                // Tag, Text
                row1Label.setActive(true);
                row1Label.setText("Tag / Text");
                row1Field1.setActive(true);
                row1Field1.setPlaceholder("Tag");
                row1Field2.setActive(true);
                row1Field2.setPlaceholder("Button Text");
                // Colors: Background, Text
                colorsLabel.setActive(true);
                colorButton.setActive(true);
                colorButton.setText("Background");
                colorButton2.setActive(true);
                colorButton2.setText("Text Color");
                // Font Size
                row2Label.setActive(true);
                row2Label.setText("Font Size: 20");
                row2Slider.setActive(true);
                // Corner Radius
                cornerRadiusLabel.setActive(true);
                cornerRadiusSlider.setActive(true);
                break;

            case "Slider":
                // Min, Max
                row1Label.setActive(true);
                row1Label.setText("Range (Min / Max)");
                row1Field1.setActive(true);
                row1Field1.setPlaceholder("0");
                row1Field2.setActive(true);
                row1Field2.setPlaceholder("100");
                // Colors: Background, Fill, Handle
                colorsLabel.setActive(true);
                colorButton.setActive(true);
                colorButton.setText("Fill");
                colorButton2.setActive(true);
                colorButton2.setText("Handle");
                // Corner Radius
                cornerRadiusLabel.setActive(true);
                cornerRadiusSlider.setActive(true);
                // Show Value checkbox
                row3Label.setActive(true);
                row3Label.setText("Options");
                row3Checkbox2.setActive(true);
                row3Checkbox2.setLabel("Show Value");
                break;

            case "Text":
                // Text Content
                row1Label.setActive(true);
                row1Label.setText("Text Content");
                row1Field1.setActive(true);
                row1Field1.setPlaceholder("Your text here");
                // Color
                colorsLabel.setActive(true);
                colorButton.setActive(true);
                colorButton.setText("Text Color");
                // Font Size
                row2Label.setActive(true);
                row2Label.setText("Font Size: 16");
                row2Slider.setActive(true);
                // Style, Alignment dropdowns
                row2Dropdown1.setActive(true);
                row2Dropdown2.setActive(true);
                break;

            case "CheckBox":
                // Label
                row1Label.setActive(true);
                row1Label.setText("Label");
                row1Field1.setActive(true);
                row1Field1.setPlaceholder("CheckBox Label");
                // Colors: Box, Checked
                colorsLabel.setActive(true);
                colorButton.setActive(true);
                colorButton.setText("Box");
                colorButton2.setActive(true);
                colorButton2.setText("Check");
                // Font Size (label)
                row2Label.setActive(true);
                row2Label.setText("Font Size: 14");
                row2Slider.setActive(true);
                // Box Size
                row3Label.setActive(true);
                row3Label.setText("Box Size: 20");
                row3Slider.setActive(true);
                break;
        }
    }

    // ==================== ACTIONS ====================

    private void addNewElement() {
        blueprintCounter++;
        String varName = currentElementType.getName().toLowerCase() + blueprintCounter;

        Vector2 pos = new Vector2(
            canvasBounds.x + canvasBounds.width / 2 - 75,
            canvasBounds.y + canvasBounds.height / 2 - 25
        );

        UIBlueprint newBp = null;

        switch (currentElementType) {
            case UIRECT:
                newBp = new UIRectBP.Builder()
                        .pos(pos)
                        .size(new Vector2(150, 100))
                        .canvasBounds(canvasBounds)
                        .varName(varName)
                        .build();
                break;
            case BUTTON:
                newBp = new ButtonBP.Builder()
                        .pos(pos)
                        .size(new Vector2(150, 50))
                        .canvasBounds(canvasBounds)
                        .varName(varName)
                        .build();
                break;
            case SLIDER:
                newBp = new SliderBP.Builder()
                        .pos(pos)
                        .size(new Vector2(200, 20))
                        .canvasBounds(canvasBounds)
                        .varName(varName)
                        .build();
                break;
            case TEXT:
                newBp = new TextBP.Builder()
                        .pos(pos)
                        .canvasBounds(canvasBounds)
                        .varName(varName)
                        .build();
                break;
            case CHECKBOX:
                newBp = new CheckBoxBP.Builder()
                        .pos(pos)
                        .canvasBounds(canvasBounds)
                        .varName(varName)
                        .build();
                break;
        }

        if (newBp != null) {
            newBp.setTargetResolution(targetWidth, targetHeight);
            blueprints.add(newBp);
            objectManager.add((GameObject) newBp);
            selectBlueprint(newBp);
            setStatus("Added " + currentElementType.getName() + ": " + varName);
        }
    }

    private void selectBlueprint(UIBlueprint bp) {
        if (selectedBlueprint != null) {
            selectedBlueprint.setSelected(false);
        }

        selectedBlueprint = bp;

        if (bp != null) {
            bp.setSelected(true);
            updateDetailEditor();
        } else {
            hideAllFields();
        }
    }

    private void updateDetailEditor() {
        if (selectedBlueprint == null) {
            hideAllFields();
            return;
        }

        varNameField.setText(selectedBlueprint.getVarName());
        showFieldsForType(selectedBlueprint.getTypeName());

        if (selectedBlueprint instanceof UIRectBP) {
            UIRectBP rect = (UIRectBP) selectedBlueprint;
            colorButton.setColor(rect.getFillColor());
            colorButton2.setColor(rect.getBorderColorValue());
            cornerRadiusSlider.setValue(rect.getCornerRadius());
            row3Slider.setValue(rect.getBorderWidth());
            row1Checkbox.setChecked(rect.getHasFill());
            updateButtonTextColor(colorButton, rect.getFillColor());
            updateButtonTextColor(colorButton2, rect.getBorderColorValue());

        } else if (selectedBlueprint instanceof ButtonBP) {
            ButtonBP btn = (ButtonBP) selectedBlueprint;
            row1Field1.setText(btn.getTag());
            row1Field2.setText(btn.getText());
            colorButton.setColor(btn.getColor());
            colorButton2.setColor(btn.getTextColor());
            row2Slider.setValue(btn.getFontSize());
            cornerRadiusSlider.setValue(btn.getCornerRadius());
            updateButtonTextColor(colorButton, btn.getColor());
            updateButtonTextColor(colorButton2, btn.getTextColor());

        } else if (selectedBlueprint instanceof SliderBP) {
            SliderBP slider = (SliderBP) selectedBlueprint;
            row1Field1.setText(String.valueOf(slider.getMinValue()));
            row1Field2.setText(String.valueOf(slider.getMaxValue()));
            colorButton.setColor(slider.getFillColor());
            colorButton2.setColor(slider.getHandleColor());
            cornerRadiusSlider.setValue(slider.getCornerRadius());
            row3Checkbox2.setChecked(slider.getShowValue());
            updateButtonTextColor(colorButton, slider.getFillColor());
            updateButtonTextColor(colorButton2, slider.getHandleColor());

        } else if (selectedBlueprint instanceof TextBP) {
            TextBP text = (TextBP) selectedBlueprint;
            row1Field1.setText(text.getText());
            colorButton.setColor(text.getColor());
            row2Slider.setValue(text.getFontSize());
            updateButtonTextColor(colorButton, text.getColor());

        } else if (selectedBlueprint instanceof CheckBoxBP) {
            CheckBoxBP cb = (CheckBoxBP) selectedBlueprint;
            row1Field1.setText(cb.getLabel());
            colorButton.setColor(cb.getBoxColor());
            colorButton2.setColor(cb.getCheckedColor());
            row2Slider.setValue(cb.getFontSize());
            row3Slider.setValue(cb.getBoxSize());
            updateButtonTextColor(colorButton, cb.getBoxColor());
            updateButtonTextColor(colorButton2, cb.getCheckedColor());
        }
    }

    private void updateButtonTextColor(Button btn, Color bgColor) {
        float brightness = (bgColor.getRed() * 299 + bgColor.getGreen() * 587 + bgColor.getBlue() * 114) / 1000f;
        btn.setTextColor(brightness > 128 ? Color.BLACK : Color.WHITE);
    }

    // === Field Update Callbacks ===

    private void updateVarName(String name) {
        if (selectedBlueprint != null && name != null && !name.isEmpty()) {
            selectedBlueprint.setVarName(name);
        }
    }

    private void updateRow1Field1(String value) {
        if (selectedBlueprint == null || value == null) return;

        if (selectedBlueprint instanceof ButtonBP) {
            ((ButtonBP) selectedBlueprint).setTag(value);
        } else if (selectedBlueprint instanceof SliderBP) {
            try { ((SliderBP) selectedBlueprint).setMinValue(Float.parseFloat(value)); } catch (NumberFormatException ignored) {}
        } else if (selectedBlueprint instanceof TextBP) {
            ((TextBP) selectedBlueprint).setText(value);
        } else if (selectedBlueprint instanceof CheckBoxBP) {
            ((CheckBoxBP) selectedBlueprint).setLabel(value);
        }
    }

    private void updateRow1Field2(String value) {
        if (selectedBlueprint == null || value == null) return;

        if (selectedBlueprint instanceof ButtonBP) {
            ((ButtonBP) selectedBlueprint).setText(value);
        } else if (selectedBlueprint instanceof SliderBP) {
            try { ((SliderBP) selectedBlueprint).setMaxValue(Float.parseFloat(value)); } catch (NumberFormatException ignored) {}
        }
    }

    private void updateRow1Checkbox(boolean checked) {
        if (selectedBlueprint instanceof UIRectBP) {
            ((UIRectBP) selectedBlueprint).setHasFill(checked);
        }
    }

    private void selectColor1() {
        ColorPicker.openColorPicker(color -> {
            if (selectedBlueprint instanceof UIRectBP) {
                ((UIRectBP) selectedBlueprint).setFillColor(color);
            } else if (selectedBlueprint instanceof ButtonBP) {
                ((ButtonBP) selectedBlueprint).setColor(color);
            } else if (selectedBlueprint instanceof SliderBP) {
                ((SliderBP) selectedBlueprint).setFillColor(color);
            } else if (selectedBlueprint instanceof TextBP) {
                ((TextBP) selectedBlueprint).setColor(color);
            } else if (selectedBlueprint instanceof CheckBoxBP) {
                ((CheckBoxBP) selectedBlueprint).setBoxColor(color);
            }
            colorButton.setColor(color);
            updateButtonTextColor(colorButton, color);
        });
    }

    private void selectColor2() {
        ColorPicker.openColorPicker(color -> {
            if (selectedBlueprint instanceof UIRectBP) {
                ((UIRectBP) selectedBlueprint).setBorderColor(color);
            } else if (selectedBlueprint instanceof ButtonBP) {
                ((ButtonBP) selectedBlueprint).setTextColor(color);
            } else if (selectedBlueprint instanceof SliderBP) {
                ((SliderBP) selectedBlueprint).setHandleColor(color);
            } else if (selectedBlueprint instanceof CheckBoxBP) {
                ((CheckBoxBP) selectedBlueprint).setCheckedColor(color);
            }
            colorButton2.setColor(color);
            updateButtonTextColor(colorButton2, color);
        });
    }

    private void updateRow2Slider(float value) {
        if (selectedBlueprint instanceof ButtonBP) {
            row2Label.setText("Font Size: " + (int) value);
            ((ButtonBP) selectedBlueprint).setFontSize((int) value);
        } else if (selectedBlueprint instanceof TextBP) {
            row2Label.setText("Font Size: " + (int) value);
            ((TextBP) selectedBlueprint).setFontSize((int) value);
        } else if (selectedBlueprint instanceof CheckBoxBP) {
            row2Label.setText("Font Size: " + (int) value);
            ((CheckBoxBP) selectedBlueprint).setFontSize((int) value);
        }
    }

    private void updateRow2Dropdown1(String value) {
        if (selectedBlueprint instanceof TextBP) {
            int style = Font.PLAIN;
            if ("Bold".equals(value)) style = Font.BOLD;
            else if ("Italic".equals(value)) style = Font.ITALIC;
            ((TextBP) selectedBlueprint).setFontStyle(style);
        }
    }

    private void updateRow2Dropdown2(String value) {
        if (selectedBlueprint instanceof TextBP) {
            ((TextBP) selectedBlueprint).setAlignment(value);
        }
    }

    private void updateCornerRadius(float value) {
        cornerRadiusLabel.setText("Corner Radius: " + (int) value);
        if (selectedBlueprint instanceof UIRectBP) {
            ((UIRectBP) selectedBlueprint).setCornerRadius((int) value);
        } else if (selectedBlueprint instanceof ButtonBP) {
            ((ButtonBP) selectedBlueprint).setCornerRadius((int) value);
        } else if (selectedBlueprint instanceof SliderBP) {
            ((SliderBP) selectedBlueprint).setCornerRadius((int) value);
        }
    }

    private void updateRow3Slider(float value) {
        if (selectedBlueprint instanceof UIRectBP) {
            row3Label.setText("Border Width: " + (int) value);
            ((UIRectBP) selectedBlueprint).setBorderWidth((int) value);
        } else if (selectedBlueprint instanceof CheckBoxBP) {
            row3Label.setText("Box Size: " + (int) value);
            value = MathUtils.map(value, 0, 40, 10, 40);
            ((CheckBoxBP) selectedBlueprint).setBoxSize((int) value);
        }
    }

    private void updateRow3Checkbox2(boolean checked) {
        if (selectedBlueprint instanceof SliderBP) {
            ((SliderBP) selectedBlueprint).setShowValue(checked);
        }
    }

    private void updateTargetWidth(String value) {
        try {
            targetWidth = Integer.parseInt(value);
            for (UIBlueprint bp : blueprints) {
                bp.setTargetResolution(targetWidth, targetHeight);
            }
        } catch (NumberFormatException ignored) {}
    }

    private void updateTargetHeight(String value) {
        try {
            targetHeight = Integer.parseInt(value);
            for (UIBlueprint bp : blueprints) {
                bp.setTargetResolution(targetWidth, targetHeight);
            }
        } catch (NumberFormatException ignored) {}
    }

    private void deleteSelected() {
        if (selectedBlueprint != null) {
            String name = selectedBlueprint.getVarName();
            blueprints.remove(selectedBlueprint);
            selectedBlueprint.destroyBlueprint();
            selectedBlueprint = null;
            hideAllFields();
            setStatus("Deleted: " + name);
        }
    }

    private void exportCode() {
        if (blueprints.isEmpty()) {
            setStatus("Nothing to export!");
            return;
        }

        for (UIBlueprint bp : blueprints) {
            bp.setTargetResolution(targetWidth, targetHeight);
        }

        // Sort blueprints: UIRect first, then others
        blueprints.sort((a, b) -> {
            boolean aIsRect = a instanceof UIRectBP;
            boolean bIsRect = b instanceof UIRectBP;
            if (aIsRect && !bIsRect) return -1;
            if (!aIsRect && bIsRect) return 1;
            return 0;
        });

        CodeExporter.exportToClipboard(blueprints);
        setStatus("Exported " + blueprints.size() + " element(s) to clipboard!");
    }

    private void setStatus(String text) {
        statusText.setText(text);
    }

    // ==================== UPDATE ====================

    @Override
    protected void update() {
        if (Input.getMouseButtonDown(Input.MouseCode.LEFT)) {
            Vector2 mousePos = Input.getMousePosition();

            if (canvasBounds.contains(mousePos.xToInt(), mousePos.yToInt())) {
                boolean clickedOnBlueprint = false;

                for (int i = blueprints.size() - 1; i >= 0; i--) {
                    UIBlueprint bp = blueprints.get(i);
                    GameObject go = (GameObject) bp;
                    if (go.collidesWith(mousePos, 10)) {
                        selectBlueprint(bp);
                        clickedOnBlueprint = true;
                        break;
                    }
                }

                if (!clickedOnBlueprint && selectedBlueprint != null) {
                    selectedBlueprint.setSelected(false);
                    selectedBlueprint = null;
                    hideAllFields();
                }
            }
        }
    }

    @Override
    protected void draw(Graphics2D g) {
        // Background drawn by UIGeneratorBackground
    }
}

