package GameEngine.Tools.SpriteEditor;

import GameEngine.Core.*;
import GameEngine.Core.gameObject.*;
import GameEngine.Core.input.Input;
import GameEngine.Core.util.Vector2;

import java.awt.*;
import java.util.Stack;

public class SpriteEditor extends GameEngine {

    public static void main(String[] args) {
        new GameEngineFrame(new SpriteEditor());
    }

    private class Action {
        Vector2 pos;
        Color color;

        public Action(Vector2 pos, Color color) {
            this.pos = pos;
            this.color = color;
        }
    }

    // Mode
    private enum EditorMode {
        SPRITE, ANIMATION
    }
    private EditorMode currentMode = EditorMode.SPRITE;

    // Tools
    private enum Tool {
        BRUSH, ERASER, BUCKET
    }
    private Tool currentTool = Tool.BRUSH;

    // Sprite-Daten
    private Color[][] pixels = new Color[32][32];
    private Color baseColor = Color.WHITE;
    private Color selectedColor = Color.WHITE;
    private float brightness = 1.0f;
    Stack<Action> actionsStack = new Stack<>();
    int undoSteps;
    int undoClickThreshold = 10;
    private int pixelSize = 20;
    private int gridOffsetX = 100;
    private int gridOffsetY = 100;
    private Vector2 lastPos = new Vector2(-1, -1);

    // Sprite Manager
    private SpriteManager spriteManager;
    private String currentSpriteName = "untitled";

    // Animation Manager
    private AnimationManager animationManager;
    private String currentAnimationName = "untitled";
    private int currentFrame = 0;
    private int totalFrames = 1;

    // Farbpalette
    private final Color[] palette = {
            Color.BLACK,
            Color.WHITE,
            Color.RED,
            Color.GREEN,
            Color.BLUE,
            Color.YELLOW,
            Color.ORANGE,
            Color.PINK,
            Color.CYAN,
            Color.MAGENTA,
            new Color(128, 64, 0),
            new Color(128, 128, 128),
            new Color(0, 128, 0),
            new Color(128, 0, 128),
            new Color(255, 192, 203),
            new Color(64, 64, 64)
    };

    // UI
    private ColorButton[] colorButtons;
    private int paletteX = 800;
    private int paletteY = 120;
    private int palette2X = paletteX + 350;
    private int buttonSize = 50;
    private int buttonSpacing = 10;

    // Tool Buttons
    private ToolButton brushButton;
    private ToolButton eraserButton;
    private ToolButton bucketButton;

    // Mode Buttons
    private ButtonObj spriteModeButton;
    private ButtonObj animationModeButton;

    // Brightness Slider
    private SliderObj brightnessSlider;

    // Save/Load Buttons (Sprite Mode)
    private ButtonObj saveButton;
    private ButtonObj loadButton;
    private ButtonObj newButton;

    // Animation Mode Buttons
    private ButtonObj saveAnimationButton;
    private ButtonObj loadAnimationButton;
    private ButtonObj newAnimationButton;
    private ButtonObj newFrameButton;
    private ButtonObj deleteFrameButton;
    private ButtonObj prevFrameButton;
    private ButtonObj nextFrameButton;
    private ButtonObj playAnimationButton;

    // Animation Preview
    private boolean isPlaying = false;
    private float animationTimer = 0f;
    private float frameDuration = 0.1f; // 10 FPS default
    private SliderObj fpsSlider;

    // Text Elements
    private TextObj titleText;
    private TextObj currentSpriteText;
    private TextObj shortcutsText;
    private TextObj brightnessText;
    private TextObj toolText;
    private TextObj frameInfoText;
    private TextObj fpsText;

    @Override
    public void init() {
        spriteManager = new SpriteManager();
        animationManager = new AnimationManager(spriteManager);
        clearSprite();
        setupToolButtons();
        setupColorButtons();
        setupBrightnessSlider();
        setupModeButtons();
        setupFileButtons();
        setupAnimationButtons();
        setupTextElements();
        updateUIVisibility();
    }

