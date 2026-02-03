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

    // For relative position calculation
    public Vector2 canvasOffset = new Vector2(0, 0);
    public Vector2 canvasSize = new Vector2(1, 1);
    public int targetWidth = 1536;
    public int targetHeight = 864;

    /**
     * Generates the Builder code for this text with relative positioning.
     *
     * Important: {@link GameEngine.Core.gameObject.Obj.Text} draws using Java2D's baseline Y.
     * In the UI generator we treat {@code pos} as a "top-left" anchor for placement.
     * So we must convert top-left -> baseline when exporting, otherwise changing fontSize will
     * shift the effective on-screen position.
     */
    public String toBuilderCode() {
        // Calculate relative position within canvas
        float relX = (pos.x - canvasOffset.x) / canvasSize.x;

        // Convert editor anchor (top) into a baseline Y for export.
        // We don't have FontMetrics here, so we use a stable approximation:
        // baseline ~= top + fontSize
        float baselineYInCanvas = (pos.y - canvasOffset.y) + fontSize;
        float relBaselineY = baselineYInCanvas / canvasSize.y;

        int targetX = (int) (relX * targetWidth);
        int targetY = (int) (relBaselineY * targetHeight);

        StringBuilder sb = new StringBuilder();
        sb.append("Text ").append(varName).append(" = new Text.Builder(\"").append(text).append("\")\n");
        sb.append("        .position(new Vector2(").append(targetX).append(", ").append(targetY).append("))\n");
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
