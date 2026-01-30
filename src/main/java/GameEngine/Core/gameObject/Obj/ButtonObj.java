package GameEngine.Core.gameObject.Obj;

import GameEngine.Core.gameObject.FuncInt.*;
import GameEngine.Core.gameObject.GameObject;
import GameEngine.Core.gameObject.Transform;
import GameEngine.Core.gameObject.collider.BoxCollider2D;
import GameEngine.Core.input.*;
import GameEngine.Core.util.MathUtils;
import GameEngine.Core.util.Vector2;
import org.w3c.dom.css.Rect;

import java.awt.*;

public class ButtonObj extends GameObject {
    private Color color;
    private TextObj text;

    private boolean lastHoverState;

    private FuncInt onClick;
    private FuncIntOne<ButtonObj> onClickOne;
    private FuncInt onHover;
    private FuncIntOne<ButtonObj> onHoverOne;
    private FuncIntTwo<ButtonObj, Boolean> onHoverTwo;

    private ButtonObj(Rectangle rect) {
        transform = new Transform(rect);
        collider = new BoxCollider2D(this);
    }

    /**
     * Konstruktor für verschachtelte Klassen (ColorButton, ToolButton)
     */
    protected ButtonObj(Builder builder) {
        transform = new Transform(builder.rect);
        collider = new BoxCollider2D(this);

        this.color = builder.color;
        this.tag = builder.tag;
        this.onClick = builder.onClick;
        this.onClickOne = builder.onClickOne;
        this.onHover = builder.onHover;
        this.onHoverOne = builder.onHoverOne;
        this.onHoverTwo = builder.onHoverTwo;

        this.text = new TextObj.Builder(builder.text)
                .position(this.getCenterPosition())
                .color(builder.textColor)
                .font(builder.font)
                .renderOrder(this.renderOrder + 1)
                .alignment(TextObj.TextAlignment.CENTER)
                .build();
    }

    public static class Builder {
        private Rectangle rect = new Rectangle(0,0, 100, 100);
        private Color color = Color.WHITE;
        private String text = "";
        private Font font = new Font("Arial", Font.BOLD, 30);
        private Color textColor = Color.BLACK;
        private String tag = "Button";

        private FuncInt onClick;
        private FuncIntOne<ButtonObj> onClickOne;
        private FuncInt onHover;
        private FuncIntOne<ButtonObj> onHoverOne;
        private FuncIntTwo<ButtonObj, Boolean> onHoverTwo;

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
        public Builder onClick(FuncInt onClick) {
            this.onClick = onClick;
            return this;
        }
        public Builder onClick(FuncIntOne<ButtonObj> onClickButton) {
            this.onClickOne = onClickButton;
            return this;
        }
        public Builder onHover(FuncInt onHover) {
            this.onHover = onHover;
            return this;
        }
        public Builder onHover(FuncIntOne<ButtonObj> onHoverOne) {
            this.onHoverOne = onHoverOne;
            return this;
        }
        public Builder onHover(FuncIntTwo<ButtonObj, Boolean> onHoverTwo) {
            this.onHoverTwo = onHoverTwo;
            return this;
        }
        public ButtonObj build() {
            ButtonObj b = new ButtonObj(rect);
            b.color = color;
            b.tag = tag;

            b.onClick = onClick;
            b.onClickOne = onClickOne;
            b.onHover = onHover;
            b.onHoverOne = onHoverOne;
            b.onHoverTwo = onHoverTwo;

            b.text = new TextObj.Builder(text)
                    .position(b.getCenterPosition())
                    .color(textColor)
                    .font(font)
                    .renderOrder(b.renderOrder + 1)
                    .alignment(TextObj.TextAlignment.CENTER)
                    .build();
            return b;
        }

    }

    @Override
    public void init() {

    }

    @Override
    public void update(double deltaTime)  {
        if (!active) return; // Nur updaten wenn aktiv

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

    private void hovering(boolean hovering) {
        if (onHover != null) onHover.call();
        if (onHoverOne != null) onHoverOne.call(this);
        if (onHoverTwo != null) onHoverTwo.call(this, hovering);
    }
    private void onClick() {
        if (onClick != null) onClick.call();
        if (onClickOne != null) onClickOne.call(this);
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
        // Text NICHT auf null setzen, nur aktivieren/deaktivieren
        if (text != null) {
            text.setActive(active);
        }
    }
    public void toggleActive() {
        setActive(!active);
    }

    public TextObj getTextObj() {
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
    public void setTextAlignment(TextObj.TextAlignment alignment) {
        if (this.text != null) {
            this.text.setAlignment(alignment);
        }
    }



    @Override
    public void draw(Graphics2D g) {
        if (!active) return; // Nur zeichnen wenn aktiv

        drawGOasFilledRect(color);

        if (text != null) {
            Vector2 pos = getCenterPosition();
            float yOffset = (float) text.getTextHeight(g)/4;
            pos.y = pos.y + yOffset;
            text.setPosition(pos);
            text.draw(g);
        }
    }

    @Override
    public void onCollision(GameObject collider) {}
}