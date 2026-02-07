package GameEngine.Tools.UIGenerator.Blueprints;

import GameEngine.Core.gameObject.GameObject;
import GameEngine.Core.input.Input;
import GameEngine.Core.util.Vector2;
import GameEngine.Tools.UIGenerator.Descriptors.RadioButtonDescriptor;

import java.awt.*;

/**
 * Blueprint for RadioButton elements in the UI Generator.
 * Can be dragged and configured.
 */
public class RadioButtonBP extends GameObject implements UIBlueprint {

    private RadioButtonDescriptor descriptor = new RadioButtonDescriptor();

    private Color borderColor = new Color(100, 100, 100);
    private Color selectedBorderColor = new Color(0, 150, 255);

    private boolean selected = false;
    private Rectangle canvasBounds;

    private RadioButtonBP() {}

    public static class Builder {
        private Vector2 pos = new Vector2(100, 100);
        private Rectangle canvasBounds;
        private String varName = "radioButton";

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

        public RadioButtonBP build() {
            RadioButtonBP bp = new RadioButtonBP();
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
                Math.min(canvasBounds.x + canvasBounds.width - 100, transform.position.x));
            transform.position.y = Math.max(canvasBounds.y,
                Math.min(canvasBounds.y + canvasBounds.height - 30, transform.position.y));
        }

        descriptor.pos = transform.position.copy();
    }

    @Override
    public void draw(Graphics2D g) {
        int x = transform.position.xToInt();
        int y = transform.position.yToInt();
        int circleSize = descriptor.circleSize;

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw outer circle
        g.setColor(descriptor.circleColor);
        g.fillOval(x, y, circleSize, circleSize);

        // Draw border
        g.setColor(borderColor);
        g.setStroke(new BasicStroke(1));
        g.drawOval(x, y, circleSize, circleSize);

        // Draw inner circle (selected preview)
        int innerSize = circleSize / 2;
        int innerOffset = (circleSize - innerSize) / 2;
        g.setColor(descriptor.selectedColor);
        g.fillOval(x + innerOffset, y + innerOffset, innerSize, innerSize);

        // Draw label
        g.setColor(descriptor.labelColor);
        Font font = new Font("Arial", Font.PLAIN, descriptor.fontSize);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(descriptor.label, x + circleSize + 10, y + circleSize / 2 + fm.getAscent() / 2 - 2);

        // Calculate total width for collision
        int labelWidth = fm.stringWidth(descriptor.label);
        int totalWidth = circleSize + 10 + labelWidth;
        transform.scale = new Vector2(totalWidth, circleSize);

        // Draw selection border
        if (selected) {
            g.setColor(selectedBorderColor);
            g.setStroke(new BasicStroke(2));
            g.drawRoundRect(x - 3, y - 3, totalWidth + 6, circleSize + 6, 4, 4);
        }

        // Draw type label
        g.setColor(new Color(150, 150, 150));
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        g.drawString("RadioButton - " + descriptor.varName, x, y - 5);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    }

    @Override
    public void onCollision(GameObject collider) {}

    // === UIBlueprint Interface ===

    @Override
    public String getTypeName() { return "RadioButton"; }

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

    public RadioButtonDescriptor getDescriptor() { return descriptor; }

    public void setLabel(String label) { descriptor.label = label; }
    public String getLabel() { return descriptor.label; }

    public void setGroupName(String group) { descriptor.groupName = group; }
    public String getGroupName() { return descriptor.groupName; }

    public void setCircleSize(int size) { descriptor.circleSize = size; }
    public int getCircleSize() { return descriptor.circleSize; }

    public void setCircleColor(Color c) { descriptor.circleColor = c; }
    public Color getCircleColor() { return descriptor.circleColor; }

    public void setSelectedColor(Color c) { descriptor.selectedColor = c; }
    public Color getSelectedColor() { return descriptor.selectedColor; }

    public void setLabelColor(Color c) { descriptor.labelColor = c; }
    public Color getLabelColor() { return descriptor.labelColor; }

    public void setFontSize(int size) { descriptor.fontSize = size; }
    public int getFontSize() { return descriptor.fontSize; }
}
