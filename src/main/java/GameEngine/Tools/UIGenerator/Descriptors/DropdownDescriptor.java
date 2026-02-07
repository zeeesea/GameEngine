package GameEngine.Tools.UIGenerator.Descriptors;

import GameEngine.Core.util.Vector2;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Descriptor containing all data needed to generate Dropdown builder code.
 */
public class DropdownDescriptor {
    public String varName = "dropdown";
    public Vector2 pos = new Vector2(0, 0);
    public Vector2 size = new Vector2(200, 30);
    public List<String> options = new ArrayList<>();
    public Color backgroundColor = new Color(40, 40, 40);
    public Color textColor = Color.WHITE;
    public Color borderColor = Color.GRAY;
    public int cornerRadius = 0;
    public int fontSize = 14;

    // For relative position calculation
    public Vector2 canvasOffset = new Vector2(0, 0);
    public Vector2 canvasSize = new Vector2(1, 1);
    public int targetWidth = 1536;
    public int targetHeight = 864;

    public DropdownDescriptor() {
        options.add("Option 1");
        options.add("Option 2");
        options.add("Option 3");
    }

    /**
     * Generates the Builder code for this Dropdown with relative positioning.
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
        sb.append(varName).append(" = new Dropdown.Builder()\n");
        sb.append("        .pos(new Vector2(").append(targetX).append(", ").append(targetY).append("))\n");
        sb.append("        .size(new Vector2(").append(targetW).append(", ").append(targetH).append("))\n");

        // Build options string
        sb.append("        .options(");
        for (int i = 0; i < options.size(); i++) {
            sb.append("\"").append(options.get(i)).append("\"");
            if (i < options.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(")\n");

        sb.append("        .backgroundColor(new Color(").append(backgroundColor.getRed()).append(", ")
          .append(backgroundColor.getGreen()).append(", ").append(backgroundColor.getBlue()).append("))\n");
        sb.append("        .textColor(new Color(").append(textColor.getRed()).append(", ")
          .append(textColor.getGreen()).append(", ").append(textColor.getBlue()).append("))\n");
        sb.append("        .borderColor(new Color(").append(borderColor.getRed()).append(", ")
          .append(borderColor.getGreen()).append(", ").append(borderColor.getBlue()).append("))\n");
        sb.append("        .font(new Font(\"Arial\", Font.PLAIN, ").append(fontSize).append("))\n");
        if (cornerRadius > 0) {
            sb.append("        .cornerRadius(").append(cornerRadius).append(")\n");
        }
        sb.append("        .build();");
        return sb.toString();
    }
}