    private void setupModeButtons() {
        int modeY = 80;
        int modeWidth = 120;
        int modeHeight = 35;

        spriteModeButton = new ButtonObj(
                new Rectangle(palette2X, modeY, modeWidth, modeHeight),
                new Color(100, 150, 255),
                () -> switchMode(EditorMode.SPRITE),
                "Sprite Mode",
                new Font("Arial", Font.BOLD, 14),
                Color.WHITE
        );
        objectManager.add(spriteModeButton);

        animationModeButton = new ButtonObj(
                new Rectangle(palette2X + modeWidth + 10, modeY, modeWidth, modeHeight),
                new Color(150, 100, 255),
                () -> switchMode(EditorMode.ANIMATION),
                "Animation",
                new Font("Arial", Font.BOLD, 14),
                Color.WHITE
        );
        objectManager.add(animationModeButton);
    }

    private void switchMode(EditorMode mode) {
        if (currentMode == mode) return;

        currentMode = mode;
        clearSprite();
        actionsStack.clear();

        if (mode == EditorMode.SPRITE) {
            currentSpriteName = "untitled";
            spriteModeButton.setColor(new Color(100, 150, 255));
            animationModeButton.setColor(new Color(80, 60, 150));
        } else {
            currentAnimationName = "untitled";
            currentFrame = 0;
            totalFrames = 1;
            isPlaying = false;
            animationModeButton.setColor(new Color(150, 100, 255));
            spriteModeButton.setColor(new Color(60, 90, 150));
        }

        updateUIVisibility();
        updateCurrentNameText();
    }

    private void updateUIVisibility() {
        boolean isSprite = currentMode == EditorMode.SPRITE;

        // Sprite Mode Buttons - besser: setVisible statt setActive
        setButtonVisible(saveButton, isSprite);
        setButtonVisible(loadButton, isSprite);
        setButtonVisible(newButton, isSprite);

        // Animation Mode Buttons
        setButtonVisible(saveAnimationButton, !isSprite);
        setButtonVisible(loadAnimationButton, !isSprite);
        setButtonVisible(newAnimationButton, !isSprite);
        setButtonVisible(newFrameButton, !isSprite);
        setButtonVisible(deleteFrameButton, !isSprite);
        setButtonVisible(prevFrameButton, !isSprite);
        setButtonVisible(nextFrameButton, !isSprite);
        setButtonVisible(playAnimationButton, !isSprite);
        fpsSlider.setActive(!isSprite);

        // Text visibility
        frameInfoText.setActive(!isSprite);
        fpsText.setActive(!isSprite);
    }

    private void setButtonVisible(ButtonObj button, boolean visible) {
        if (button == null) return;
        button.setActive(visible);
        if (button.getText() != null) {
            button.getText().setActive(visible);
        }
    }

    private void setupToolButtons() {
        int toolY = paletteY + 60;
        int toolSize = 40;
        int toolSpacing = 10;

        brushButton = new ToolButton(
                new Rectangle(palette2X, toolY, toolSize, toolSize),
                new Color(100, 150, 255),
                () -> selectTool(Tool.BRUSH),
                "B"
        );
        objectManager.add(brushButton);

        eraserButton = new ToolButton(
                new Rectangle(palette2X + toolSize + toolSpacing, toolY, toolSize, toolSize),
                new Color(255, 100, 100),
                () -> selectTool(Tool.ERASER),
                "E"
        );
        objectManager.add(eraserButton);

        bucketButton = new ToolButton(
                new Rectangle(palette2X + (toolSize + toolSpacing) * 2, toolY, toolSize, toolSize),
                new Color(100, 255, 100),
                () -> selectTool(Tool.BUCKET),
                "F"
        );
        objectManager.add(bucketButton);

        brushButton.setSelected(true);
    }

