package GameEngine.Core.gameObject.Obj;

import GameEngine.Core.gameObject.FuncInt.*;
import GameEngine.Core.gameObject.GameObject;
import GameEngine.Core.input.Input;
import GameEngine.Core.util.Vector2;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

/**
 * A slider UI component for selecting values within a range.
 * Supports horizontal dragging, value callbacks, and customizable appearance.
 * Use the Builder pattern to create instances.
 */
public class Slider extends GameObject {
    //<editor-fold desc="VARIABLES">
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
    private int cornerRadius = 0;

    /**
     * Determines how the slider handle is drawn.
     */
    public enum HandleShape {
        RECTANGLE,
        CIRCLE
    }

    private HandleShape handleShape = HandleShape.RECTANGLE;

    // Verhalten
    private boolean dragging = false;
    private FuncIntOne<Float> onValueChanged;
    private FuncInt onDragStart;
    private FuncInt onDragEnd;
    private FuncIntOne<Float> onDragStartValue;
    private FuncIntOne<Float> onDragEndValue;

    // Text
    private boolean showValue = false;
    private String label = "";
    private Font font = new Font("Arial", Font.PLAIN, 12);
    //</editor-fold>

    //<editor-fold desc="CONSTRUCTOR/BUILDER">
    /**
     * Private Konstruktor
     */
    private Slider(int x, int y, int width, int height, float minValue, float maxValue, float startValue) {
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
        private int cornerRadius = 0;
        private HandleShape handleShape = HandleShape.RECTANGLE;
        private FuncIntOne<Float> onValueChanged;
        private FuncInt onDragStart;
        private FuncIntOne<Float> onDragStartValue;
        private FuncInt onDragEnd;
        private FuncIntOne<Float> onDragEndValue;

        public  Builder() {}

        public Builder pos(Vector2 position) {
            this.x = position.xToInt();
            this.y = position.yToInt();
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

        public Builder cornerRadius(int radius) {
            this.cornerRadius = Math.max(0, radius);
            return this;
        }

        /**
         * Sets the shape of the slider handle.
         * - RECTANGLE: current default look
         * - CIRCLE: a circle that fits exactly inside the slider track height
         */
        public Builder handleShape(HandleShape shape) {
            if (shape != null) {
                this.handleShape = shape;
            }
            return this;
        }

        public Builder onValueChanged(FuncIntOne<Float> callback) {
            this.onValueChanged = callback;
            return this;
        }

        public Builder onDragStart(FuncInt callback) {
            this.onDragStart = callback;
            return this;
        }

        public Builder onDragStartValue(FuncIntOne<Float> callback) {
            this.onDragStartValue = callback;
            return this;
        }

        public Builder onDragEnd(FuncInt callback) {
            this.onDragEnd = callback;
            return this;
        }

        public Builder onDragEndValue(FuncIntOne<Float> callback) {
            this.onDragEndValue = callback;
            return this;
        }

        public Slider build() {
            Slider slider = new Slider(x, y, width, height, minValue, maxValue, startValue);
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
            slider.cornerRadius = cornerRadius;
            slider.handleShape = handleShape;
            slider.onValueChanged = onValueChanged;
            slider.onDragStart = onDragStart;
            slider.onDragStartValue = onDragStartValue;
            slider.onDragEnd = onDragEnd;
            slider.onDragEndValue = onDragEndValue;
            return slider;
        }
    }
    //</editor-fold>

    //<editor-fold desc="SETTER">
    /**
     * Sets the background color of the slider track.
     *
     * @param color The background color
     * @return This slider for chaining
     */
    public Slider setBackgroundColor(Color color) {
        this.backgroundColor = color;
        return this;
    }

    /**
     * Sets the fill color of the slider (the filled portion).
     *
     * @param color The fill color
     * @return This slider for chaining
     */
    public Slider setFillColor(Color color) {
        this.fillColor = color;
        return this;
    }

    /**
     * Sets the handle/knob color.
     *
     * @param color The handle color
     * @return This slider for chaining
     */
    public Slider setHandleColor(Color color) {
        this.handleColor = color;
        return this;
    }

    /**
     * Sets the border color.
     *
     * @param color The border color
     * @return This slider for chaining
     */
    public Slider setBorderColor(Color color) {
        this.borderColor = color;
        return this;
    }

    /**
     * Enables gradient fill and sets the gradient colors.
     *
     * @param startColor The start color of the gradient
     * @param endColor The end color of the gradient
     * @return This slider for chaining
     */
    public Slider setGradient(Color startColor, Color endColor) {
        this.showGradient = true;
        this.gradientStartColor = startColor;
        this.gradientEndColor = endColor;
        return this;
    }

    /**
     * Disables gradient fill.
     *
     * @return This slider for chaining
     */
    public Slider removeGradient() {
        this.showGradient = false;
        return this;
    }

    /**
     * Sets whether to show the current value as text.
     *
     * @param show true to show the value
     * @return This slider for chaining
     */
    public Slider setShowValue(boolean show) {
        this.showValue = show;
        return this;
    }

    /**
     * Sets the label text displayed next to the slider.
     *
     * @param label The label text
     * @return This slider for chaining
     */
    public Slider setLabel(String label) {
        this.label = label;
        return this;
    }

    /**
     * Sets the font for label and value text.
     *
     * @param font The font to use
     * @return This slider for chaining
     */
    public Slider setFont(Font font) {
        this.font = font;
        return this;
    }

    /**
     * Sets the callback for when the value changes.
     *
     * @param callback The callback receiving the new value
     * @return This slider for chaining
     */
    public Slider setOnValueChanged(FuncIntOne<Float> callback) {
        this.onValueChanged = callback;
        return this;
    }

    /**
     * Sets the callback for when dragging starts.
     *
     * @param callback The callback to execute
     * @return This slider for chaining
     */
    public Slider setOnDragStart(FuncInt callback) {
        this.onDragStart = callback;
        return this;
    }

    /**
     * Sets the callback for when dragging ends.
     *
     * @param callback The callback to execute
     * @return This slider for chaining
     */
    public Slider setOnDragEnd(FuncInt callback) {
        this.onDragEnd = callback;
        return this;
    }

    /**
     * Sets the current value of the slider.
     *
     * @param value The new value (will be clamped to min/max range)
     * @return This slider for chaining
     */
    public Slider setValue(float value) {
        float oldValue = this.value;
        this.value = Math.max(minValue, Math.min(maxValue, value));
        if (oldValue != this.value && onValueChanged != null) {
            onValueChanged.call(this.value);
        }
        return this;
    }

    /**
     * Sets the minimum value of the slider range.
     *
     * @param min The minimum value
     * @return This slider for chaining
     */
    public Slider setMinValue(float min) {
        this.minValue = min;
        if (value < min) setValue(min);
        return this;
    }

    /**
     * Sets the maximum value of the slider range.
     *
     * @param max The maximum value
     * @return This slider for chaining
     */
    public Slider setMaxValue(float max) {
        this.maxValue = max;
        if (value > max) setValue(max);
        return this;
    }

    /**
     * Sets both minimum and maximum values.
     *
     * @param min The minimum value
     * @param max The maximum value
     * @return This slider for chaining
     */
    public Slider setRange(float min, float max) {
        this.minValue = min;
        this.maxValue = max;
        setValue(value); // Clamp to new range
        return this;
    }

    /**
     * Sets the position of the slider.
     *
     * @param x The x position
     * @param y The y position
     * @return This slider for chaining
     */
    public Slider setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    /**
     * Sets the size of the slider.
     *
     * @param width The width
     * @param height The height
     * @return This slider for chaining
     */
    public Slider setSize(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    /**
     * Sets the corner radius for rounded corners.
     *
     * @param radius The corner radius (minimum 0)
     * @return This slider for chaining
     */
    public Slider setCornerRadius(int radius) {
        this.cornerRadius = Math.max(0, radius);
        return this;
    }

    /**
     * Returns the corner radius.
     *
     * @return The corner radius
     */
    public int getCornerRadius() {
        return cornerRadius;
    }

    /**
     * Sets the shape of the handle.
     * - RECTANGLE: current default look
     * - CIRCLE: a circle that fits exactly inside the slider track height
     */
    public Slider setHandleShape(HandleShape shape) {
        if (shape != null) {
            this.handleShape = shape;
        }
        return this;
    }
    //</editor-fold>

    //<editor-fold desc="GETTER METHODS">
    /**
     * Returns the current value of the slider.
     *
     * @return The current value
     */
    public float getValue() {
        return value;
    }

    /**
     * Returns the minimum value of the range.
     *
     * @return The minimum value
     */
    public float getMinValue() {
        return minValue;
    }

    /**
     * Returns the maximum value of the range.
     *
     * @return The maximum value
     */
    public float getMaxValue() {
        return maxValue;
    }

    /**
     * Returns the normalized value (0 to 1).
     *
     * @return The normalized value
     */
    public float getNormalizedValue() {
        return (value - minValue) / (maxValue - minValue);
    }

    /**
     * Returns the value as an integer (rounded).
     *
     * @return The value as int
     */
    public int getValueAsInt() {
        return Math.round(value);
    }

    /**
     * Returns the value as a percentage (0 to 100).
     *
     * @return The value as percent
     */
    public int getValueAsPercent() {
        return (int)(getNormalizedValue() * 100);
    }

    /**
     * Checks if the slider is currently being dragged.
     *
     * @return true if dragging
     */
    public boolean isDragging() {
        return dragging;
    }
    //</editor-fold>

    //<editor-fold desc="UPDATE/DRAW">
    @Override
    public void init() {

    }

    @Override
    public void update(double deltaTime) {
        // Prüfe ob ein Dropdown offen ist - dann keine Interaktion (außer wenn bereits dragging)
        if (Dropdown.isAnyDropdownExpanded() && !dragging) {
            return;
        }

        Vector2 mousePos = Input.getMousePosition();
        boolean mousePressed = Input.getMouseButton(Input.MouseCode.LEFT);
        boolean wasPressed = Input.getMouseLast(Input.MouseCode.LEFT);

        if (mousePressed) {
            if (!dragging && isMouseOver(mousePos) && !wasPressed) {
                dragging = true;
                if (onDragStart != null) {
                    onDragStart.call();
                }
                if (onDragStartValue != null) {
                    onDragStartValue.call(value);
                }
            }

            if (dragging) {
                updateValueFromMouse(mousePos);
            }
        } else {
            if (dragging) {
                dragging = false;
                if (onDragEnd != null) {
                    onDragEnd.call();
                }
                if (onDragEndValue != null) {
                    onDragEndValue.call(value);
                }
            }
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
        // Anti-Aliasing für runde Ecken
        if (cornerRadius > 0 || handleShape == HandleShape.CIRCLE) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }

        // Hintergrund
        if (showGradient) {
            drawGradientBackground(g);
        } else {
            if (cornerRadius > 0) {
                // Hintergrund mit runden Ecken
                g.setColor(backgroundColor);
                RoundRectangle2D bgRect = new RoundRectangle2D.Float(
                        x, y, width, height, cornerRadius * 2, cornerRadius * 2
                );
                g.fill(bgRect);

                // Fill (bis zum aktuellen Wert) mit runden Ecken
                int fillWidth = (int) (getNormalizedValue() * width);
                if (fillWidth > 0) {
                    g.setColor(fillColor);
                    // Clip auf den Fill-Bereich für saubere runde Ecken
                    Shape oldClip = g.getClip();
                    g.setClip(x, y, fillWidth, height);
                    g.fill(bgRect);
                    g.setClip(oldClip);
                }
            } else {
                // Hintergrund ohne runde Ecken
                g.setColor(backgroundColor);
                g.fillRect(x, y, width, height);

                // Fill (bis zum aktuellen Wert)
                int fillWidth = (int) (getNormalizedValue() * width);
                g.setColor(fillColor);
                g.fillRect(x, y, fillWidth, height);
            }
        }

        // Border
        g.setColor(borderColor);
        if (cornerRadius > 0) {
            RoundRectangle2D borderRect = new RoundRectangle2D.Float(
                    x, y, width, height, cornerRadius * 2, cornerRadius * 2
            );
            g.draw(borderRect);
        } else {
            g.drawRect(x, y, width, height);
        }

        // Handle (Regler)
        int handleCenterX = x + (int) (getNormalizedValue() * width);

        if (handleShape == HandleShape.CIRCLE) {
            // Circle fits exactly inside the track height.
            int diameter = Math.max(1, height);
            int radius = diameter / 2;

            // Clamp the *center* so the circle doesn't stick out beyond the track bounds.
            // This keeps value/range math unchanged (only rendering is clamped).
            int minCenterX = x + radius;
            int maxCenterX = x + width - radius;
            int clampedCenterX = Math.max(minCenterX, Math.min(maxCenterX, handleCenterX));

            int topY = y + (height - diameter) / 2; // usually 0
            int leftX = clampedCenterX - radius;

            Shape circle = new Ellipse2D.Float(leftX, topY, diameter, diameter);
            g.setColor(handleColor);
            g.fill(circle);
            g.setColor(borderColor);
            g.draw(circle);
        } else {
            // Rectangle (legacy look)
            int handleWidth = 6;
            int handleHeight = height + 4;

            g.setColor(handleColor);
            g.fillRect(handleCenterX - handleWidth / 2, y - 2, handleWidth, handleHeight);
            g.setColor(borderColor);
            g.drawRect(handleCenterX - handleWidth / 2, y - 2, handleWidth, handleHeight);
        }

        // Anti-Aliasing wieder ausschalten
        if (cornerRadius > 0 || handleShape == HandleShape.CIRCLE) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        }

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
            g.drawString(valueText, x + width + 10, y + height / 2 + fm.getAscent() / 2);
        }
    }

