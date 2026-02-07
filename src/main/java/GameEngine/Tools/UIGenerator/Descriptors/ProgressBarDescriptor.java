package GameEngine.Tools.UIGenerator.Descriptors;

import GameEngine.Core.util.Vector2;

import java.awt.*;

/**
 * Descriptor containing all data needed to generate ProgressBar builder code.
 */
public class ProgressBarDescriptor {
    public String varName = "progressBar";
    public Vector2 pos = new Vector2(0, 0);
    public Vector2 size = new Vector2(200, 20);
    public float value = 0.5f;
    public Color backgroundColor = new Color(40, 40, 40);
    public Color fillColor = new Color(100, 200, 100);
    public Color borderColor = Color.GRAY;
    public boolean showPercentage = true;
    public boolean animated = true;

    // For relative position calculation
    public Vector2 canvasOffset = new Vector2(0, 0);
    public Vector2 canvasSize = new Vector2(1, 1);
    public int targetWidth = 1536;
    public int targetHeight = 864;

    /**
     * Generates the Builder code for this ProgressBar with relative positioning.
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
        sb.append(varName).append(" = new ProgressBar.Builder()\n");
        sb.append("        .pos(new Vector2(").append(targetX).append(", ").append(targetY).append("))\n");
        sb.append("        .size(new Vector2(").append(targetW).append(", ").append(targetH).append("))\n");
        sb.append("        .value(").append(value).append("f)\n");
        sb.append("        .backgroundColor(new Color(").append(backgroundColor.getRed()).append(", ")
          .append(backgroundColor.getGreen()).append(", ").append(backgroundColor.getBlue()).append("))\n");
        sb.append("        .fillColor(new Color(").append(fillColor.getRed()).append(", ")
          .append(fillColor.getGreen()).append(", ").append(fillColor.getBlue()).append("))\n");
        sb.append("        .borderColor(new Color(").append(borderColor.getRed()).append(", ")
          .append(borderColor.getGreen()).append(", ").append(borderColor.getBlue()).append("))\n");
        sb.append("        .showPercentage(").append(showPercentage).append(")\n");
        sb.append("        .animated(").append(animated).append(")\n");
        sb.append("        .build();");
        return sb.toString();
    }
}
