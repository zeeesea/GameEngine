package GameEngine.Core.gameObject.Obj;

import GameEngine.Core.gameObject.FuncInt.*;
import GameEngine.Core.gameObject.GameObject;
import GameEngine.Core.gameObject.Transform;
import GameEngine.Core.gameObject.collider.BoxCollider2D;
import GameEngine.Core.input.*;
import GameEngine.Core.util.Vector2;

import java.awt.*;

/**
 * A clickable button UI component with hover effects and callbacks.
 * Supports smooth hover animations, rounded corners, and multiple callback types.
 * Use the Builder pattern to create instances.
 */
public class Button extends GameObject {
    //<editor-fold desc="VARIABLES">
    private Color color;
    private Text text;

    private boolean lastHoverState;

    //Smooth Hover (SH)
    private boolean SH_Enabled = false;
    private Vector2 SH_baseSize; // Basis-Größe (unveränderlich)
    private float SH_sizeIncrease;
    private float SH_currentOffset = 0f; // Aktueller Offset
    private float SH_targetOffset = 0f; // Ziel-Offset
    private float SH_speed;
    private boolean SH_changingSize = false;

    //Round Corners
    private int cornerRadius = 0;

    //Border/Outline
    private boolean borderEnabled = false;
    private Color borderColor = Color.WHITE;
    private int borderThickness = 1;

    //Functional Interfaces
    private FuncInt onClick;
    private FuncIntOne<Button> onClickOne;
    private FuncInt onHover;
    private FuncIntOne<Button> onHoverOne;
    private FuncIntTwo<Button, Boolean> onHoverTwo;

    public enum ButtonPreset {
        BACK_BUTTON;
    }
    //</editor-fold>

    //<editor-fold desc="CONSTRUCTOR/BUILDER">
    private Button(Rectangle rect) {
        transform = new Transform(rect);
        collider = new BoxCollider2D(this);
    }

    protected Button(Builder builder) {
        transform = new Transform(builder.rect);
        collider = new BoxCollider2D(this);

        this.color = builder.color;
        this.tag = builder.tag;
        this.onClick = builder.onClick;
        this.onClickOne = builder.onClickOne;
        this.onHover = builder.onHover;
        this.onHoverOne = builder.onHoverOne;
        this.onHoverTwo = builder.onHoverTwo;
        this.cornerRadius = builder.cornerRadius;

        this.text = new Text.Builder(builder.text)
                .position(this.getCenterPosition())
                .color(builder.textColor)
                .font(builder.font)
                .renderOrder(this.renderOrder + 1)
                .alignment(Text.TextAlignment.CENTER)
                .build();
    }

    public static class Builder {
        private Rectangle rect = new Rectangle(0,0, 100, 100);
        private Color color = Color.WHITE;
        private String text = "";
        private Font font = new Font("Arial", Font.BOLD, 30);
        private Color textColor = Color.BLACK;
        private String tag = "Button";
        private int cornerRadius = 0;

        // Border/Outline
        private boolean borderEnabled = false;
        private Color borderColor = Color.WHITE;
        private int borderThickness = 1;

        boolean SH_Enabled = false;
        float SH_sizeIncrease;
        float SH_speed;

        private FuncInt onClick;
        private FuncIntOne<Button> onClickOne;
        private FuncInt onHover;
        private FuncIntOne<Button> onHoverOne;
        private FuncIntTwo<Button, Boolean> onHoverTwo;

        public Builder() {}

        public Builder rect(Rectangle rect) {
            this.rect = rect;
            return this;
        }

        public Builder pos(Vector2 pos) {
            rect.x = pos.xToInt();
            rect.y = pos.yToInt();
            return this;
        }

        public Builder size(Vector2 size) {
            rect.width = size.xToInt();
            rect.height = size.yToInt();
            return this;
        }

        public Builder color(Color color) {
            this.color = color;
            return this;
        }

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder tag(String tag) {
            this.tag = tag;
            return this;
        }

        public Builder font(Font font) {
            this.font = font;
            return this;
        }

        public Builder textColor(Color textColor) {
            this.textColor = textColor;
            return this;
        }

        public Builder cornerRadius(int radius) {
            this.cornerRadius = Math.max(0, radius);
            return this;
        }

        /**
         * Enables a border/outline with the specified color and thickness.
         * Works with smoothHover and cornerRadius.
         * @param color The border color
         * @param thickness The border thickness in pixels (minimum 1)
         */
        public Builder border(Color color, int thickness) {
            this.borderEnabled = true;
            this.borderColor = color != null ? color : Color.WHITE;
            this.borderThickness = Math.max(1, thickness);
            return this;
        }

