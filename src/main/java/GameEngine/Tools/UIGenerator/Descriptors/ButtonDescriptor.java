package GameEngine.Tools.UIGenerator.Descriptors;

import GameEngine.Core.util.Vector2;

import java.awt.*;

/**
 * Descriptor containing all data needed to generate Button builder code.
 */
public class ButtonDescriptor {
    public String varName = "button";
    public String tag = "Button";
    public Vector2 pos = new Vector2(0, 0);
    public Vector2 size = new Vector2(150, 50);
    public Color color = Color.WHITE;
    public Color textColor = Color.BLACK;
    public String text = "Button";
    public String fontName = "Arial";
    public int fontSize = 20;
    public int cornerRadius = 8;
    public boolean smoothHover = true;
    public float smoothHoverSize = 10f;
    public float smoothHoverSpeed = 150f;

    // For relative position calculation
    public Vector2 canvasOffset = new Vector2(0, 0);
    public Vector2 canvasSize = new Vector2(1, 1);
    public int targetWidth = 1536;
    public int targetHeight = 864;

    /**
     * Generates the Builder code for this button with relative positioning.
     */
    public String toBuilderCode() {
        // Calculate relative position within canvas
        float relX = (pos.x - canvasOffset.x) / canvasSize.x;
        float relY = (pos.y - canvasOffset.y) / canvasSize.y;
        int targetX = (int)(relX * targetWidth);
        int targetY = (int)(relY * targetHeight);

        // Scale size proportionally
        float scaleX = targetWidth / canvasSize.x;
        float scaleY = targetHeight / canvasSize.y;
        int targetW = (int)(size.x * scaleX);
        int targetH = (int)(size.y * scaleY);

        StringBuilder sb = new StringBuilder();
        sb.append(varName).append(" = new Button.Builder()\n");
        sb.append("        .rect(new Rectangle(").append(targetX).append(", ").append(targetY)
          .append(", ").append(targetW).append(", ").append(targetH).append("))\n");
        sb.append("        .color(new Color(").append(color.getRed()).append(", ")
          .append(color.getGreen()).append(", ").append(color.getBlue()).append("))\n");
        sb.append("        .text(\"").append(text).append("\")\n");
        sb.append("        .textColor(new Color(").append(textColor.getRed()).append(", ")
          .append(textColor.getGreen()).append(", ").append(textColor.getBlue()).append("))\n");
        sb.append("        .font(new Font(\"").append(fontName).append("\", Font.BOLD, ").append(fontSize).append("))\n");
        sb.append("        .tag(\"").append(tag).append("\")\n");
        if (cornerRadius > 0) {
            sb.append("        .cornerRadius(").append(cornerRadius).append(")\n");
        }
        if (smoothHover) {
            sb.append("        .smoothHover(").append(smoothHoverSize).append("f, ").append(smoothHoverSpeed).append("f)\n");
        }
        sb.append("        .build();");
        return sb.toString();
    }
}
