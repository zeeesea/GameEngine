package GameEngine.Tools.UIGenerator.Blueprints;

import GameEngine.Core.gameObject.GameObject;
import GameEngine.Core.gameObject.Obj.Text;
import GameEngine.Core.input.Input;
import GameEngine.Core.util.Console.Console;
import GameEngine.Core.util.Console.ConsoleTag;
import GameEngine.Core.util.Vector2;
import GameEngine.Tools.UIGenerator.Descriptors.ButtonDescriptor;

import java.awt.*;

public class ButtonBP extends GameObject {
    private Color color = Color.GREEN;
    private Color cornerNormalColor = Color.YELLOW;
    private Color cornerSelectedColor = Color.RED;
    private int cornerSize = 10;
    private Vector2 size = new Vector2(200, 100);
    private Vector2 minSize = new Vector2(cornerSize + 5);
    private Corner[] corners;
    private String varName;
    private Text text;
    private String tag;


    private ButtonBP() {
    }
    public static class Builder {
        private Color color = Color.GREEN;
        private Color cornerNormalColor = Color.YELLOW;
        private Color cornerSelectedColor = Color.RED;
        private int cornerSize = 10;
        private Vector2 size = new Vector2(200, 100);
        private Vector2 minSize = new Vector2(cornerSize + 5);
        private String varName = "button";
        private String text = "Button";
        private int textSize = 30;
        private Vector2 pos;

        public Builder() {
        }

        public Builder varName(String varName) {
            this.varName = varName;
            return this;
        }

        public Builder color(Color color) {
            this.color = color;
            return this;
        }

        public Builder cornerNormalColor(Color cornerNormalColor) {
            this.cornerNormalColor = cornerNormalColor;
            return this;
        }

        public Builder cornerSelectedColor(Color cornerSelectedColor) {
            this.cornerSelectedColor = cornerSelectedColor;
            return this;
        }

        public Builder cornerSize(int cornerSize) {
            this.cornerSize = cornerSize;
            return this;
        }

        public Builder minSize(Vector2 minSize) {
            this.minSize = minSize;
            return this;
        }

        public Builder size(Vector2 size) {
            this.size = size;
            return this;
        }

        public Builder textSize(int size) {
            this.textSize = size;
            return this;
        }

        public Builder pos(Vector2 pos) {
            this.pos = pos;
            return this;
        }

        public ButtonBP build() {
            ButtonBP b = new ButtonBP();
            b.color = color;
            b.cornerNormalColor = cornerNormalColor;
            b.cornerSelectedColor = cornerSelectedColor;
            b.cornerSize = cornerSize;
            b.size = size;
            b.minSize = minSize;
            b.varName = varName;
            b.transform.position = pos;
            Text t = new Text.Builder(text)
                    .font(new Font("Arial", Font.BOLD, textSize))
                    .build();
            b.text = t;
            return b;
        }
    }

    @Override
    public void init() {
        renderOrder = 1;
        transform.scale = size;

        corners = new Corner[4];
        corners[0] = new Corner(Corner.Pos.LEFTTOP, this);
        corners[1] = new Corner(Corner.Pos.LEFTBOTTOM, this);
        corners[2] = new Corner(Corner.Pos.RIGHTTOP, this);
        corners[3] = new Corner(Corner.Pos.RIGHTBOTTOM, this);

        for (Corner c : corners) {
            objectManager.add(c);
        }
    }

    @Override
    public void update(double deltaTime) {
        boolean drag = false;
        for (Corner c : corners) {
            if (c.isDragging()) {
                drag = true;
                break;
            }
        }
        if (!drag) draggable(Input.MouseCode.LEFT);
    }

    public ButtonDescriptor toDescriptor(String varName) {
        ButtonDescriptor d = new ButtonDescriptor();
        d.pos = transform.position.copy();
        d.size = transform.scale.copy();
        d.color = color;
        d.text.setText("Button");
        d.varName = this.varName;
        return d;
    }

    @Override
    public void draw(Graphics2D g) {
        drawGOasFilledRect(color);
    }