        /**
         * Enables a border/outline with the specified color and default thickness (1).
         * @param color The border color
         */
        public Builder border(Color color) {
            return border(color, 1);
        }

        public Builder smoothHover(float SH_sizeIncrease, float SH_speed) {
            this.SH_sizeIncrease = SH_sizeIncrease;
            this.SH_speed = SH_speed;
            SH_Enabled = true;
            return this;
        }

        public Builder onClick(FuncInt onClick) {
            this.onClick = onClick;
            return this;
        }

        public Builder onClick(FuncIntOne<Button> onClickButton) {
            this.onClickOne = onClickButton;
            return this;
        }

        public Builder onHover(FuncInt onHover) {
            this.onHover = onHover;
            return this;
        }

        public Builder onHover(FuncIntOne<Button> onHoverOne) {
            this.onHoverOne = onHoverOne;
            return this;
        }

        public Builder onHover(FuncIntTwo<Button, Boolean> onHoverTwo) {
            this.onHoverTwo = onHoverTwo;
            return this;
        }

        public Builder preset(ButtonPreset preset) {
            switch (preset) {
                case BACK_BUTTON -> {
                    rect(new Rectangle(10, 10, 150, 40));
                    color(Color.WHITE);
                    text("Back to Menu");
                    font(new Font("Arial", Font.BOLD, 16));
                    textColor(Color.BLACK);
                    smoothHover(10, 150);
                    cornerRadius(8);
                    tag("BackButton");
                }
            }
            return this;
        }

        public Button build() {
            Button b = new Button(rect);
            b.color = color;
            b.tag = tag;
            b.cornerRadius = cornerRadius;

            // Border
            b.borderEnabled = borderEnabled;
            b.borderColor = borderColor;
            b.borderThickness = borderThickness;

            // Speichere die Basis-Größe für SmoothHover
            b.SH_baseSize = new Vector2(rect.width, rect.height);
            b.SH_sizeIncrease = SH_sizeIncrease;
            b.SH_Enabled = SH_Enabled;
            b.SH_speed = SH_speed;
            b.SH_currentOffset = 0f;
            b.SH_targetOffset = 0f;

            b.onClick = onClick;
            b.onClickOne = onClickOne;
            b.onHover = onHover;
            b.onHoverOne = onHoverOne;
            b.onHoverTwo = onHoverTwo;

            b.text = new Text.Builder(text)
                    .position(b.getCenterPosition())
                    .color(textColor)
                    .font(font)
                    .renderOrder(b.renderOrder + 1)
                    .alignment(Text.TextAlignment.CENTER)
                    .build();
            return b;
        }
    }
    //</editor-fold>

    //<editor-fold desc="CLICK/HOVER LOGIC">
    @Override
    public void init() {}

    @Override
    public void update(double deltaTime) {
        if (!active) return;
        updateState(deltaTime);
        updateHover(deltaTime);
    }

    private void updateState(double deltaTime) {
        // Prüfe ob ein Dropdown offen ist - dann keine Interaktion
        if (Dropdown.isAnyDropdownExpanded()) {
            if (lastHoverState) {
                hovering(false);
                lastHoverState = false;
            }
            return;
        }

        boolean hovering = collider.collidesWithPoint(Input.getMousePosition());
        if (hovering != lastHoverState) {
            hovering(hovering);
            lastHoverState = hovering;
        }

        if (hovering && Input.getMouseButtonDown(Input.MouseCode.LEFT)) {
            onClick();
        }

        if (text != null) {
            text.setPosition(getCenterPosition());
        }
    }

    private void updateHover(double deltaTime) {
        if (!SH_Enabled || !SH_changingSize) return;

        float step = SH_speed * (float)deltaTime;

        if (SH_currentOffset < SH_targetOffset) {
            SH_currentOffset = Math.min(SH_currentOffset + step, SH_targetOffset);
        } else if (SH_currentOffset > SH_targetOffset) {
            SH_currentOffset = Math.max(SH_currentOffset - step, SH_targetOffset);
        }

        if (Math.abs(SH_currentOffset - SH_targetOffset) < 0.1f) {
            SH_currentOffset = SH_targetOffset;
            SH_changingSize = false;
        }

        // Berechne die tatsächliche Größe basierend auf Basis + Offset
        Vector2 currentSize = SH_baseSize.add(SH_currentOffset);

        // Setze die Größe zentriert
        transform.setScaleCentered(currentSize);
    }

