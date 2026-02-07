package GameEngine.Tools.UIGenerator.Blueprints;

import GameEngine.Core.gameObject.GameObject;
import GameEngine.Core.gameObject.Obj.Slider;
import GameEngine.Core.input.Input;
import GameEngine.Core.util.Vector2;
import GameEngine.Tools.UIGenerator.Descriptors.ProgressBarDescriptor;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Blueprint for ProgressBar elements in the UI Generator.
 * Can be dragged, resized, and configured.
 */
public class ProgressBarBP extends GameObject implements UIBlueprint, Resizable {

    private ProgressBarDescriptor descriptor = new ProgressBarDescriptor();

    private Color borderColor = new Color(100, 100, 100);
    private Color selectedBorderColor = new Color(0, 150, 255);
    private Vector2 minSize = new Vector2(60, 10);

    private boolean selected = false;
    private ResizeHandle[] handles;
    private Rectangle canvasBounds;

    private ProgressBarBP() {}

    public static class Builder {
        private Vector2 pos = new Vector2(100, 100);
        private Vector2 size = new Vector2(200, 20);
        private Rectangle canvasBounds;
        private String varName = "progressBar";

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

        public ProgressBarBP build() {
            ProgressBarBP bp = new ProgressBarBP();
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
        g.setColor(descriptor.backgroundColor);
        g.fillRoundRect(x, y, w, h, 4, 4);

        // Draw fill (50% for preview)
        int fillWidth = (int)(w * descriptor.value);
        g.setColor(descriptor.fillColor);
        g.fillRoundRect(x, y, fillWidth, h, 4, 4);

        // Draw percentage text if enabled
        if (descriptor.showPercentage) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, Math.min(h - 4, 12)));
            FontMetrics fm = g.getFontMetrics();
            String text = (int)(descriptor.value * 100) + "%";
            int textX = x + (w - fm.stringWidth(text)) / 2;
            int textY = y + (h - fm.getHeight()) / 2 + fm.getAscent();
            g.drawString(text, textX, textY);
        }

        // Draw border
        g.setColor(selected ? selectedBorderColor : descriptor.borderColor);
        g.setStroke(new BasicStroke(selected ? 2 : 1));
        g.drawRoundRect(x, y, w, h, 4, 4);

        // Draw type label
        g.setColor(new Color(150, 150, 150));
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        g.drawString("ProgressBar - " + descriptor.varName, x, y - 5);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    }

    @Override
    public void onCollision(GameObject collider) {}

    // === UIBlueprint Interface ===

    @Override
    public String getTypeName() { return "ProgressBar"; }

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

    public ProgressBarDescriptor getDescriptor() { return descriptor; }

    public void setValue(float value) { descriptor.value = Math.max(0, Math.min(1, value)); }
    public float getValue() { return descriptor.value; }

    public void setBackgroundColor(Color c) { descriptor.backgroundColor = c; }
    public Color getBackgroundColor() { return descriptor.backgroundColor; }

    public void setFillColor(Color c) { descriptor.fillColor = c; }
    public Color getFillColor() { return descriptor.fillColor; }

    public void setBorderColor(Color c) { descriptor.borderColor = c; }
    public Color getBorderColorValue() { return descriptor.borderColor; }

    public void setShowPercentage(boolean show) { descriptor.showPercentage = show; }
    public boolean getShowPercentage() { return descriptor.showPercentage; }

    public void setAnimated(boolean animated) { descriptor.animated = animated; }
    public boolean getAnimated() { return descriptor.animated; }

    @Override
    public Vector2 getMinSize() { return minSize; }

    @Override
    public Rectangle getCanvasBounds() { return canvasBounds; }
}
