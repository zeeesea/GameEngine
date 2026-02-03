package GameEngine.Core.gameObject.Obj;

import GameEngine.Core.gameObject.GameObject;
import GameEngine.Core.gameObject.Transform;
import GameEngine.Core.util.Vector2;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * A simple rectangular UI element with optional rounded corners.
 * Can be used as a visual container, background, or separator.
 * Use the Builder pattern to create instances.
 */
public class UIRect extends GameObject {

    //<editor-fold desc="VARIABLES">
    private Color fillColor = new Color(50, 50, 55);
    private Color borderColor = new Color(80, 80, 85);
    private int cornerRadius = 0;
    private int borderWidth = 1;
    private boolean hasBorder = true;
    private boolean hasFill = true;
    //</editor-fold>

    //<editor-fold desc="CONSTRUCTOR/BUILDER">
    private UIRect(Rectangle rect) {
        transform = new Transform(rect);
        renderOrder = 0; // Default low render order (background)
    }

    /**
     * Builder pattern for UIRect.
     */
    public static class Builder {
        private Rectangle rect = new Rectangle(0, 0, 100, 100);
        private Color fillColor = new Color(50, 50, 55);
        private Color borderColor = new Color(80, 80, 85);
        private int cornerRadius = 0;
        private int borderWidth = 1;
        private boolean hasBorder = true;
        private boolean hasFill = true;
        private int renderOrder = 0;

        public Builder() {}

        /**
         * Sets the position and size using a Rectangle.
         *
         * @param rect The bounds of the UIRect
         * @return This builder for chaining
         */
        public Builder rect(Rectangle rect) {
            this.rect = rect;
            return this;
        }

        /**
         * Sets the position.
         *
         * @param x The x position
         * @param y The y position
         * @return This builder for chaining
         */
        public Builder position(int x, int y) {
            this.rect.x = x;
            this.rect.y = y;
            return this;
        }

        /**
         * Sets the size.
         *
         * @param width The width
         * @param height The height
         * @return This builder for chaining
         */
        public Builder size(int width, int height) {
            this.rect.width = width;
            this.rect.height = height;
            return this;
        }

        /**
         * Sets the fill color.
         *
         * @param color The fill color
         * @return This builder for chaining
         */
        public Builder fillColor(Color color) {
            this.fillColor = color;
            return this;
        }

        /**
         * Sets the border color.
         *
         * @param color The border color
         * @return This builder for chaining
         */
        public Builder borderColor(Color color) {
            this.borderColor = color;
            return this;
        }

        /**
         * Sets the corner radius for rounded corners.
         *
         * @param radius The corner radius in pixels
         * @return This builder for chaining
         */
        public Builder cornerRadius(int radius) {
            this.cornerRadius = Math.max(0, radius);
            return this;
        }

        /**
         * Sets the border width.
         *
         * @param width The border width in pixels
         * @return This builder for chaining
         */
        public Builder borderWidth(int width) {
            this.borderWidth = Math.max(0, width);
            return this;
        }

        /**
         * Sets whether to draw a border.
         *
         * @param hasBorder true to draw border, false otherwise
         * @return This builder for chaining
         */
        public Builder hasBorder(boolean hasBorder) {
            this.hasBorder = hasBorder;
            return this;
        }

        /**
         * Sets whether to fill the rectangle.
         *
         * @param hasFill true to fill, false for outline only
         * @return This builder for chaining
         */
        public Builder hasFill(boolean hasFill) {
            this.hasFill = hasFill;
            return this;
        }

        /**
         * Sets the render order (lower = drawn first/behind).
         *
         * @param order The render order
         * @return This builder for chaining
         */
        public Builder renderOrder(int order) {
            this.renderOrder = order;
            return this;
        }

        /**
         * Builds the UIRect instance.
         *
         * @return The configured UIRect
         */
        public UIRect build() {
            UIRect r = new UIRect(rect);
            r.fillColor = fillColor;
            r.borderColor = borderColor;
            r.cornerRadius = cornerRadius;
            r.borderWidth = borderWidth;
            r.hasBorder = hasBorder;
            r.hasFill = hasFill;
            r.renderOrder = renderOrder;
            return r;
        }
    }
    //</editor-fold>