    private void hovering(boolean hovering) {
        if (SH_Enabled) changeHoverState(hovering);
        if (onHover != null) onHover.call();
        if (onHoverOne != null) onHoverOne.call(this);
        if (onHoverTwo != null) onHoverTwo.call(this, hovering);
    }

    private void onClick() {
        if (onClick != null) onClick.call();
        if (onClickOne != null) onClickOne.call(this);
    }
    //</editor-fold>

    //<editor-fold desc="SETTER/GETTER/HELPER">
    @Override
    public void setActive(boolean active) {
        this.active = active;
        if (text != null) {
            text.setActive(active);
        }
    }

    public void toggleActive() {
        setActive(!active);
    }

    private void changeHoverState(boolean b) {
        SH_changingSize = true;
        if (b) {
            SH_targetOffset = SH_sizeIncrease;
        } else {
            SH_targetOffset = 0f;
        }
    }

    public Text getTextObj() {
        return text;
    }

    public String getText() {
        if (text == null) return "";
        return text.getText();
    }

    public void setText(String text) {
        if (this.text != null) {
            this.text.setText(text);
        }
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setTextColor(Color textColor) {
        if (this.text != null) {
            text.setColor(textColor);
        }
    }

    public void setTextAlignment(Text.TextAlignment alignment) {
        if (this.text != null) {
            this.text.setAlignment(alignment);
        }
    }

    public boolean isSmoothHoveringEnabled() {
        return SH_Enabled;
    }

    public void setSmoothHoveringEnabled(boolean smoothHoveringEnabled) {
        this.SH_Enabled = smoothHoveringEnabled;
    }

    public int getCornerRadius() {
        return cornerRadius;
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = Math.max(0, radius);
    }

    /**
     * Enables or disables the border.
     * @param enabled true to show border
     */
    public void setBorderEnabled(boolean enabled) {
        this.borderEnabled = enabled;
    }

    /**
     * Sets the border color.
     * @param color The border color
     */
    public void setBorderColor(Color color) {
        if (color != null) {
            this.borderColor = color;
        }
    }

    /**
     * Sets the border thickness.
     * @param thickness The thickness in pixels (minimum 1)
     */
    public void setBorderThickness(int thickness) {
        this.borderThickness = Math.max(1, thickness);
    }

    /**
     * Enables border with the specified color and thickness.
     * @param color The border color
     * @param thickness The thickness in pixels
     */
    public void setBorder(Color color, int thickness) {
        this.borderEnabled = true;
        setBorderColor(color);
        setBorderThickness(thickness);
    }

    /**
     * Disables the border.
     */
    public void removeBorder() {
        this.borderEnabled = false;
    }

    public boolean isBorderEnabled() {
        return borderEnabled;
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public int getBorderThickness() {
        return borderThickness;
    }

    @Override
    public void draw(Graphics2D g) {
        if (!active) return;

        // Zeichne Button mit runden Ecken
        if (cornerRadius > 0) {
            drawGOasRoundedRect(color, cornerRadius);
        } else {
            drawGOasFilledRect(color);
        }

        // Zeichne Border/Outline wenn aktiviert
        if (borderEnabled) {
            drawBorder(g);
        }

        if (text != null) {
            Vector2 pos = getCenterPosition();
            float yOffset = (float) text.getTextHeight(g) / 4;
            pos.y = pos.y + yOffset;
            text.setPosition(pos);
            text.draw(g);
        }
    }

    private void drawBorder(Graphics2D g) {
        g.setColor(borderColor);
        Stroke oldStroke = g.getStroke();
        g.setStroke(new BasicStroke(borderThickness));

        int rx = (int) transform.position.x;
        int ry = (int) transform.position.y;
        int rw = (int) transform.scale.x;
        int rh = (int) transform.scale.y;

        if (cornerRadius > 0) {
            // Runde Ecken mit Anti-Aliasing
            Object oldHint = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g.drawRoundRect(rx, ry, rw, rh, cornerRadius * 2, cornerRadius * 2);

            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    oldHint != null ? oldHint : RenderingHints.VALUE_ANTIALIAS_DEFAULT);
        } else {
            g.drawRect(rx, ry, rw, rh);
        }

        g.setStroke(oldStroke);
    }

    @Override
    public void onCollision(GameObject collider) {}
    //</editor-fold>
}