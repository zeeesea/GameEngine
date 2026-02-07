package GameEngine.Tools.UIGenerator;

import GameEngine.Core.util.ClipBoard;
import GameEngine.Tools.UIGenerator.Blueprints.UIBlueprint;

import java.util.List;

/**
 * Exports UI blueprints as Java builder code.
 */
public class CodeExporter {

    /**
     * Generates code for all blueprints and copies to clipboard.
     *
     * @param blueprints List of UI blueprints to export
     * @return The generated code string
     */
    public static String exportToClipboard(List<UIBlueprint> blueprints) {
        String code = generateCode(blueprints);
        ClipBoard.copy(code);
        return code;
    }

    /**
     * Generates Java code for all blueprints.
     * Outputs declarations first, then initializations.
     *
     * @param blueprints List of UI blueprints
     * @return Generated code as string
     */
    public static String generateCode(List<UIBlueprint> blueprints) {
        if (blueprints == null || blueprints.isEmpty()) {
            return "// No UI elements to export";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("// === Generated UI Code ===\n\n");

        // First: Generate all declarations
        sb.append("// --- Declarations ---\n");
        for (UIBlueprint bp : blueprints) {
            sb.append(bp.getTypeName()).append(" ").append(bp.getVarName()).append(";\n");
        }
        sb.append("\n");

        // Second: Generate all initializations
        sb.append("// --- Initializations ---\n");
        for (int i = 0; i < blueprints.size(); i++) {
            UIBlueprint bp = blueprints.get(i);
            sb.append("// ").append(bp.getTypeName()).append(": ").append(bp.getVarName()).append("\n");
            sb.append(bp.toBuilderCode());
            sb.append("\n");

            // Add objectManager.add() call
            sb.append("objectManager.add(").append(bp.getVarName()).append(");\n");

            if (i < blueprints.size() - 1) {
                sb.append("\n");
            }
        }

        return sb.toString();
    }

    /**
     * Generates code for a single blueprint.
     *
     * @param blueprint The blueprint to export
     * @return Generated code as string
     */
    public static String generateSingleCode(UIBlueprint blueprint) {
        if (blueprint == null) {
            return "";
        }
        return blueprint.getTypeName() + " " + blueprint.getVarName() + ";\n\n" +
               blueprint.toBuilderCode() + "\nobjectManager.add(" + blueprint.getVarName() + ");";
    }
}