    private void selectTool(Tool tool) {
        currentTool = tool;
        brushButton.setSelected(tool == Tool.BRUSH);
        eraserButton.setSelected(tool == Tool.ERASER);
        bucketButton.setSelected(tool == Tool.BUCKET);
        updateToolText();
    }

    private void updateToolText() {
        String toolName = switch (currentTool) {
            case BRUSH -> "Brush";
            case ERASER -> "Eraser";
            case BUCKET -> "Bucket Fill";
        };
        toolText.setText("Tool: " + toolName);
    }

    private void setupTextElements() {
        // Titel
        titleText = new TextObj(
                "Sprite Editor",
                new Vector2(SCREEN_WIDTH / 2f, 30),
                Color.WHITE,
                new Font("Arial", Font.BOLD, 32)
        );
        titleText.setAlignment(TextObj.TextAlignment.CENTER);
        objectManager.add(titleText);

        // Aktuelles Sprite/Animation
        currentSpriteText = new TextObj(
                "Current: " + currentSpriteName,
                new Vector2(palette2X, paletteY + 20),
                Color.CYAN,
                new Font("Arial", Font.PLAIN, 14)
        );
        objectManager.add(currentSpriteText);

        // Current Tool
        toolText = new TextObj(
                "Tool: Brush",
                new Vector2(palette2X, paletteY + 40),
                Color.YELLOW,
                new Font("Arial", Font.PLAIN, 14)
        );
        objectManager.add(toolText);

        // Frame Info (Animation Mode)
        frameInfoText = new TextObj(
                "Frame: 1/1",
                new Vector2(palette2X, paletteY + 120),
                Color.ORANGE,
                new Font("Arial", Font.BOLD, 14)
        );
        objectManager.add(frameInfoText);

        // FPS Info (Animation Mode)
        fpsText = new TextObj(
                "FPS: 10",
                new Vector2(palette2X, paletteY + 155),
                Color.WHITE,
                new Font("Arial", Font.PLAIN, 14)
        );
        objectManager.add(fpsText);

        // Brightness Label
        brightnessText = new TextObj(
                "Brightness: 100%",
                new Vector2(paletteX, paletteY + 270),
                Color.WHITE,
                new Font("Arial", Font.PLAIN, 14)
        );
        objectManager.add(brightnessText);

        // Shortcuts Info
        String shortcuts = "B/E/F (Tools) | C (Clear) | Ctrl+S/O/N | Right Click (Undo) | Tab (Switch Mode) | Arrow Keys (Frames)";
        shortcutsText = new TextObj(
                shortcuts,
                new Vector2(10, SCREEN_HEIGHT - 20),
                Color.GRAY,
                new Font("Arial", Font.PLAIN, 12)
        );
        objectManager.add(shortcutsText);
    }

    private void setupColorButtons() {
        colorButtons = new ColorButton[palette.length];

        for (int i = 0; i < palette.length; i++) {
            int row = i / 4;
            int col = i % 4;

            int x = paletteX + col * (buttonSize + buttonSpacing);
            int y = paletteY + row * (buttonSize + buttonSpacing);

            Color color = palette[i];
            colorButtons[i] = new ColorButton(
                    new Rectangle(x, y, buttonSize, buttonSize),
                    color,
                    () -> selectColor(color)
            );

            objectManager.add(colorButtons[i]);
        }
    }

    private void setupBrightnessSlider() {
        int sliderY = paletteY + 290;
        brightnessSlider = new SliderObj(paletteX, sliderY, 200, 20, 0f, 2f, 1f, this::onBrightnessChanged);
        brightnessSlider.setGradient(Color.BLACK, Color.WHITE)
                .setHandleColor(Color.YELLOW)
                .setBorderColor(Color.WHITE);
        objectManager.add(brightnessSlider);
    }

    private void onBrightnessChanged(float value) {
        brightness = value;
        updateSelectedColor();
        brightnessText.setText(String.format("Brightness: %d%%", (int)(brightness * 100)));
    }

