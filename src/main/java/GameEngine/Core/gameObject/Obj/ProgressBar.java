package GameEngine.Core.gameObject.Obj;

import GameEngine.Core.gameObject.FuncInt.*;
import GameEngine.Core.gameObject.GameObject;
import GameEngine.Core.gameObject.Transform;
import GameEngine.Core.util.Vector2;

import java.awt.*;

public class ProgressBar extends GameObject {
    //<editor-fold desc="VARIABLES">
    private float value = 0f;
    private float targetValue = 0f;
    private boolean animated = true;
    private float animationSpeed = 2f;

    private Color backgroundColor = new Color(40, 40, 40);
    private Color fillColor = new Color(100, 200, 100);
    private Color borderColor = Color.GRAY;
    private boolean showPercentage = true;
    private boolean showLabel = false;
    private String label = "";
    private Color textColor = Color.WHITE;
    private Font font = new Font("Arial", Font.BOLD, 12);

    private boolean useGradient = false;
    private Color gradientStart = Color.GREEN;
    private Color gradientEnd = Color.RED;

    private FuncIntOne<Float> onValueChanged;
    private FuncInt onComplete;
    private FuncInt onEmpty;
    //</editor-fold>

    //<editor-fold desc="CONSTRUCTOR/BUILDER">
    private ProgressBar(Rectangle rect) {
        transform = new Transform(rect);
        renderOrder = 100;
    }

    public static class Builder {
        private Rectangle rect = new Rectangle(0, 0, 200, 20);
        private float initialValue = 0f;
        private boolean animated = true;
        private float animationSpeed = 2f;

        private Color backgroundColor = new Color(40, 40, 40);
        private Color fillColor = new Color(100, 200, 100);
        private Color borderColor = Color.GRAY;
        private boolean showPercentage = true;
        private boolean showLabel = false;
        private String label = "";
        private Color textColor = Color.WHITE;
        private Font font = new Font("Arial", Font.BOLD, 12);

        private boolean useGradient = false;
        private Color gradientStart = Color.GREEN;
        private Color gradientEnd = Color.RED;

        private FuncIntOne<Float> onValueChanged;
        private FuncInt onComplete;
        private FuncInt onEmpty;

        public Builder() {}

        public Builder rect(Rectangle rect) { this.rect = rect; return this; }
        public Builder pos(Vector2 pos) { rect.x = pos.xToInt(); rect.y = pos.yToInt(); return this; }
        public Builder size(Vector2 size) { rect.width = size.xToInt(); rect.height = size.yToInt(); return this; }
        public Builder value(float value) { this.initialValue = Math.max(0f, Math.min(1f, value)); return this; }
        public Builder animated(boolean animated) { this.animated = animated; return this; }
        public Builder animationSpeed(float speed) { this.animationSpeed = speed; return this; }
        public Builder backgroundColor(Color color) { this.backgroundColor = color; return this; }
        public Builder fillColor(Color color) { this.fillColor = color; this.useGradient = false; return this; }
        public Builder borderColor(Color color) { this.borderColor = color; return this; }
        public Builder gradient(Color start, Color end) { this.gradientStart = start; this.gradientEnd = end; this.useGradient = true; return this; }
        public Builder showPercentage(boolean show) { this.showPercentage = show; return this; }
        public Builder label(String label) { this.label = label; this.showLabel = !label.isEmpty(); return this; }
        public Builder textColor(Color color) { this.textColor = color; return this; }
        public Builder font(Font font) { this.font = font; return this; }
        public Builder onValueChanged(FuncIntOne<Float> callback) { this.onValueChanged = callback; return this; }
        public Builder onComplete(FuncInt callback) { this.onComplete = callback; return this; }
        public Builder onEmpty(FuncInt callback) { this.onEmpty = callback; return this; }

        public ProgressBar build() {
            ProgressBar pb = new ProgressBar(rect);
            pb.value = initialValue;
            pb.targetValue = initialValue;
            pb.animated = animated;
            pb.animationSpeed = animationSpeed;
            pb.backgroundColor = backgroundColor;
            pb.fillColor = fillColor;
            pb.borderColor = borderColor;
            pb.showPercentage = showPercentage;
            pb.showLabel = showLabel;
            pb.label = label;
            pb.textColor = textColor;
            pb.font = font;
            pb.useGradient = useGradient;
            pb.gradientStart = gradientStart;
            pb.gradientEnd = gradientEnd;
            pb.onValueChanged = onValueChanged;
            pb.onComplete = onComplete;
            pb.onEmpty = onEmpty;
            return pb;
        }
    }
    //</editor-fold>

    //<editor-fold desc="UPDATE">
    @Override
    public void init() {}

    @Override
    public void update(double deltaTime) {
        if (!active) return;

        if (animated && Math.abs(value - targetValue) > 0.001f) {
            float oldValue = value;

            float diff = targetValue - value;
            value += diff * animationSpeed * (float)deltaTime;

            value = Math.max(0f, Math.min(1f, value));

            if (Math.abs(value - targetValue) < 0.001f) {
                value = targetValue;
            }

            if (oldValue != value) {
                triggerValueChanged();
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="HELPER METHODS">
    private void triggerValueChanged() {
        if (onValueChanged != null) onValueChanged.call(value);
        if (value >= 1f && onComplete != null) onComplete.call();
        if (value <= 0f && onEmpty != null) onEmpty.call();
    }

    public void setValue(float newValue) {
        float oldValue = value;
        targetValue = Math.max(0f, Math.min(1f, newValue));

        if (!animated) {
            value = targetValue;
            if (oldValue != value) triggerValueChanged();
        }
    }

    public void setValueInstant(float newValue) {
        boolean wasAnimated = animated;
        animated = false;
        setValue(newValue);
        animated = wasAnimated;
    }

    public float getValue() { return value; }
    public int getPercentage() { return (int)(value * 100); }
    public void increment(float amount) { setValue(value + amount); }
    public void decrement(float amount) { setValue(value - amount); }
    public void setLabel(String label) { this.label = label; this.showLabel = !label.isEmpty(); }
    public boolean isFull() { return value >= 1f; }
    public boolean isEmpty() { return value <= 0f; }
    //</editor-fold>

    //<editor-fold desc="DRAW">
    @Override
    public void draw(Graphics2D g) {
        if (!active) return;

        int x = transform.position.xToInt();
        int y = transform.position.yToInt();
        int width = transform.scale.xToInt();
        int height = transform.scale.yToInt();

        g.setColor(backgroundColor);
        g.fillRect(x, y, width, height);

        int fillWidth = (int)(width * value);
        if (fillWidth > 0) {
            if (useGradient) {
                GradientPaint gradient = new GradientPaint(x, y, gradientStart, x + fillWidth, y, gradientEnd);
                g.setPaint(gradient);
                g.fillRect(x, y, fillWidth, height);
            } else {
                g.setColor(fillColor);
                g.fillRect(x, y, fillWidth, height);
            }
        }

        g.setColor(borderColor);
        g.drawRect(x, y, width, height);

        if (showPercentage || showLabel) {
            g.setFont(font);
            g.setColor(textColor);
            FontMetrics fm = g.getFontMetrics();

            String text = "";
            if (showLabel && !label.isEmpty()) text = label + " ";
            if (showPercentage) text += getPercentage() + "%";

            int textWidth = fm.stringWidth(text);
            int textX = x + (width - textWidth) / 2;
            int textY = y + (height + fm.getAscent()) / 2 - 2;

            g.drawString(text, textX, textY);
        }
    }

    @Override
    public void onCollision(GameObject collider) {}
    //</editor-fold>
}