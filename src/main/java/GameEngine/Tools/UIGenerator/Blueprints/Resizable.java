package GameEngine.Tools.UIGenerator.Blueprints;

import GameEngine.Core.util.Vector2;

import java.awt.*;

/**
 * Interface for blueprints that support resizing via handles.
 */
public interface Resizable {
    /**
     * Returns the minimum allowed size for this blueprint.
     */
    Vector2 getMinSize();

    /**
     * Returns the canvas bounds that constrain movement and resizing.
     */
    Rectangle getCanvasBounds();
}
