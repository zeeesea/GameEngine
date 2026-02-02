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

    /**
     * Generates the Builder code for this slider.
     */
    public String toBuilderCode() {
        StringBuilder sb = new StringBuilder();
        sb.append("Slider ").append(varName).append(" = new Slider.Builder()\n");
        sb.append("        .position(").append((int)pos.x).append(", ").append((int)pos.y).append(")\n");
        sb.append("        .size(").append((int)size.x).append(", ").append((int)size.y).append(")\n");
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
