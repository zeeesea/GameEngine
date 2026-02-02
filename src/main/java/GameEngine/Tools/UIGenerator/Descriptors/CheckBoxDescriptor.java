package GameEngine.Tools.UIGenerator.Descriptors;

import GameEngine.Core.util.Vector2;

import java.awt.*;

/**
 * Descriptor containing all data needed to generate CheckBox builder code.
 */
public class CheckBoxDescriptor {
    public String varName = "checkBox";
    public Vector2 pos = new Vector2(0, 0);
    public String label = "CheckBox";
    public boolean checked = false;
    public int boxSize = 20;
    public Color boxColor = new Color(40, 40, 40);
    public Color checkedColor = new Color(100, 200, 100);
    public Color labelColor = Color.WHITE;
    public int fontSize = 14;

    /**
     * Generates the Builder code for this checkbox.
     */
    public String toBuilderCode() {
        StringBuilder sb = new StringBuilder();
        sb.append("CheckBox ").append(varName).append(" = new CheckBox.Builder()\n");
        sb.append("        .pos(new Vector2(").append((int)pos.x).append(", ").append((int)pos.y).append("))\n");
        sb.append("        .label(\"").append(label).append("\")\n");
        if (checked) {
            sb.append("        .checked(true)\n");
        }
        sb.append("        .boxSize(").append(boxSize).append(")\n");
        sb.append("        .boxColor(new Color(").append(boxColor.getRed()).append(", ")
          .append(boxColor.getGreen()).append(", ").append(boxColor.getBlue()).append("))\n");
        sb.append("        .checkedColor(new Color(").append(checkedColor.getRed()).append(", ")
          .append(checkedColor.getGreen()).append(", ").append(checkedColor.getBlue()).append("))\n");
        sb.append("        .labelColor(new Color(").append(labelColor.getRed()).append(", ")
          .append(labelColor.getGreen()).append(", ").append(labelColor.getBlue()).append("))\n");
        sb.append("        .labelFont(new Font(\"Arial\", Font.PLAIN, ").append(fontSize).append("))\n");
        sb.append("        .build();");
        return sb.toString();
    }
}
