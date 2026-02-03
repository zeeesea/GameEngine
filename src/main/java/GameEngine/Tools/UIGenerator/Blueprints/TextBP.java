package GameEngine.Tools.UIGenerator.Blueprints;

import GameEngine.Core.gameObject.GameObject;
import GameEngine.Core.input.Input;
import GameEngine.Core.util.Vector2;
import GameEngine.Tools.UIGenerator.Descriptors.TextDescriptor;

import java.awt.*;

/**
 * Blueprint for Text elements in the UI Generator.
 * Can be dragged and configured.
 */
public class TextBP extends GameObject implements UIBlueprint {

    private TextDescriptor descriptor = new TextDescriptor();

    private Color borderColor = new Color(100, 100, 100);
    private Color selectedBorderColor = new Color(0, 150, 255);

    private boolean selected = false;
    private Rectangle canvasBounds;

    private TextBP() {}

    public static class Builder {
        private Vector2 pos = new Vector2(100, 100);
        private Rectangle canvasBounds;
        private String varName = "text";

        public Builder pos(Vector2 pos) {
            this.pos = pos;
            return this;
        }

        public Builder canvasBounds(Rectangle bounds) {
            this.canvasBounds = bounds;
            return this;
        }

        public Builder varName(String name) {
            this.varName = name;
            return this;
        }

        public TextBP build() {
            TextBP bp = new TextBP();
            bp.transform.position = pos.copy();
            bp.canvasBounds = canvasBounds;
            bp.descriptor.varName = varName;
            bp.descriptor.pos = pos.copy();
            if (canvasBounds != null) {
                bp.descriptor.canvasOffset = new Vector2(canvasBounds.x, canvasBounds.y);
                bp.descriptor.canvasSize = new Vector2(canvasBounds.width, canvasBounds.height);
            }
            return bp;
        }
    }

    @Override
    public void init() {
        renderOrder = 10;
    }

    @Override
    public void update(double deltaTime) {
        if (selected) {
            draggable(Input.MouseCode.LEFT);
        }

        if (canvasBounds != null) {
            transform.position.x = Math.max(canvasBounds.x,
                Math.min(canvasBounds.x + canvasBounds.width - 50, transform.position.x));
            transform.position.y = Math.max(canvasBounds.y,
                Math.min(canvasBounds.y + canvasBounds.height - 20, transform.position.y));
        }

        descriptor.pos = transform.position.copy();
    }

    @Override
    public void draw(Graphics2D g) {
        int x = transform.position.xToInt();
        int y = transform.position.yToInt();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw text
        Font font = new Font(descriptor.fontName, descriptor.fontStyle, descriptor.fontSize);
        g.setFont(font);
        g.setColor(descriptor.color);
        g.drawString(descriptor.text, x, y + descriptor.fontSize);

        // Calculate bounds for selection
        FontMetrics fm = g.getFontMetrics(font);
        int textWidth = fm.stringWidth(descriptor.text);
        int textHeight = fm.getHeight();
        transform.scale = new Vector2(textWidth, textHeight);

        // Draw selection border
        if (selected) {
            g.setColor(selectedBorderColor);
            g.setStroke(new BasicStroke(2));
            g.drawRect(x - 2, y - 2, textWidth + 4, textHeight + 4);
        }

        // Draw type label
        g.setColor(new Color(150, 150, 150));
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        g.drawString("Text - " + descriptor.varName, x, y - 5);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    }

    @Override
    public void onCollision(GameObject collider) {}

    // === UIBlueprint Interface ===

    @Override
    public String getTypeName() { return "Text"; }

    @Override
    public String getVarName() { return descriptor.varName; }

    @Override
    public void setVarName(String name) { descriptor.varName = name; }

    @Override
    public String toBuilderCode() { return descriptor.toBuilderCode(); }

    @Override
    public boolean isSelected() { return selected; }

    @Override
    public void setSelected(boolean selected) { this.selected = selected; }

    @Override
    public void destroyBlueprint() {
        destroy();
    }

    @Override
    public void setTargetResolution(int width, int height) {
        descriptor.targetWidth = width;
        descriptor.targetHeight = height;
    }

    // === Getters/Setters ===

    public TextDescriptor getDescriptor() { return descriptor; }

    public void setText(String text) { descriptor.text = text; }
    public String getText() { return descriptor.text; }

    public void setColor(Color c) { descriptor.color = c; }
    public Color getColor() { return descriptor.color; }

    public void setFontSize(int size) { descriptor.fontSize = size; }
    public int getFontSize() { return descriptor.fontSize; }

    public void setFontStyle(int style) { descriptor.fontStyle = style; }
    public int getFontStyle() { return descriptor.fontStyle; }

    public void setFontName(String name) { descriptor.fontName = name; }
    public String getFontName() { return descriptor.fontName; }

    public void setAlignment(String alignment) { descriptor.alignment = alignment; }
    public String getAlignment() { return descriptor.alignment; }
}
