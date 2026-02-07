package GameEngine.Tools.UIGenerator.Descriptors;

import GameEngine.Core.util.Vector2;

import java.awt.*;

/**
 * Descriptor containing all data needed to generate RadioButton builder code.
 */
public class RadioButtonDescriptor {
    public String varName = "radioButton";
    public Vector2 pos = new Vector2(0, 0);
    public String label = "Radio Button";
    public String groupName = "default";
    public int circleSize = 20;
    public Color circleColor = new Color(40, 40, 40);
    public Color selectedColor = new Color(100, 200, 100);
    public Color labelColor = Color.WHITE;
    public int fontSize = 14;

    // For relative position calculation
    public Vector2 canvasOffset = new Vector2(0, 0);
    public Vector2 canvasSize = new Vector2(1, 1);
    public int targetWidth = 1536;
    public int targetHeight = 864;

    /**
     * Generates the Builder code for this RadioButton with relative positioning.
     */
    public String toBuilderCode() {
        // Calculate relative position within canvas
        float relX = (pos.x - canvasOffset.x) / canvasSize.x;
        float relY = (pos.y - canvasOffset.y) / canvasSize.y;
        int targetX = (int)(relX * targetWidth);
        int targetY = (int)(relY * targetHeight);

        StringBuilder sb = new StringBuilder();
        sb.append(varName).append(" = new RadioButton.Builder()\n");
        sb.append("        .pos(new Vector2(").append(targetX).append(", ").append(targetY).append("))\n");
        sb.append("        .label(\"").append(label).append("\")\n");
        sb.append("        .group(\"").append(groupName).append("\")\n");
        sb.append("        .circleSize(").append(circleSize).append(")\n");
        sb.append("        .circleColor(new Color(").append(circleColor.getRed()).append(", ")
          .append(circleColor.getGreen()).append(", ").append(circleColor.getBlue()).append("))\n");
        sb.append("        .selectedColor(new Color(").append(selectedColor.getRed()).append(", ")
          .append(selectedColor.getGreen()).append(", ").append(selectedColor.getBlue()).append("))\n");
        sb.append("        .labelColor(new Color(").append(labelColor.getRed()).append(", ")
          .append(labelColor.getGreen()).append(", ").append(labelColor.getBlue()).append("))\n");
        sb.append("        .labelFont(new Font(\"Arial\", Font.PLAIN, ").append(fontSize).append("))\n");
        sb.append("        .build();");
        return sb.toString();
    }
}
