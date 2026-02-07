package GameEngine.Tools.UIGenerator.Descriptors;

import GameEngine.Core.util.Vector2;

import java.awt.*;

/**
 * Descriptor containing all data needed to generate TextField builder code.
 */
public class TextFieldDescriptor {
    public String varName = "textField";
    public Vector2 pos = new Vector2(0, 0);
    public Vector2 size = new Vector2(200, 40);
    public String placeholder = "Enter text...";
    public Color backgroundColor = new Color(40, 40, 40);
    public Color textColor = Color.WHITE;
    public Color borderColor = Color.GRAY;
    public Color focusedBorderColor = Color.CYAN;
    public int cornerRadius = 0;
    public int fontSize = 16;

    // For relative position calculation
    public Vector2 canvasOffset = new Vector2(0, 0);
    public Vector2 canvasSize = new Vector2(1, 1);
    public int targetWidth = 1536;
    public int targetHeight = 864;

    /**
     * Generates the Builder code for this TextField with relative positioning.
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
        sb.append(varName).append(" = new TextField.Builder()\n");
        sb.append("        .pos(new Vector2(").append(targetX).append(", ").append(targetY).append("))\n");
        sb.append("        .size(new Vector2(").append(targetW).append(", ").append(targetH).append("))\n");
        sb.append("        .placeholder(\"").append(placeholder).append("\")\n");
        sb.append("        .backgroundColor(new Color(").append(backgroundColor.getRed()).append(", ")
          .append(backgroundColor.getGreen()).append(", ").append(backgroundColor.getBlue()).append("))\n");
        sb.append("        .textColor(new Color(").append(textColor.getRed()).append(", ")
          .append(textColor.getGreen()).append(", ").append(textColor.getBlue()).append("))\n");
        sb.append("        .borderColor(new Color(").append(borderColor.getRed()).append(", ")
          .append(borderColor.getGreen()).append(", ").append(borderColor.getBlue()).append("))\n");
        sb.append("        .focusedBorderColor(new Color(").append(focusedBorderColor.getRed()).append(", ")
          .append(focusedBorderColor.getGreen()).append(", ").append(focusedBorderColor.getBlue()).append("))\n");
        sb.append("        .font(new Font(\"Arial\", Font.PLAIN, ").append(fontSize).append("))\n");
        if (cornerRadius > 0) {
            sb.append("        .cornerRadius(").append(cornerRadius).append(")\n");
        }
        sb.append("        .build();");
        return sb.toString();
    }
}
