package GameEngine.Core.gameObject.Obj;

import GameEngine.Core.gameObject.GameObject;
import GameEngine.Core.gameObject.Transform;
import GameEngine.Core.gameObject.collider.BoxCollider2D;
import GameEngine.Core.input.*;

import java.awt.*;
import java.util.function.Consumer;

public class ButtonObj extends GameObject {
    private final Runnable onClick;
    private Color color;
    private TextObj text;
    private String textString;
    private Font font;
    private boolean lastHoverState;
    private Consumer<Boolean> onHoverChange;


    public ButtonObj(Rectangle rect, Color color, Runnable onClick, String textString, Font font, Color textColor) {
        transform = new Transform(rect);
        this.onClick = onClick;
        this.color = color;
        this.font = font;
        this.textString = textString;

        this.text = new TextObj(
                textString,
                getCenterPosition(),
                textColor,
                font,
                renderOrder + 1
        );

        // Collider
        collider = new BoxCollider2D(this);
    }

    public ButtonObj(Rectangle rect, Runnable onClick, String textString) {
        this(
                rect,
                Color.white,
                onClick,
                textString,
                new Font("Arial", Font.BOLD, 30),
                Color.BLACK
        );
    }

    public ButtonObj(Rectangle rect, Color color, Runnable onClick, Consumer<Boolean> onHoverChange, String textString, Font font, Color textColor) {
        this(
                rect,
                color,
                onClick,
                textString,
                font,
                textColor
        );
        this.onHoverChange = onHoverChange;

    }

    @Override
    public void init() {

    }

    @Override
    public void update(double deltaTime) {
        if (!active) return; // Nur updaten wenn aktiv

        boolean hovering = collider.collidesWithPoint(Input.getMousePosition());
        if (onHoverChange != null && hovering != lastHoverState) {
            onHoverChange.accept(hovering);
            lastHoverState = hovering;
        }

        if (hovering && Input.getMouseButtonDown(Input.MouseCode.LEFT)) {
            onClick.run();
        }

        // Textposition immer in der Mitte halten
        if (text != null) {
            text.setPosition(getCenterPosition());
        }
    }

    public TextObj getText() {
        return text;
    }

    public void setText(String newText) {
        this.textString = newText;
        if (text != null) {
            text.setText(newText);
        }
    }

    public void setColor(Color newColor) {
        this.color = newColor;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public void draw(Graphics2D g) {
        if (!active) return; // Nur zeichnen wenn aktiv

        drawGOasFilledRect(color);

        if (text != null) {
            FontMetrics fm = g.getFontMetrics(font);
            int textWidth = fm.stringWidth(textString);
            int textHeight = fm.getHeight();

            text.transform.position.x -= (float) textWidth / 2;
            text.transform.position.y += (float) textHeight / 4;
            text.draw(g);
        }
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

    @Override
    public void onCollision(GameObject collider) {}
}