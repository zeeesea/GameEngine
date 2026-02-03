package GameEngine.Tools.UIGenerator.Blueprints;

/**
 * Interface for all UI element blueprints in the UI Generator.
 * Blueprints are draggable, resizable representations of UI elements.
 */
public interface UIBlueprint {

    /**
     * Returns the type name of this blueprint (e.g., "Button", "Slider").
     */
    String getTypeName();

    /**
     * Returns the variable name for code generation.
     */
    String getVarName();

    /**
     * Sets the variable name.
     */
    void setVarName(String name);

    /**
     * Generates the builder code for this element.
     */
    String toBuilderCode();

    /**
     * Checks if this blueprint is currently selected.
     */
    boolean isSelected();

    /**
     * Sets the selected state.
     */
    void setSelected(boolean selected);

    /**
     * Destroys this blueprint and its children (like resize handles).
     */
    void destroyBlueprint();

    /**
     * Sets the target resolution for code generation.
     */
    void setTargetResolution(int width, int height);
}