    private void updateSelectedColor() {
        selectedColor = applyBrightness(baseColor, brightness);
    }

    private Color applyBrightness(Color color, float brightness) {
        if (brightness < 1.0f) {
            int r = (int)(color.getRed() * brightness);
            int g = (int)(color.getGreen() * brightness);
            int b = (int)(color.getBlue() * brightness);
            return new Color(
                    Math.max(0, Math.min(255, r)),
                    Math.max(0, Math.min(255, g)),
                    Math.max(0, Math.min(255, b)),
                    color.getAlpha()
            );
        } else {
            float factor = brightness - 1.0f;
            int r = color.getRed() + (int)((255 - color.getRed()) * factor);
            int g = color.getGreen() + (int)((255 - color.getGreen()) * factor);
            int b = color.getBlue() + (int)((255 - color.getBlue()) * factor);
            return new Color(
                    Math.max(0, Math.min(255, r)),
                    Math.max(0, Math.min(255, g)),
                    Math.max(0, Math.min(255, b)),
                    color.getAlpha()
            );
        }
    }

    private void setupFileButtons() {
        int btnY = paletteY + 340;
        int btnWidth = 120;
        int btnHeight = 40;

        // Sprite Mode Buttons
        saveButton = new ButtonObj(
                new Rectangle(paletteX, btnY, btnWidth, btnHeight),
                Color.GREEN,
                this::saveSprite,
                "Save",
                new Font("Arial", Font.BOLD, 20),
                Color.BLACK
        );
        objectManager.add(saveButton);

        loadButton = new ButtonObj(
                new Rectangle(paletteX, btnY + 50, btnWidth, btnHeight),
                Color.BLUE,
                this::loadSprite,
                "Load",
                new Font("Arial", Font.BOLD, 20),
                Color.WHITE
        );
        objectManager.add(loadButton);

        newButton = new ButtonObj(
                new Rectangle(paletteX, btnY + 100, btnWidth, btnHeight),
                Color.ORANGE,
                this::newSprite,
                "New",
                new Font("Arial", Font.BOLD, 20),
                Color.BLACK
        );
        objectManager.add(newButton);
    }

    private void setupAnimationButtons() {
        int btnY = paletteY + 340;
        int btnWidth = 120;
        int btnHeight = 40;
        int smallBtnWidth = 55;

        // Animation Mode Buttons
        saveAnimationButton = new ButtonObj(
                new Rectangle(paletteX, btnY, btnWidth, btnHeight),
                Color.GREEN,
                this::saveAnimation,
                "Save Anim",
                new Font("Arial", Font.BOLD, 16),
                Color.BLACK
        );
        objectManager.add(saveAnimationButton);

        loadAnimationButton = new ButtonObj(
                new Rectangle(paletteX, btnY + 50, btnWidth, btnHeight),
                Color.BLUE,
                this::loadAnimation,
                "Load Anim",
                new Font("Arial", Font.BOLD, 16),
                Color.WHITE
        );
        objectManager.add(loadAnimationButton);

        newAnimationButton = new ButtonObj(
                new Rectangle(paletteX, btnY + 100, btnWidth, btnHeight),
                Color.ORANGE,
                this::newAnimation,
                "New Anim",
                new Font("Arial", Font.BOLD, 16),
                Color.BLACK
        );
        objectManager.add(newAnimationButton);

        // Frame Controls
        int frameY = btnY + 150;
        newFrameButton = new ButtonObj(
                new Rectangle(paletteX, frameY, btnWidth, 35),
                new Color(100, 200, 100),
                this::newFrame,
                "New Frame",
                new Font("Arial", Font.BOLD, 14),
                Color.BLACK
        );
        objectManager.add(newFrameButton);

        deleteFrameButton = new ButtonObj(
                new Rectangle(paletteX, frameY + 40, btnWidth, 35),
                new Color(200, 100, 100),
                this::deleteFrame,
                "Delete Frame",
                new Font("Arial", Font.BOLD, 14),
                Color.WHITE
        );
        objectManager.add(deleteFrameButton);

        // Navigation
        int navY = frameY + 85;
        prevFrameButton = new ButtonObj(
                new Rectangle(paletteX, navY, smallBtnWidth, 35),
                new Color(150, 150, 200),
                this::prevFrame,
                "<<",
                new Font("Arial", Font.BOLD, 18),
                Color.WHITE
        );
        objectManager.add(prevFrameButton);

        nextFrameButton = new ButtonObj(
                new Rectangle(paletteX + smallBtnWidth + 10, navY, smallBtnWidth, 35),
                new Color(150, 150, 200),
                this::nextFrame,
                ">>",
                new Font("Arial", Font.BOLD, 18),
                Color.WHITE
        );
        objectManager.add(nextFrameButton);

        // Play Button
        playAnimationButton = new ButtonObj(
                new Rectangle(paletteX, navY + 45, btnWidth, 35),
                new Color(100, 255, 100),
                this::togglePlayAnimation,
                "Play",
                new Font("Arial", Font.BOLD, 14),
                Color.BLACK
        );
        objectManager.add(playAnimationButton);

        // FPS Slider
        int fpsY = paletteY + 175;
        fpsSlider = new SliderObj(palette2X, fpsY, 150, 20, 1f, 30f, 10f, this::onFPSChanged);
        fpsSlider.setGradient(Color.DARK_GRAY, Color.GREEN)
                .setHandleColor(Color.ORANGE)
                .setBorderColor(Color.WHITE);
        objectManager.add(fpsSlider);
    }

