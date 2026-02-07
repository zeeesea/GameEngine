package GameEngine.Tools.UIGenerator.Blueprints;

import GameEngine.Core.gameObject.GameObject;
import GameEngine.Core.gameObject.Obj.Text;
import GameEngine.Core.input.Input;
import GameEngine.Core.util.Vector2;
import GameEngine.Tools.UIGenerator.Descriptors.ButtonDescriptor;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Blueprint for Button elements in the UI Generator.
 * Can be dragged, resized, and configured.
 */
public class ButtonBP extends GameObject implements UIBlueprint, Resizable {

    // Descriptor holds all the data
    private ButtonDescriptor descriptor = new ButtonDescriptor();

    // Visual settings for the editor
    private Color borderColor = new Color(100, 100, 100);
    private Color selectedBorderColor = new Color(0, 150, 255);
    private Vector2 minSize = new Vector2(50, 30);

    // State
    private boolean selected = false;
    private ResizeHandle[] handles;

    // Canvas bounds (to constrain movement)
    private Rectangle canvasBounds;

    private ButtonBP() {}

    public static class Builder {
        private Vector2 pos = new Vector2(100, 100);
        private Vector2 size = new Vector2(150, 50);
        private Rectangle canvasBounds;
        private String varName = "button";

        public Builder() {}

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

        public ButtonBP build() {
            ButtonBP bp = new ButtonBP();
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

        // Create resize handles
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
        // Check if any handle is being dragged
        boolean handleDragging = false;
        for (ResizeHandle h : handles) {
            if (h.isDragging()) {
                handleDragging = true;
                break;
            }
        }

        // Only allow dragging if no handle is active
        if (!handleDragging && selected) {
            draggable(Input.MouseCode.LEFT);
        }

        // Constrain to canvas
        if (canvasBounds != null) {
            transform.position.x = Math.max(canvasBounds.x,
                Math.min(canvasBounds.x + canvasBounds.width - transform.scale.x, transform.position.x));
            transform.position.y = Math.max(canvasBounds.y,
                Math.min(canvasBounds.y + canvasBounds.height - transform.scale.y, transform.position.y));
        }

        // Update descriptor
        descriptor.pos = transform.position.copy();
        descriptor.size = transform.scale.copy();

        // Update handle visibility
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

        // Enable antialiasing
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw button background
        RoundRectangle2D roundRect = new RoundRectangle2D.Float(
            x, y, w, h, descriptor.cornerRadius * 2, descriptor.cornerRadius * 2
        );
        g.setColor(descriptor.color);
        g.fill(roundRect);

        // Draw border
        g.setColor(selected ? selectedBorderColor : borderColor);
        g.setStroke(new BasicStroke(selected ? 2 : 1));
        g.draw(roundRect);

        // Draw text
        g.setColor(descriptor.textColor);
        g.setFont(new Font(descriptor.fontName, Font.BOLD, descriptor.fontSize));
        FontMetrics fm = g.getFontMetrics();
        int textX = x + (w - fm.stringWidth(descriptor.text)) / 2;
        int textY = y + (h + fm.getAscent() - fm.getDescent()) / 2;
        g.drawString(descriptor.text, textX, textY);

        // Draw type label above element
        g.setColor(new Color(150, 150, 150));
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        g.drawString("Button - " + descriptor.varName, x, y - 5);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    }

    @Override
    public void onCollision(GameObject collider) {}

    // === UIBlueprint Interface ===

    @Override
    public String getTypeName() { return "Button"; }

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

    // === Getters/Setters for Descriptor ===

    public ButtonDescriptor getDescriptor() { return descriptor; }

    public void setColor(Color c) { descriptor.color = c; }
    public Color getColor() { return descriptor.color; }

    public void setTextColor(Color c) { descriptor.textColor = c; }
    public Color getTextColor() { return descriptor.textColor; }

    public void setText(String text) { descriptor.text = text; }
    public String getText() { return descriptor.text; }

    public void setTag(String tag) { descriptor.tag = tag; }
    public String getTag() { return descriptor.tag; }

    public void setFontSize(int size) { descriptor.fontSize = size; }
    public int getFontSize() { return descriptor.fontSize; }

    public void setCornerRadius(int radius) { descriptor.cornerRadius = radius; }
    public int getCornerRadius() { return descriptor.cornerRadius; }

    @Override
    public Vector2 getMinSize() { return minSize; }

    @Override
    public Rectangle getCanvasBounds() { return canvasBounds; }
}