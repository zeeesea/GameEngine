package GameEngine.Tools.UIGenerator.Blueprints;

import GameEngine.Core.gameObject.GameObject;
import GameEngine.Core.input.Input;
import GameEngine.Core.util.Vector2;
import GameEngine.Tools.UIGenerator.Descriptors.UIRectDescriptor;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Blueprint for UIRect elements in the UI Generator.
 * Can be dragged, resized, and configured.
 */
public class UIRectBP extends GameObject implements UIBlueprint, Resizable {

    private UIRectDescriptor descriptor = new UIRectDescriptor();

    private Color borderColor = new Color(100, 100, 100);
    private Color selectedBorderColor = new Color(0, 150, 255);
    private Vector2 minSize = new Vector2(30, 30);

    private boolean selected = false;
    private ResizeHandle[] handles;
    private Rectangle canvasBounds;

    private UIRectBP() {}

    public static class Builder {
        private Vector2 pos = new Vector2(100, 100);
        private Vector2 size = new Vector2(150, 100);
        private Rectangle canvasBounds;
        private String varName = "rect";

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

        public UIRectBP build() {
            UIRectBP bp = new UIRectBP();
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
        renderOrder = 5; // Lower than other elements (buttons are 10)
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

        // Draw rect background
        if (descriptor.cornerRadius > 0) {
            RoundRectangle2D roundRect = new RoundRectangle2D.Float(
                x, y, w, h, descriptor.cornerRadius * 2, descriptor.cornerRadius * 2
            );

            if (descriptor.hasFill) {
                g.setColor(descriptor.fillColor);
                g.fill(roundRect);
            }

            // Draw border
            g.setColor(selected ? selectedBorderColor : (descriptor.hasBorder ? descriptor.borderColor : borderColor));
            g.setStroke(new BasicStroke(selected ? 2 : descriptor.borderWidth));
            g.draw(roundRect);
        } else {
            if (descriptor.hasFill) {
                g.setColor(descriptor.fillColor);
                g.fillRect(x, y, w, h);
            }

            g.setColor(selected ? selectedBorderColor : (descriptor.hasBorder ? descriptor.borderColor : borderColor));
            g.setStroke(new BasicStroke(selected ? 2 : descriptor.borderWidth));
            g.drawRect(x, y, w, h);
        }

        // Draw type label
        g.setColor(new Color(150, 150, 150));
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        g.drawString("UIRect - " + descriptor.varName, x, y - 5);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    }

    @Override
    public void onCollision(GameObject collider) {}

    // === UIBlueprint Interface ===

    @Override
    public String getTypeName() { return "UIRect"; }

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

    public UIRectDescriptor getDescriptor() { return descriptor; }

    public void setFillColor(Color c) { descriptor.fillColor = c; }
    public Color getFillColor() { return descriptor.fillColor; }

    public void setBorderColor(Color c) { descriptor.borderColor = c; }
    public Color getBorderColorValue() { return descriptor.borderColor; }

    public void setCornerRadius(int r) { descriptor.cornerRadius = r; }
    public int getCornerRadius() { return descriptor.cornerRadius; }

    public void setBorderWidth(int w) { descriptor.borderWidth = w; }
    public int getBorderWidth() { return descriptor.borderWidth; }

    public void setHasBorder(boolean has) { descriptor.hasBorder = has; }
    public boolean getHasBorder() { return descriptor.hasBorder; }

    public void setHasFill(boolean has) { descriptor.hasFill = has; }
    public boolean getHasFill() { return descriptor.hasFill; }

    @Override
    public Vector2 getMinSize() { return minSize; }

    @Override
    public Rectangle getCanvasBounds() { return canvasBounds; }
}