    private void onFPSChanged(float value) {
        int fps = (int)value;
        frameDuration = 1.0f / fps;
        fpsText.setText("FPS: " + fps);
    }

    // ===== SPRITE MODE FUNCTIONS =====

    private void saveSprite() {
        String name = javax.swing.JOptionPane.showInputDialog(
                null,
                "Sprite Name:",
                currentSpriteName
        );

        if (name != null && !name.trim().isEmpty()) {
            currentSpriteName = name;
            spriteManager.saveSprite(name, pixels);
            updateCurrentNameText();
            flashSaveConfirmation();
        }
    }

    private void loadSprite() {
        java.util.List<String> names = spriteManager.getSpriteNames();

        if (names.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(null, "No sprites found!");
            return;
        }

        String[] options = names.toArray(new String[0]);
        String selected = (String) javax.swing.JOptionPane.showInputDialog(
                null,
                "Select Sprite:",
                "Load Sprite",
                javax.swing.JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );

        if (selected != null) {
            Color[][] loadedPixels = spriteManager.loadSprite(selected);
            if (loadedPixels != null) {
                pixels = loadedPixels;
                currentSpriteName = selected;
                updateCurrentNameText();
            }
        }
    }

    private void newSprite() {
        clearSprite();
        currentSpriteName = "untitled";
        updateCurrentNameText();
        actionsStack.clear();
    }

    // ===== ANIMATION MODE FUNCTIONS =====

    private void saveAnimation() {
        String name = javax.swing.JOptionPane.showInputDialog(
                null,
                "Animation Name:",
                currentAnimationName
        );

        if (name != null && !name.trim().isEmpty()) {
            currentAnimationName = name;
            animationManager.saveFrame(name, currentFrame, pixels);
            updateCurrentNameText();
            flashSaveConfirmation();
        }
    }

    private void loadAnimation() {
        java.util.List<String> names = animationManager.getAnimationNames();

        if (names.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(null, "No animations found!");
            return;
        }

        String[] options = names.toArray(new String[0]);
        String selected = (String) javax.swing.JOptionPane.showInputDialog(
                null,
                "Select Animation:",
                "Load Animation",
                javax.swing.JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );

        if (selected != null) {
            currentAnimationName = selected;
            totalFrames = animationManager.getFrameCount(selected);
            currentFrame = 0;
            loadCurrentFrame();
            updateCurrentNameText();
            updateFrameInfo();
        }
    }