    private void drawGradientBackground(Graphics2D g) {
        if (cornerRadius > 0) {
            // Gradient mit runden Ecken
            for (int i = 0; i < width; i++) {
                float t = i / (float) width;

                int r = (int)(gradientStartColor.getRed() + t * (gradientEndColor.getRed() - gradientStartColor.getRed()));
                int gr = (int)(gradientStartColor.getGreen() + t * (gradientEndColor.getGreen() - gradientStartColor.getGreen()));
                int b = (int)(gradientStartColor.getBlue() + t * (gradientEndColor.getBlue() - gradientStartColor.getBlue()));

                g.setColor(new Color(r, gr, b));

                // Clip für runde Ecken
                Shape oldClip = g.getClip();
                RoundRectangle2D clipRect = new RoundRectangle2D.Float(
                        x, y, width, height, cornerRadius * 2, cornerRadius * 2
                );
                g.setClip(clipRect);
                g.drawLine(x + i, y, x + i, y + height);
                g.setClip(oldClip);
            }
        } else {
            // Gradient ohne runde Ecken
            for (int i = 0; i < width; i++) {
                float t = i / (float) width;

                int r = (int)(gradientStartColor.getRed() + t * (gradientEndColor.getRed() - gradientStartColor.getRed()));
                int gr = (int)(gradientStartColor.getGreen() + t * (gradientEndColor.getGreen() - gradientStartColor.getGreen()));
                int b = (int)(gradientStartColor.getBlue() + t * (gradientEndColor.getBlue() - gradientStartColor.getBlue()));

                g.setColor(new Color(r, gr, b));
                g.drawLine(x + i, y, x + i, y + height);
            }
        }
    }

    @Override
    public void onCollision(GameObject collider) {}
    //</editor-fold>
}