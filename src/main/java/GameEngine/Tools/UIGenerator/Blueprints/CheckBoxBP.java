package GameEngine.Tools.UIGenerator.Blueprints;

import GameEngine.Core.gameObject.GameObject;
import GameEngine.Core.input.Input;
import GameEngine.Core.util.Vector2;
import GameEngine.Tools.UIGenerator.Descriptors.CheckBoxDescriptor;

import java.awt.*;

/**
 * Blueprint for CheckBox elements in the UI Generator.
 * Can be dragged and configured.
 */
public class CheckBoxBP extends GameObject implements UIBlueprint {

    private CheckBoxDescriptor descriptor = new CheckBoxDescriptor();

    private Color borderColor = new Color(100, 100, 100);
    private Color selectedBorderColor = new Color(0, 150, 255);

    private boolean selected = false;
    private Rectangle canvasBounds;

    private CheckBoxBP() {}

    public static class Builder {
        private Vector2 pos = new Vector2(100, 100);
        private Rectangle canvasBounds;
        private String varName = "checkBox";

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

        public CheckBoxBP build() {
            CheckBoxBP bp = new CheckBoxBP();
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
        int boxSize = descriptor.boxSize;

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw checkbox box
        g.setColor(descriptor.boxColor);
        g.fillRoundRect(x, y, boxSize, boxSize, 4, 4);

        // Draw checkmark (preview as checked)
        g.setColor(descriptor.checkedColor);
        g.setStroke(new BasicStroke(2));
        int pad = 5;
        g.drawLine(x + pad, y + boxSize/2, x + boxSize/2 - 2, y + boxSize - pad);
        g.drawLine(x + boxSize/2 - 2, y + boxSize - pad, x + boxSize - pad, y + pad);

        // Draw label
        g.setColor(descriptor.labelColor);
        Font font = new Font("Arial", Font.PLAIN, descriptor.fontSize);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(descriptor.label, x + boxSize + 10, y + boxSize/2 + fm.getAscent()/2 - 2);

        // Calculate total width for collision
        int labelWidth = fm.stringWidth(descriptor.label);
        int totalWidth = boxSize + 10 + labelWidth;
        transform.scale = new Vector2(totalWidth, boxSize);

        // Draw selection border
        if (selected) {
            g.setColor(selectedBorderColor);
            g.setStroke(new BasicStroke(2));
            g.drawRoundRect(x - 3, y - 3, totalWidth + 6, boxSize + 6, 4, 4);
        }

        // Draw type label
        g.setColor(new Color(150, 150, 150));
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        g.drawString("CheckBox - " + descriptor.varName, x, y - 5);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    }

    @Override
    public void onCollision(GameObject collider) {}

    // === UIBlueprint Interface ===

    @Override
    public String getTypeName() { return "CheckBox"; }

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

    public CheckBoxDescriptor getDescriptor() { return descriptor; }

    public void setLabel(String label) { descriptor.label = label; }
    public String getLabel() { return descriptor.label; }

    public void setBoxSize(int size) { descriptor.boxSize = size; }
    public int getBoxSize() { return descriptor.boxSize; }

    public void setBoxColor(Color c) { descriptor.boxColor = c; }
    public Color getBoxColor() { return descriptor.boxColor; }

    public void setCheckedColor(Color c) { descriptor.checkedColor = c; }
    public Color getCheckedColor() { return descriptor.checkedColor; }

    public void setLabelColor(Color c) { descriptor.labelColor = c; }
    public Color getLabelColor() { return descriptor.labelColor; }

    public void setFontSize(int size) { descriptor.fontSize = size; }
    public int getFontSize() { return descriptor.fontSize; }

    public void setChecked(boolean checked) { descriptor.checked = checked; }
    public boolean isChecked() { return descriptor.checked; }
}
