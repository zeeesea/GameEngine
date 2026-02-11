package GameEngine.Core.gameObject.Obj;

import GameEngine.Core.gameObject.GameObject;
import GameEngine.Core.util.Vector2;

import java.awt.*;

/**
 * A text rendering UI component for displaying strings on screen.
 * Supports different fonts, colors, and text alignment options.
 * Also supports styled text with multiple colors using StyledString.
 * Use the Builder pattern to create instances.
 *
 * Examples:
 * <pre>
 * // Simple text
 * Text text = new Text.Builder("Hello World").color(Color.WHITE).build();
 *
 * // Styled text with multiple colors
 * StyledString styled = new StyledString.Builder()
 *     .append("Ha", Color.GREEN)
 *     .append("ll", Color.RED)
 *     .append("o", Color.GREEN)
 *     .build();
 * Text text = new Text.Builder(styled).build();
 *
 * // Using ANSI codes
 * Text text = new Text.Builder("\u001b[92mHello \u001b[91mWorld").build();
 * </pre>
 */
public class Text extends GameObject {
    //<editor-fold desc="VARIABLES">
    private String text;
    private StyledString styledText;
    private boolean useStyledText = false;
    private Color color;
    private Font font;
    private TextAlignment alignment;

    /**
     * Text alignment options for positioning text relative to its anchor point.
     */
    public enum TextAlignment {
        LEFT, CENTER, RIGHT
    }
    //</editor-fold>

    //<editor-fold desc="CONSTRUCTOR/BUILDER">
    public static class Builder {
        private String text;
        private StyledString styledText;
        private boolean useStyledText = false;
        private boolean parseAnsi = false;
        private Vector2 position = new Vector2(0, 0);
        private Color color = Color.WHITE;
        private Font font = new Font("Arial", Font.PLAIN, 16);
        private int renderOrder = 0;
        private TextAlignment alignment = TextAlignment.LEFT;

        /**
         * Creates a builder with a plain text string.
         */
        public Builder(String text) {
            this.text = text;
        }

        /**
         * Creates a builder with a styled string.
         */
        public Builder(StyledString styledText) {
            this.styledText = styledText;
            this.useStyledText = true;
        }

        /**
         * If enabled, parses ANSI escape codes in the text string.
         * Example: "\u001b[91mRed\u001b[92mGreen"
         */
        public Builder parseAnsi(boolean parse) {
            this.parseAnsi = parse;
            return this;
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

        public Text build() {
            Text t;
            if (useStyledText) {
                t = new Text(styledText, position, font, renderOrder);
            } else if (parseAnsi && text != null && text.contains("\u001b[")) {
                // Auto-detect and parse ANSI codes
                t = new Text(StyledString.fromAnsi(text, color), position, font, renderOrder);
            } else {
                t = new Text(text, position, color, font, renderOrder);
            }
            t.setAlignment(alignment);
            return t;
        }
    }

    private Text(String text, Vector2 pos, Color color, Font font, int renderOrder) {
        this.renderOrder = renderOrder;
        this.text = text;
        this.transform.position = pos;
        this.color = color;
        this.font = font;
        this.useStyledText = false;
    }

    private Text(StyledString styledText, Vector2 pos, Font font, int renderOrder) {
        this.renderOrder = renderOrder;
        this.styledText = styledText;
        this.text = styledText.getPlainText();
        this.transform.position = pos;
        this.font = font;
        this.useStyledText = true;
        this.color = Color.WHITE; // Default, not used for styled text
    }
    //</editor-fold>

    @Override
    public void init() {

    }
    @Override
    public void update(double deltaTime) {
    }

    //<editor-fold desc="SETTERS">
    public void setColor(Color color) {
        this.color = color;
    }
    public void setFont(Font font) {
        this.font = font;
    }
    public void setText(String text) {
        this.text = text;
        this.useStyledText = false;
    }
    public void setText(StyledString styledText) {
        this.styledText = styledText;
        this.text = styledText.getPlainText();
        this.useStyledText = true;
    }
    public void setText(int text) {
        this.text = Integer.toString(text);
        this.useStyledText = false;
    }
    public void setText(float text) {
        this.text = String.format("%.2f", text);
        this.useStyledText = false;
    }
    public void setText(double text) {
        this.text = String.format("%.2f", text);
        this.useStyledText = false;
    }
    public void setPosition(Vector2 pos) {
        this.transform.position = pos;
    }
    public void setAlignment(TextAlignment alignment) {
        this.alignment = alignment;
    }
    //</editor-fold>

    //<editor-fold desc="GETTERS">
    public String getText() {
        return text;
    }
    public Color getColor() {
        return color;
    }
    public Font getFont() {
        return font;
    }
    //</editor-fold>

    //<editor-fold desc="HELPER METHODS">
    /**
     * Returns width of text in pixels
     */
    public int getTextWidth(Graphics2D g) {
        FontMetrics fm = g.getFontMetrics(font);
        return fm.stringWidth(text);
    }
    /**
     * Returns height of text in pixels
     */
    public int getTextHeight(Graphics2D g) {
        FontMetrics fm = g.getFontMetrics(font);
        return fm.getHeight();
    }
    /**
     * Centers the text on a specific position
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
        if (text == null || text.isEmpty()) return;
        g.setFont(font);

        float drawX = transform.position.x;
        float drawY = transform.position.y;

        // Alignment
        if (alignment == TextAlignment.CENTER) {
            FontMetrics fm = g.getFontMetrics(font);
            int textWidth = fm.stringWidth(text);
            drawX -= textWidth / 2f;
        } else if (alignment == TextAlignment.RIGHT) {
            FontMetrics fm = g.getFontMetrics(font);
            int textWidth = fm.stringWidth(text);
            drawX -= textWidth;
        }

        if (useStyledText && styledText != null) {
            // Draw styled text with multiple colors
            FontMetrics fm = g.getFontMetrics(font);
            float currentX = drawX;

            for (StyledString.StyledSegment segment : styledText.getSegments()) {
                g.setColor(segment.color);
                g.drawString(segment.text, currentX, drawY);
                currentX += fm.stringWidth(segment.text);
            }
        } else {
            // Draw simple text
            g.setColor(color);
            g.drawString(text, drawX, drawY);
        }
    }

    @Override
    public void onCollision(GameObject collider) {
    }
    //</editor-fold>
}