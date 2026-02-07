package GameEngine.Tools.UIGenerator.Descriptors;

import GameEngine.Core.util.Vector2;

import java.awt.*;

/**
 * Descriptor containing all data needed to generate UIRect builder code.
 */
public class UIRectDescriptor {
    public String varName = "rect";
    public Vector2 pos = new Vector2(0, 0);
    public Vector2 size = new Vector2(100, 100);
    public Color fillColor = new Color(50, 50, 55);
    public Color borderColor = new Color(80, 80, 85);
    public int cornerRadius = 8;
    public int borderWidth = 1;
    public boolean hasBorder = true;
    public boolean hasFill = true;

    // For relative position calculation
    public Vector2 canvasOffset = new Vector2(0, 0);
    public Vector2 canvasSize = new Vector2(1, 1);
    public int targetWidth = 1536;
    public int targetHeight = 864;

    /**
     * Generates the Builder code for this UIRect with relative positioning.
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
        sb.append(varName).append(" = new UIRect.Builder()\n");
        sb.append("        .rect(new Rectangle(").append(targetX).append(", ").append(targetY)
          .append(", ").append(targetW).append(", ").append(targetH).append("))\n");
        sb.append("        .fillColor(new Color(").append(fillColor.getRed()).append(", ")
          .append(fillColor.getGreen()).append(", ").append(fillColor.getBlue()).append("))\n");
        sb.append("        .borderColor(new Color(").append(borderColor.getRed()).append(", ")
          .append(borderColor.getGreen()).append(", ").append(borderColor.getBlue()).append("))\n");
        if (cornerRadius > 0) {
            sb.append("        .cornerRadius(").append(cornerRadius).append(")\n");
        }
        sb.append("        .borderWidth(").append(borderWidth).append(")\n");
        sb.append("        .hasBorder(").append(hasBorder).append(")\n");
        sb.append("        .hasFill(").append(hasFill).append(")\n");
        sb.append("        .renderOrder(-1) // Behind other elements\n");
        sb.append("        .build();");
        return sb.toString();
    }
}