    @Override
    public void onCollision(GameObject collider) {

    }

    private class Corner extends GameObject {

        private final Pos p;
        private final ButtonBP owner;

        public enum Pos {
            LEFTTOP,
            LEFTBOTTOM,
            RIGHTTOP,
            RIGHTBOTTOM;
        }

        public Corner(Pos pos, ButtonBP owner) {
            this.p = pos;
            this.owner = owner;
            transform.scale = new Vector2(cornerSize, cornerSize);
        }

        @Override
        public void init() {
            renderOrder = 2;
        }

        @Override
        public void update(double deltaTime) {
            draggable(Input.MouseCode.LEFT);

            if (owner == null) return;
            Vector2 cornerPos = getCornerPos();
            if (cornerPos != null) {
                transform.setPositionCentered(cornerPos);
            }

            if (isDragging()) {
                resizeButton();
            }

        }

        private void resizeButton() {
            Vector2 mousePos = Input.getMousePosition();
            Vector2 ownerPos = owner.transform.position;

            switch (p) {
                case LEFTTOP: {
                    float newWidth = (ownerPos.x + owner.transform.scale.x) - mousePos.x;
                    float newHeight = (ownerPos.y + owner.transform.scale.y) - mousePos.y;

                    if (newWidth > minSize.x) {
                        owner.transform.position.x = mousePos.x;
                        owner.transform.scale.x = newWidth;
                    }

                    if (newHeight > minSize.y) {
                        owner.transform.position.y = mousePos.y;
                        owner.transform.scale.y = newHeight;
                    }
                    break;
                }

                case RIGHTBOTTOM: {
                    Vector2 newSize = mousePos.subtract(ownerPos);

                    if (newSize.x > minSize.x) {
                        owner.transform.scale.x = newSize.x;
                    }

                    if (newSize.y > minSize.y) {
                        owner.transform.scale.y = newSize.y;
                    }
                    break;
                }

                case LEFTBOTTOM: {
                    float newWidth = (ownerPos.x + owner.transform.scale.x) - mousePos.x;
                    float newHeight = mousePos.y - ownerPos.y;

                    if (newWidth > minSize.x) {
                        owner.transform.position.x = mousePos.x;
                        owner.transform.scale.x = newWidth;
                    }

                    if (newHeight > minSize.y) {
                        owner.transform.scale.y = newHeight;
                    }
                    break;
                }

                case RIGHTTOP: {
                    float newWidth = mousePos.x - ownerPos.x;
                    float newHeight = (ownerPos.y + owner.transform.scale.y) - mousePos.y;

                    if (newWidth > minSize.x) {
                        owner.transform.scale.x = newWidth;
                    }

                    if (newHeight > minSize.y) {
                        owner.transform.position.y = mousePos.y;
                        owner.transform.scale.y = newHeight;
                    }
                    break;
                }
            }
        }
        public Vector2 getCornerPos() {

            Vector2 ownerPos = owner.transform.position;
            Vector2 ownerSize = owner.transform.scale;

            switch (p) {
                case LEFTTOP:
                    return new Vector2(ownerPos.x, ownerPos.y);

                case LEFTBOTTOM:
                    return new Vector2(ownerPos.x, ownerPos.y + ownerSize.y);

                case RIGHTTOP:
                    return new Vector2(ownerPos.x + ownerSize.x, ownerPos.y);

                case RIGHTBOTTOM:
                    return new Vector2(ownerPos.x + ownerSize.x, ownerPos.y + ownerSize.y);

                default:
                    Console.log(ConsoleTag.ERROR, "Unknown corner position");
                    return null;
            }
        }

        @Override
        public void draw(Graphics2D g) {
            drawGOasFilledRect(isDragging() ? cornerSelectedColor : cornerNormalColor);
        }

        @Override
        public void onCollision(GameObject collider) {

        }
    }

    public String getTag() {
        return tag;
    }
    public void setTag(String tag) {
        this.tag = tag;
    }
}