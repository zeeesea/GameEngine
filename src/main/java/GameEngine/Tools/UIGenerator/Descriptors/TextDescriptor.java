package GameEngine.Tools.UIGenerator.Descriptors;

import GameEngine.Core.util.Vector2;

import java.awt.*;

/**
 * Descriptor containing all data needed to generate Text builder code.
 */
public class TextDescriptor {
    public String varName = "text";
    public Vector2 pos = new Vector2(0, 0);
    public String text = "Text";
    public Color color = Color.WHITE;
    public String fontName = "Arial";
    public int fontStyle = Font.PLAIN;
    public int fontSize = 16;
    public String alignment = "LEFT"; // LEFT, CENTER, RIGHT

    /**
     * Generates the Builder code for this text.
     */
    public String toBuilderCode() {
        StringBuilder sb = new StringBuilder();
        sb.append("Text ").append(varName).append(" = new Text.Builder(\"").append(text).append("\")\n");
        sb.append("        .position(new Vector2(").append((int)pos.x).append(", ").append((int)pos.y).append("))\n");
        sb.append("        .color(new Color(").append(color.getRed()).append(", ")
          .append(color.getGreen()).append(", ").append(color.getBlue()).append("))\n");

        String styleStr = fontStyle == Font.BOLD ? "Font.BOLD" : fontStyle == Font.ITALIC ? "Font.ITALIC" : "Font.PLAIN";
        sb.append("        .font(new Font(\"").append(fontName).append("\", ").append(styleStr).append(", ").append(fontSize).append("))\n");

        if (!alignment.equals("LEFT")) {
            sb.append("        .alignment(Text.TextAlignment.").append(alignment).append(")\n");
        }
        sb.append("        .build();");
        return sb.toString();
    }
}
