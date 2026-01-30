package GameEngine.Core.gameObject.Obj;

import GameEngine.Core.gameObject.GameObject;
import GameEngine.Core.util.Vector2;

import java.awt.*;

public class TextObj extends GameObject {
    private String text;
    private Color color;
    private Font font;
    private TextAlignment alignment;

    // Text-Alignment
    public enum TextAlignment {
        LEFT, CENTER, RIGHT
    }

    public static class Builder {
        private String text;
        private Vector2 position = new Vector2(0, 0);
        private Color color = Color.WHITE;
        private Font font = new Font("Arial", Font.PLAIN, 16);
        private int renderOrder = 0;
        private TextAlignment alignment = TextAlignment.LEFT;

        public Builder(String text) {
            this.text = text;
        }

        public Builder position(Vector2 pos) {
            this.position = pos;
            return this;
        }

        public Builder color(Color color) {
            this.color = color;
            return this;
        }

        public Builder font(Font font) {
            this.font = font;
            return this;
        }

        public Builder renderOrder(int order) {
            this.renderOrder = order;
            return this;
        }

        public Builder alignment(TextAlignment alignment) {
            this.alignment = alignment;
            return this;
        }

        public TextObj build() {
            TextObj t = new TextObj(text, position, color, font, renderOrder);
            t.setAlignment(alignment);
            return t;
        }
    }

    private TextObj(String text, Vector2 pos, Color color, Font font, int renderOrder) {
        this.renderOrder = renderOrder;
        this.text = text;
        this.transform.position = pos;
        this.color = color;
        this.font = font;
    }

    @Override
    public void init() {

    }

    @Override
    public void update(double deltaTime) {
        // Kann für Animationen genutzt werden
    }

    // Setters
    public void setColor(Color color) {
        this.color = color;
    }
    public void setFont(Font font) {
        this.font = font;
    }
    public void setText(String text) {
        this.text = text;
    }
    public void setText(int text) {
        this.text = Integer.toString(text);
    }
    public void setText(float text) {
        this.text = String.format("%.2f", text); // 2 Dezimalstellen
    }
    public void setText(double text) {
        this.text = String.format("%.2f", text);
    }
    public void setPosition(Vector2 pos) {
        this.transform.position = pos;
    }
    public void setAlignment(TextAlignment alignment) {
        this.alignment = alignment;
    }

    // Getters
    public String getText() {
        return text;
    }
    public Color getColor() {
        return color;
    }
    public Font getFont() {
        return font;
    }

    /**
     * Gibt die Breite des Textes in Pixeln zurück
     */
    public int getTextWidth(Graphics2D g) {
        FontMetrics fm = g.getFontMetrics(font);
        return fm.stringWidth(text);
    }
    /**
     * Gibt die Höhe des Textes in Pixeln zurück
     */
    public int getTextHeight(Graphics2D g) {
        FontMetrics fm = g.getFontMetrics(font);
        return fm.getHeight();
    }
    /**
     * Zentriert den Text an einer Position
     */
    public void centerAt(Vector2 pos, Graphics2D g) {
        FontMetrics fm = g.getFontMetrics(font);
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();

        this.transform.position.x = pos.x - textWidth / 2f;
        this.transform.position.y = pos.y + textHeight / 4f;
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(color);
        g.setFont(font);

        float drawX = transform.position.x;
        float drawY = transform.position.y;

        // Alignment anwenden
        if (alignment == TextAlignment.CENTER) {
            FontMetrics fm = g.getFontMetrics(font);
            int textWidth = fm.stringWidth(text);
            drawX -= textWidth / 2f;
        } else if (alignment == TextAlignment.RIGHT) {
            FontMetrics fm = g.getFontMetrics(font);
            int textWidth = fm.stringWidth(text);
            drawX -= textWidth;
        }

        g.drawString(text, drawX, drawY);
    }

    @Override
    public void onCollision(GameObject collider) {
        // Text hat keine Kollision
    }
}