    private void newAnimation() {
        String name = javax.swing.JOptionPane.showInputDialog(
                null,
                "New Animation Name:",
                "untitled_anim"
        );

        if (name != null && !name.trim().isEmpty()) {
            currentAnimationName = name;
            currentFrame = 0;
            totalFrames = 1;
            clearSprite();
            animationManager.createAnimation(name);
            updateCurrentNameText();
            updateFrameInfo();
            actionsStack.clear();
        }
    }

    private void newFrame() {
        // Save current frame
        animationManager.saveFrame(currentAnimationName, currentFrame, pixels);

        // Create new frame (copy of current)
        totalFrames++;
        currentFrame = totalFrames - 1;

        // pixels stays the same (copy of last frame)
        animationManager.saveFrame(currentAnimationName, currentFrame, pixels);

        updateFrameInfo();
        actionsStack.clear();
    }

    private void deleteFrame() {
        if (totalFrames <= 1) {
            javax.swing.JOptionPane.showMessageDialog(null, "Cannot delete the last frame!");
            return;
        }

        animationManager.deleteFrame(currentAnimationName, currentFrame);
        totalFrames--;

        if (currentFrame >= totalFrames) {
            currentFrame = totalFrames - 1;
        }

        loadCurrentFrame();
        updateFrameInfo();
    }

    private void prevFrame() {
        if (totalFrames <= 0) return;

        // Save current frame before switching
        animationManager.saveFrame(currentAnimationName, currentFrame, pixels);

        currentFrame--;
        if (currentFrame < 0) currentFrame = totalFrames - 1;

        loadCurrentFrame();
        updateFrameInfo();
        actionsStack.clear();
    }

    private void nextFrame() {
        if (totalFrames <= 0) return;

        // Save current frame before switching
        animationManager.saveFrame(currentAnimationName, currentFrame, pixels);

        currentFrame++;
        if (currentFrame >= totalFrames) currentFrame = 0;

        loadCurrentFrame();
        updateFrameInfo();
        actionsStack.clear();
    }

    private void loadCurrentFrame() {
        Color[][] loaded = animationManager.loadFrame(currentAnimationName, currentFrame);
        if (loaded != null) {
            pixels = loaded;
        } else {
            clearSprite();
        }
    }

    private void togglePlayAnimation() {
        isPlaying = !isPlaying;
        playAnimationButton.setText(isPlaying ? "Stop" : "Play");
        playAnimationButton.setColor(isPlaying ? new Color(255, 100, 100) : new Color(100, 255, 100));
    }

    private void updateFrameInfo() {
        frameInfoText.setText("Frame: " + (currentFrame + 1) + "/" + totalFrames);
    }

    private void updateCurrentNameText() {
        if (currentMode == EditorMode.SPRITE) {
            currentSpriteText.setText("Current: " + currentSpriteName);
        } else {
            currentSpriteText.setText("Anim: " + currentAnimationName);
        }
    }

