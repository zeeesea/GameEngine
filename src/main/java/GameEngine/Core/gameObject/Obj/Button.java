package GameEngine.Core.gameObject.Obj;

import GameEngine.Core.gameObject.FuncInt.*;
import GameEngine.Core.gameObject.GameObject;
import GameEngine.Core.gameObject.Transform;
import GameEngine.Core.gameObject.collider.BoxCollider2D;
import GameEngine.Core.input.*;
import GameEngine.Core.util.Vector2;

import java.awt.*;

public class Button extends GameObject {
    //<editor-fold desc="VARIABLES">
    private Color color;
    private Text text;

    private boolean lastHoverState;

    //Smooth Hover (SH)
    boolean SH_Enabled = false;
    Vector2 SH_minSize;
    Vector2 SH_maxSize;
    Vector2 SH_size;
    Vector2 SH_targetSize;
    float SH_speed;
    boolean SH_changingSize = false;

    //Functional Interfaces
    private FuncInt onClick;
    private FuncIntOne<Button> onClickOne;
    private FuncInt onHover;
    private FuncIntOne<Button> onHoverOne;
    private FuncIntTwo<Button, Boolean> onHoverTwo;
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
        public Button build() {
            Button b = new Button(rect);
            b.color = color;
            b.tag = tag;

            b.SH_Enabled = SH_Enabled;
            Vector2 v = new Vector2(rect.width,  rect.height);
            b.SH_minSize = v.copy();
            b.SH_size = v.copy();
            b.SH_maxSize = v.add(SH_sizeIncrease);
            b.SH_speed = SH_speed;

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
    public void update(double deltaTime)  {
        if (!active) return;
        updateState(deltaTime);
        updateHover(deltaTime);
    }
    private void updateState(double deltaTime) {
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
        if (!SH_changingSize || !SH_Enabled) return;

        Vector2 step = new Vector2(SH_speed * (float)deltaTime);

        if (SH_size.x < SH_targetSize.x) {
            SH_size = SH_size.add(step);
        } else if (SH_size.x > SH_targetSize.x) {
            SH_size = SH_size.subtract(step);
        }

        SH_size = SH_size.clamp(SH_minSize.xToInt(), SH_maxSize.xToInt());

        if (SH_size.equals(SH_targetSize)) {
            SH_changingSize = false;
        }

        transform.setScaleCentered(SH_size);
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
        // Text NICHT auf null setzen, nur aktivieren/deaktivieren
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
            SH_targetSize = SH_maxSize.copy();
        } else {
            SH_targetSize = SH_minSize.copy();
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
    //</editor-fold>
}