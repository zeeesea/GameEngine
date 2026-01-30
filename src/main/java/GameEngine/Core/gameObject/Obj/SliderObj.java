package GameEngine.Core.gameObject.Obj;

import GameEngine.Core.gameObject.FuncInt.*;
import GameEngine.Core.gameObject.GameObject;
import GameEngine.Core.input.Input;
import GameEngine.Core.util.Vector2;

import java.awt.*;

public class SliderObj extends GameObject {
    // Position und Größe
    private int x, y, width, height;

    // Wert
    private float value;
    private float minValue;
    private float maxValue;

    // Visuals
    private Color backgroundColor = new Color(60, 60, 60);
    private Color fillColor = new Color(100, 150, 255);
    private Color handleColor = Color.YELLOW;
    private Color borderColor = Color.WHITE;
    private boolean showGradient = false;
    private Color gradientStartColor = Color.BLACK;
    private Color gradientEndColor = Color.WHITE;

    // Verhalten
    private boolean dragging = false;
    private FuncIntOne<Float> onValueChanged;

    // Text
    private boolean showValue = false;
    private String label = "";
    private Font font = new Font("Arial", Font.PLAIN, 12);

    /**
     * Private Konstruktor - nur über Builder zugänglich
     */
    private SliderObj(int x, int y, int width, int height, float minValue, float maxValue, float startValue) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.value = Math.max(minValue, Math.min(maxValue, startValue));
    }

    /**
     * Builder Pattern für SliderObj
     */
    public static class Builder {
        // Required
        private int x = 0;
        private int y = 0;
        private int width = 200;
        private int height = 20;

        // Optional mit Defaults
        private float minValue = 0f;
        private float maxValue = 1f;
        private float startValue = 0.5f;
        private Color backgroundColor = new Color(60, 60, 60);
        private Color fillColor = new Color(100, 150, 255);
        private Color handleColor = Color.YELLOW;
        private Color borderColor = Color.WHITE;
        private boolean showGradient = false;
        private Color gradientStartColor = Color.BLACK;
        private Color gradientEndColor = Color.WHITE;
        private boolean showValue = false;
        private String label = "";
        private Font font = new Font("Arial", Font.PLAIN, 12);
        private FuncIntOne<Float> onValueChanged;

        public Builder() {}

        public Builder position(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public Builder size(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder range(float min, float max) {
            this.minValue = min;
            this.maxValue = max;
            return this;
        }

        public Builder startValue(float value) {
            this.startValue = value;
            return this;
        }

        public Builder backgroundColor(Color color) {
            this.backgroundColor = color;
            return this;
        }

        public Builder fillColor(Color color) {
            this.fillColor = color;
            return this;
        }

        public Builder handleColor(Color color) {
            this.handleColor = color;
            return this;
        }

        public Builder borderColor(Color color) {
            this.borderColor = color;
            return this;
        }

        public Builder gradient(Color start, Color end) {
            this.showGradient = true;
            this.gradientStartColor = start;
            this.gradientEndColor = end;
            return this;
        }

        public Builder showValue(boolean show) {
            this.showValue = show;
            return this;
        }

        public Builder label(String label) {
            this.label = label;
            return this;
        }

        public Builder font(Font font) {
            this.font = font;
            return this;
        }

        public Builder onValueChanged(FuncIntOne<Float> callback) {
            this.onValueChanged = callback;
            return this;
        }

        public SliderObj build() {
            SliderObj slider = new SliderObj(x, y, width, height, minValue, maxValue, startValue);
            slider.backgroundColor = backgroundColor;
            slider.fillColor = fillColor;
            slider.handleColor = handleColor;
            slider.borderColor = borderColor;
            slider.showGradient = showGradient;
            slider.gradientStartColor = gradientStartColor;
            slider.gradientEndColor = gradientEndColor;
            slider.showValue = showValue;
            slider.label = label;
            slider.font = font;
            slider.onValueChanged = onValueChanged;
            return slider;
        }
    }

    // ===== SETTER METHODS =====

    public SliderObj setBackgroundColor(Color color) {
        this.backgroundColor = color;
        return this;
    }

    public SliderObj setFillColor(Color color) {
        this.fillColor = color;
        return this;
    }

    public SliderObj setHandleColor(Color color) {
        this.handleColor = color;
        return this;
    }

    public SliderObj setBorderColor(Color color) {
        this.borderColor = color;
        return this;
    }

    public SliderObj setGradient(Color startColor, Color endColor) {
        this.showGradient = true;
        this.gradientStartColor = startColor;
        this.gradientEndColor = endColor;
        return this;
    }

    public SliderObj removeGradient() {
        this.showGradient = false;
        return this;
    }

    public SliderObj setShowValue(boolean show) {
        this.showValue = show;
        return this;
    }

    public SliderObj setLabel(String label) {
        this.label = label;
        return this;
    }

    public SliderObj setFont(Font font) {
        this.font = font;
        return this;
    }

    public SliderObj setOnValueChanged(FuncIntOne<Float> callback) {
        this.onValueChanged = callback;
        return this;
    }

    public SliderObj setValue(float value) {
        float oldValue = this.value;
        this.value = Math.max(minValue, Math.min(maxValue, value));
        if (oldValue != this.value && onValueChanged != null) {
            onValueChanged.call(this.value);
        }
        return this;
    }

    public SliderObj setMinValue(float min) {
        this.minValue = min;
        if (value < min) setValue(min);
        return this;
    }

    public SliderObj setMaxValue(float max) {
        this.maxValue = max;
        if (value > max) setValue(max);
        return this;
    }

    public SliderObj setRange(float min, float max) {
        this.minValue = min;
        this.maxValue = max;
        setValue(value); // Clamp to new range
        return this;
    }

    public SliderObj setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public SliderObj setSize(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    // ===== GETTER METHODS =====

    public float getValue() {
        return value;
    }

    public float getMinValue() {
        return minValue;
    }

    public float getMaxValue() {
        return maxValue;
    }

    public float getNormalizedValue() {
        return (value - minValue) / (maxValue - minValue);
    }

    public int getValueAsInt() {
        return Math.round(value);
    }

    public int getValueAsPercent() {
        return (int)(getNormalizedValue() * 100);
    }

    public boolean isDragging() {
        return dragging;
    }

    // ===== UPDATE & DRAW =====

    @Override
    public void init() {

    }

    @Override
    public void update(double deltaTime) {
        Vector2 mousePos = Input.getMousePosition();
        boolean mousePressed = Input.getMouseButton(Input.MouseCode.LEFT);
        boolean wasPressed = Input.getMouseLast(Input.MouseCode.LEFT);

        if (mousePressed) {
            if (!dragging && isMouseOver(mousePos) && !wasPressed) {
                dragging = true;
            }

            if (dragging) {
                updateValueFromMouse(mousePos);
            }
        } else {
            dragging = false;
        }
    }

    private void updateValueFromMouse(Vector2 mousePos) {
        float normalizedX = (mousePos.x - x) / (float) width;
        normalizedX = Math.max(0f, Math.min(1f, normalizedX));

        float newValue = minValue + normalizedX * (maxValue - minValue);
        setValue(newValue);
    }

    private boolean isMouseOver(Vector2 pos) {
        return pos.x >= x && pos.x <= x + width &&
                pos.y >= y && pos.y <= y + height;
    }

    @Override
    public void draw(Graphics2D g) {
        // Hintergrund
        if (showGradient) {
            drawGradientBackground(g);
        } else {
            // Hintergrund
            g.setColor(backgroundColor);
            g.fillRect(x, y, width, height);

            // Fill (bis zum aktuellen Wert)
            int fillWidth = (int)(getNormalizedValue() * width);
            g.setColor(fillColor);
            g.fillRect(x, y, fillWidth, height);
        }

        // Border
        g.setColor(borderColor);
        g.drawRect(x, y, width, height);

        // Handle (Regler)
        int handleX = x + (int)(getNormalizedValue() * width);
        int handleWidth = 6;
        int handleHeight = height + 4;

        g.setColor(handleColor);
        g.fillRect(handleX - handleWidth/2, y - 2, handleWidth, handleHeight);
        g.setColor(borderColor);
        g.drawRect(handleX - handleWidth/2, y - 2, handleWidth, handleHeight);

        // Label
        if (!label.isEmpty()) {
            g.setFont(font);
            g.setColor(Color.WHITE);
            g.drawString(label, x, y - 5);
        }

        // Value Display
        if (showValue) {
            g.setFont(font);
            g.setColor(Color.WHITE);
            String valueText = String.format("%.2f", value);
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(valueText);
            g.drawString(valueText, x + width + 10, y + height/2 + fm.getAscent()/2);
        }
    }

    private void drawGradientBackground(Graphics2D g) {
        for (int i = 0; i < width; i++) {
            float t = i / (float) width;

            int r = (int)(gradientStartColor.getRed() + t * (gradientEndColor.getRed() - gradientStartColor.getRed()));
            int gr = (int)(gradientStartColor.getGreen() + t * (gradientEndColor.getGreen() - gradientStartColor.getGreen()));
            int b = (int)(gradientStartColor.getBlue() + t * (gradientEndColor.getBlue() - gradientStartColor.getBlue()));

            g.setColor(new Color(r, gr, b));
            g.drawLine(x + i, y, x + i, y + height);
        }
    }

    @Override
    public void onCollision(GameObject collider) {}
}
