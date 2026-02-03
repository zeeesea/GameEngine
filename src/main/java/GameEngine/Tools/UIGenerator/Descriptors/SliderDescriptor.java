package GameEngine.Tools.UIGenerator.Descriptors;

import GameEngine.Core.util.Vector2;

import java.awt.*;

/**
 * Descriptor containing all data needed to generate Slider builder code.
 */
public class SliderDescriptor {
    public String varName = "slider";
    public Vector2 pos = new Vector2(0, 0);
    public Vector2 size = new Vector2(200, 20);
    public float minValue = 0f;
    public float maxValue = 100f;
    public float startValue = 50f;
    public Color backgroundColor = new Color(60, 60, 60);
    public Color fillColor = new Color(100, 150, 255);
    public Color handleColor = Color.WHITE;
    public int cornerRadius = 5;
    public boolean showValue = false;
    public String label = "";

    // For relative position calculation
    public Vector2 canvasOffset = new Vector2(0, 0);
    public Vector2 canvasSize = new Vector2(1, 1);
    public int targetWidth = 1536;
    public int targetHeight = 864;

    /**
     * Generates the Builder code for this slider with relative positioning.
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
        sb.append("Slider ").append(varName).append(" = new Slider.Builder()\n");
        sb.append("        .position(").append(targetX).append(", ").append(targetY).append(")\n");
        sb.append("        .size(").append(targetW).append(", ").append(targetH).append(")\n");
        sb.append("        .range(").append(minValue).append("f, ").append(maxValue).append("f)\n");
        sb.append("        .startValue(").append(startValue).append("f)\n");
        sb.append("        .backgroundColor(new Color(").append(backgroundColor.getRed()).append(", ")
          .append(backgroundColor.getGreen()).append(", ").append(backgroundColor.getBlue()).append("))\n");
        sb.append("        .fillColor(new Color(").append(fillColor.getRed()).append(", ")
          .append(fillColor.getGreen()).append(", ").append(fillColor.getBlue()).append("))\n");
        sb.append("        .handleColor(new Color(").append(handleColor.getRed()).append(", ")
          .append(handleColor.getGreen()).append(", ").append(handleColor.getBlue()).append("))\n");
        if (cornerRadius > 0) {
            sb.append("        .cornerRadius(").append(cornerRadius).append(")\n");
        }
        if (showValue) {
            sb.append("        .showValue(true)\n");
        }
        if (!label.isEmpty()) {
            sb.append("        .label(\"").append(label).append("\")\n");
        }
        sb.append("        .build();");
        return sb.toString();
    }
}