    private void flashSaveConfirmation() {
        currentSpriteText.setColor(Color.GREEN);
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        currentSpriteText.setColor(Color.CYAN);
                    }
                },
                1000
        );
    }

    private void selectColor(Color color) {
        baseColor = color;
        updateSelectedColor();
        for (ColorButton btn : colorButtons) {
            btn.setSelected(btn.color.equals(baseColor));
        }
    }

    private void clearSprite() {
        for (int y = 0; y < 32; y++) {
            for (int x = 0; x < 32; x++) {
                pixels[x][y] = new Color(0, 0, 0, 0);
            }
        }
    }

    @Override
    protected void update() {
        objectManager.update(deltaTime);

        // Animation Preview
        if (currentMode == EditorMode.ANIMATION && isPlaying && totalFrames > 1) {
            animationTimer += deltaTime;
            if (animationTimer >= frameDuration) {
                animationTimer = 0;
                currentFrame = (currentFrame + 1) % totalFrames;
                loadCurrentFrame();
                updateFrameInfo();
            }
        }

        if (Input.getMouseButton(Input.MouseCode.LEFT)) {
            int gridX = (Input.getMousePosition().xToInt() - gridOffsetX) / pixelSize;
            int gridY = (Input.getMousePosition().yToInt() - gridOffsetY) / pixelSize;

            if (gridX >= 0 && gridX < 32 && gridY >= 0 && gridY < 32) {
                if (currentTool == Tool.BUCKET) {
                    if (lastPos.x == -1 || lastPos.y == -1) {
                        bucketFill(gridX, gridY, pixels[gridX][gridY], selectedColor);
                    }
                    lastPos.x = gridX;
                    lastPos.y = gridY;
                } else {
                    if (lastPos.x == -1 || lastPos.y == -1) {
                        lastPos.x = gridX;
                        lastPos.y = gridY;
                    }

                    Color drawColor = currentTool == Tool.ERASER ? new Color(0, 0, 0, 0) : selectedColor;
                    drawLineBetween(lastPos.xToInt(), lastPos.yToInt(), gridX, gridY, drawColor);

                    lastPos.x = gridX;
                    lastPos.y = gridY;
                }
            }
        } else {
            lastPos.x = -1;
            lastPos.y = -1;
        }

        // Keyboard Shortcuts
        if (Input.getKeyDown(Input.KeyCode.C)) clearSprite();
        if (Input.getKeyDown(Input.KeyCode.B)) selectTool(Tool.BRUSH);
        if (Input.getKeyDown(Input.KeyCode.E)) selectTool(Tool.ERASER);
        if (Input.getKeyDown(Input.KeyCode.F)) selectTool(Tool.BUCKET);
        if (Input.getKeyDown(Input.KeyCode.TAB)) {
            switchMode(currentMode == EditorMode.SPRITE ? EditorMode.ANIMATION : EditorMode.SPRITE);
        }

        // Animation Frame Navigation
        if (currentMode == EditorMode.ANIMATION && !isPlaying) {
            if (Input.getKeyDown(Input.KeyCode.LEFT)) prevFrame();
            if (Input.getKeyDown(Input.KeyCode.RIGHT)) nextFrame();
        }

        // Undo
        if (Input.getMouseButton(Input.MouseCode.RIGHT)) {
            undoSteps++;
            if (!actionsStack.isEmpty() && (undoSteps == 0 || undoSteps >= undoClickThreshold)) {
                Action action = actionsStack.pop();
                pixels[action.pos.xToInt()][action.pos.yToInt()] = action.color;
            }
        } else undoSteps = -1;

        // File Shortcuts
        if (Input.getKey(Input.KeyCode.CONTROL)) {
            if (Input.getKeyDown(Input.KeyCode.S)) {
                if (currentMode == EditorMode.SPRITE) saveSprite();
                else saveAnimation();
            }
            if (Input.getKeyDown(Input.KeyCode.O)) {
                if (currentMode == EditorMode.SPRITE) loadSprite();
                else loadAnimation();
            }
            if (Input.getKeyDown(Input.KeyCode.N)) {
                if (currentMode == EditorMode.SPRITE) newSprite();
                else newAnimation();
            }
        }
    }

    private void bucketFill(int x, int y, Color targetColor, Color replacementColor) {
        if (colorsEqual(targetColor, replacementColor)) return;

        Stack<Vector2> stack = new Stack<>();
        stack.push(new Vector2(x, y));

        while (!stack.isEmpty()) {
            Vector2 pos = stack.pop();
            int px = pos.xToInt();
            int py = pos.yToInt();

            if (px < 0 || px >= 32 || py < 0 || py >= 32) continue;
            if (!colorsEqual(pixels[px][py], targetColor)) continue;

            actionsStack.push(new Action(new Vector2(px, py), pixels[px][py]));
            pixels[px][py] = replacementColor;

            stack.push(new Vector2(px + 1, py));
            stack.push(new Vector2(px - 1, py));
            stack.push(new Vector2(px, py + 1));
            stack.push(new Vector2(px, py - 1));
        }
    }

    private boolean colorsEqual(Color c1, Color c2) {
        return c1.getRed() == c2.getRed() &&
                c1.getGreen() == c2.getGreen() &&
                c1.getBlue() == c2.getBlue() &&
                c1.getAlpha() == c2.getAlpha();
    }

    private void drawLineBetween(int x0, int y0, int x1, int y1, Color color) {
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;

        while (true) {
            if (x0 >= 0 && x0 < 32 && y0 >= 0 && y0 < 32) {
                if (!colorsEqual(pixels[x0][y0], color)) {
                    actionsStack.push(new Action(new Vector2(x0, y0), pixels[x0][y0]));
                    pixels[x0][y0] = color;
                }
            }

            if (x0 == x1 && y0 == y1) break;

            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x0 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y0 += sy;
            }
        }
    }

    @Override
    protected void draw(Graphics2D g) {
        // Grid
        for (int y = 0; y < 32; y++) {
            for (int x = 0; x < 32; x++) {
                g.setColor(pixels[x][y]);
                g.fillRect(
                        gridOffsetX + x * pixelSize,
                        gridOffsetY + y * pixelSize,
                        pixelSize,
                        pixelSize
                );

                g.setColor(new Color(100, 100, 100, 100));
                g.drawRect(
                        gridOffsetX + x * pixelSize,
                        gridOffsetY + y * pixelSize,
                        pixelSize,
                        pixelSize
                );
            }
        }

        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(3));
        g.drawRect(gridOffsetX, gridOffsetY, 32 * pixelSize, 32 * pixelSize);
        g.setStroke(new BasicStroke(1));

        // Selected Color Indicator
        g.setColor(Color.WHITE);
        g.drawString("Selected:", paletteX, paletteY - 10);
        g.setColor(selectedColor);
        g.fillRect(paletteX + 70, paletteY - 20, 40, 15);
        g.setColor(Color.WHITE);
        g.drawRect(paletteX + 70, paletteY - 20, 40, 15);

        objectManager.draw(g);
    }

    private class ColorButton extends ButtonObj {
        private Color color;
        private boolean selected = false;

        public ColorButton(Rectangle rect, Color color, Runnable onClick) {
            super(rect, color, onClick, "", new Font("Arial", Font.PLAIN, 1), Color.BLACK);
            this.color = color;

            if (color.equals(Color.WHITE)) {
                selected = true;
            }
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        @Override
        public void draw(Graphics2D g) {
            drawGOasFilledRect(color);

            if (selected) {
                g.setColor(Color.YELLOW);
                g.setStroke(new BasicStroke(4));
            } else {
                g.setColor(Color.GRAY);
                g.setStroke(new BasicStroke(2));
            }

            g.drawRect(
                    transform.position.xToInt(),
                    transform.position.yToInt(),
                    transform.scale.xToInt(),
                    transform.scale.yToInt()
            );

            g.setStroke(new BasicStroke(1));
        }
    }

    private class ToolButton extends ButtonObj {
        private boolean selected = false;

        public ToolButton(Rectangle rect, Color color, Runnable onClick, String label) {
            super(rect, color, onClick, label, new Font("Arial", Font.BOLD, 18), Color.WHITE);
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        @Override
        public void draw(Graphics2D g) {
            super.draw(g);

            if (selected) {
                g.setColor(Color.YELLOW);
                g.setStroke(new BasicStroke(3));
                g.drawRect(
                        transform.position.xToInt(),
                        transform.position.yToInt(),
                        transform.scale.xToInt(),
                        transform.scale.yToInt()
                );
                g.setStroke(new BasicStroke(1));
            }
        }
    }
}