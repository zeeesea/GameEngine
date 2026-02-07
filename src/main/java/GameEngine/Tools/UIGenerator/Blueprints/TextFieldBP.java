package GameEngine.Tools.UIGenerator.Blueprints;

import GameEngine.Core.gameObject.GameObject;
import GameEngine.Core.input.Input;
import GameEngine.Core.util.Vector2;
import GameEngine.Tools.UIGenerator.Descriptors.TextFieldDescriptor;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Blueprint for TextField elements in the UI Generator.
 * Can be dragged, resized, and configured.
 */
public class TextFieldBP extends GameObject implements UIBlueprint, Resizable {

    private TextFieldDescriptor descriptor = new TextFieldDescriptor();

    private Color borderColor = new Color(100, 100, 100);
    private Color selectedBorderColor = new Color(0, 150, 255);
    private Vector2 minSize = new Vector2(80, 25);

    private boolean selected = false;
    private ResizeHandle[] handles;
    private Rectangle canvasBounds;

    private TextFieldBP() {}

    public static class Builder {
        private Vector2 pos = new Vector2(100, 100);
        private Vector2 size = new Vector2(200, 40);
        private Rectangle canvasBounds;
        private String varName = "textField";

        public Builder pos(Vector2 pos) {
            this.pos = pos;
            return this;
        }

        public Builder size(Vector2 size) {
            this.size = size;
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

        public TextFieldBP build() {
            TextFieldBP bp = new TextFieldBP();
            bp.transform.position = pos.copy();
            bp.transform.scale = size.copy();
            bp.canvasBounds = canvasBounds;
            bp.descriptor.varName = varName;
            bp.descriptor.pos = pos.copy();
            bp.descriptor.size = size.copy();
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

        handles = new ResizeHandle[4];
        handles[0] = new ResizeHandle(ResizeHandle.Position.TOP_LEFT, this);
        handles[1] = new ResizeHandle(ResizeHandle.Position.TOP_RIGHT, this);
        handles[2] = new ResizeHandle(ResizeHandle.Position.BOTTOM_LEFT, this);
        handles[3] = new ResizeHandle(ResizeHandle.Position.BOTTOM_RIGHT, this);

        for (ResizeHandle h : handles) {
            objectManager.add(h);
        }
    }

    @Override
    public void update(double deltaTime) {
        boolean handleDragging = false;
        for (ResizeHandle h : handles) {
            if (h.isDragging()) {
                handleDragging = true;
                break;
            }
        }

        if (!handleDragging && selected) {
            draggable(Input.MouseCode.LEFT);
        }

        if (canvasBounds != null) {
            transform.position.x = Math.max(canvasBounds.x,
                Math.min(canvasBounds.x + canvasBounds.width - transform.scale.x, transform.position.x));
            transform.position.y = Math.max(canvasBounds.y,
                Math.min(canvasBounds.y + canvasBounds.height - transform.scale.y, transform.position.y));
        }

        // Enforce minimum size
        transform.scale.x = Math.max(minSize.x, transform.scale.x);
        transform.scale.y = Math.max(minSize.y, transform.scale.y);

        descriptor.pos = transform.position.copy();
        descriptor.size = transform.scale.copy();

        for (ResizeHandle h : handles) {
            h.setActive(selected);
        }
    }

    @Override
    public void draw(Graphics2D g) {
        int x = transform.position.xToInt();
        int y = transform.position.yToInt();
        int w = transform.scale.xToInt();
        int h = transform.scale.yToInt();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw background
        RoundRectangle2D roundRect = new RoundRectangle2D.Float(
            x, y, w, h, descriptor.cornerRadius * 2, descriptor.cornerRadius * 2
        );
        g.setColor(descriptor.backgroundColor);
        g.fill(roundRect);

        // Draw placeholder text
        g.setColor(new Color(128, 128, 128));
        Font font = new Font("Arial", Font.PLAIN, descriptor.fontSize);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();
        int textY = y + (h - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString(descriptor.placeholder, x + 10, textY);

        // Draw border
        g.setColor(selected ? selectedBorderColor : descriptor.borderColor);
        g.setStroke(new BasicStroke(selected ? 2 : 1));
        g.draw(roundRect);

        // Draw type label
        g.setColor(new Color(150, 150, 150));
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        g.drawString("TextField - " + descriptor.varName, x, y - 5);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    }

    @Override
    public void onCollision(GameObject collider) {}

    // === UIBlueprint Interface ===

    @Override
    public String getTypeName() { return "TextField"; }

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
        for (ResizeHandle h : handles) {
            h.destroy();
        }
        destroy();
    }

    @Override
    public void setTargetResolution(int width, int height) {
        descriptor.targetWidth = width;
        descriptor.targetHeight = height;
    }

    // === Getters/Setters ===

    public TextFieldDescriptor getDescriptor() { return descriptor; }

    public void setPlaceholder(String placeholder) { descriptor.placeholder = placeholder; }
    public String getPlaceholder() { return descriptor.placeholder; }

    public void setBackgroundColor(Color c) { descriptor.backgroundColor = c; }
    public Color getBackgroundColor() { return descriptor.backgroundColor; }

    public void setTextColor(Color c) { descriptor.textColor = c; }
    public Color getTextColor() { return descriptor.textColor; }

    public void setBorderColor(Color c) { descriptor.borderColor = c; }
    public Color getBorderColorValue() { return descriptor.borderColor; }

    public void setFocusedBorderColor(Color c) { descriptor.focusedBorderColor = c; }
    public Color getFocusedBorderColor() { return descriptor.focusedBorderColor; }

    public void setCornerRadius(int radius) { descriptor.cornerRadius = radius; }
    public int getCornerRadius() { return descriptor.cornerRadius; }

    public void setFontSize(int size) { descriptor.fontSize = size; }
    public int getFontSize() { return descriptor.fontSize; }

    @Override
    public Vector2 getMinSize() { return minSize; }

    @Override
    public Rectangle getCanvasBounds() { return canvasBounds; }
}