    //<editor-fold desc="LIFECYCLE">
    @Override
    public void init() {}

    @Override
    public void update(double deltaTime) {}

    @Override
    public void draw(Graphics2D g) {
        int x = transform.position.xToInt();
        int y = transform.position.yToInt();
        int w = transform.scale.xToInt();
        int h = transform.scale.yToInt();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (cornerRadius > 0) {
            RoundRectangle2D roundRect = new RoundRectangle2D.Float(
                x, y, w, h, cornerRadius * 2, cornerRadius * 2
            );

            if (hasFill) {
                g.setColor(fillColor);
                g.fill(roundRect);
            }

            if (hasBorder && borderWidth > 0) {
                g.setColor(borderColor);
                g.setStroke(new BasicStroke(borderWidth));
                g.draw(roundRect);
            }
        } else {
            if (hasFill) {
                g.setColor(fillColor);
                g.fillRect(x, y, w, h);
            }

            if (hasBorder && borderWidth > 0) {
                g.setColor(borderColor);
                g.setStroke(new BasicStroke(borderWidth));
                g.drawRect(x, y, w, h);
            }
        }

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    }

    @Override
    public void onCollision(GameObject collider) {}
    //</editor-fold>

    //<editor-fold desc="SETTERS">
    /**
     * Sets the fill color.
     *
     * @param color The fill color
     * @return This UIRect for chaining
     */
    public UIRect setFillColor(Color color) {
        this.fillColor = color;
        return this;
    }

    /**
     * Sets the border color.
     *
     * @param color The border color
     * @return This UIRect for chaining
     */
    public UIRect setBorderColor(Color color) {
        this.borderColor = color;
        return this;
    }

    /**
     * Sets the corner radius.
     *
     * @param radius The corner radius
     * @return This UIRect for chaining
     */
    public UIRect setCornerRadius(int radius) {
        this.cornerRadius = Math.max(0, radius);
        return this;
    }

    /**
     * Sets the border width.
     *
     * @param width The border width
     * @return This UIRect for chaining
     */
    public UIRect setBorderWidth(int width) {
        this.borderWidth = Math.max(0, width);
        return this;
    }

    /**
     * Sets whether to draw border.
     *
     * @param hasBorder true to draw border
     * @return This UIRect for chaining
     */
    public UIRect setHasBorder(boolean hasBorder) {
        this.hasBorder = hasBorder;
        return this;
    }

    /**
     * Sets whether to fill.
     *
     * @param hasFill true to fill
     * @return This UIRect for chaining
     */
    public UIRect setHasFill(boolean hasFill) {
        this.hasFill = hasFill;
        return this;
    }

    /**
     * Sets position.
     *
     * @param x The x position
     * @param y The y position
     * @return This UIRect for chaining
     */
    public UIRect setPosition(int x, int y) {
        transform.position = new Vector2(x, y);
        return this;
    }

    /**
     * Sets size.
     *
     * @param width The width
     * @param height The height
     * @return This UIRect for chaining
     */
    public UIRect setSize(int width, int height) {
        transform.scale = new Vector2(width, height);
        return this;
    }
    //</editor-fold>

    //<editor-fold desc="GETTERS">
    /**
     * Gets the fill color.
     *
     * @return The fill color
     */
    public Color getFillColor() {
        return fillColor;
    }

    /**
     * Gets the border color.
     *
     * @return The border color
     */
    public Color getBorderColor() {
        return borderColor;
    }

    /**
     * Gets the corner radius.
     *
     * @return The corner radius
     */
    public int getCornerRadius() {
        return cornerRadius;
    }

    /**
     * Gets the border width.
     *
     * @return The border width
     */
    public int getBorderWidth() {
        return borderWidth;
    }

    /**
     * Checks if border is enabled.
     *
     * @return true if border is drawn
     */
    public boolean hasBorder() {
        return hasBorder;
    }

    /**
     * Checks if fill is enabled.
     *
     * @return true if filled
     */
    public boolean hasFill() {
        return hasFill;
    }
    //</editor-fold>
}
