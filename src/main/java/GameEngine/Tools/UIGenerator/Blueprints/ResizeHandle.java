package GameEngine.Tools.UIGenerator.Blueprints;

import GameEngine.Core.gameObject.GameObject;
import GameEngine.Core.input.Input;
import GameEngine.Core.util.Vector2;

import java.awt.*;

/**
 * A resize handle that can be placed at corners of UI blueprints
 * to allow resizing via drag interaction.
 */
public class ResizeHandle extends GameObject {

    public enum Position {
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
    }

    private static final int HANDLE_SIZE = 12;

    private final Position pos;
    private final GameObject owner;
    private final Resizable resizable;

    private Color normalColor = new Color(0, 150, 255);
    private Color hoverColor = new Color(50, 200, 255);
    private boolean hovering = false;

    /**
     * Creates a resize handle for a resizable blueprint.
     *
     * @param pos   The corner position of this handle
     * @param owner The blueprint that owns this handle (must implement Resizable)
     */
    public ResizeHandle(Position pos, GameObject owner) {
        this.pos = pos;
        this.owner = owner;

        if (owner instanceof Resizable) {
            this.resizable = (Resizable) owner;
        } else {
            throw new IllegalArgumentException("Owner must implement Resizable interface");
        }

        transform.scale = new Vector2(HANDLE_SIZE, HANDLE_SIZE);
    }

    @Override
    public void init() {
        renderOrder = 15;
    }

    @Override
    public void update(double deltaTime) {
        // Update position based on owner
        Vector2 cornerPos = getCornerPosition();
        transform.setPositionCentered(cornerPos);

        // Check hover using distance-based detection (larger hit area)
        Vector2 mousePos = Input.getMousePosition();
        hovering = mousePos.distance(cornerPos) < HANDLE_SIZE;

        // Handle dragging - allow if hovering or already dragging
        if (hovering || isDragging()) {
            draggable(Input.MouseCode.LEFT);
        }

        if (isDragging()) {
            resize();
        }
    }

    private Vector2 getCornerPosition() {
        Vector2 ownerPos = owner.transform.position;
        Vector2 ownerSize = owner.transform.scale;

        switch (pos) {
            case TOP_LEFT:
                return ownerPos.copy();
            case TOP_RIGHT:
                return new Vector2(ownerPos.x + ownerSize.x, ownerPos.y);
            case BOTTOM_LEFT:
                return new Vector2(ownerPos.x, ownerPos.y + ownerSize.y);
            case BOTTOM_RIGHT:
                return ownerPos.add(ownerSize);
            default:
                return ownerPos;
        }
    }

    private void resize() {
        Vector2 mousePos = Input.getMousePosition();
        Vector2 ownerPos = owner.transform.position;
        Vector2 ownerSize = owner.transform.scale;
        Vector2 minSize = resizable.getMinSize();
        Rectangle bounds = resizable.getCanvasBounds();

        switch (pos) {
            case TOP_LEFT: {
                float newW = (ownerPos.x + ownerSize.x) - mousePos.x;
                float newH = (ownerPos.y + ownerSize.y) - mousePos.y;

                if (newW >= minSize.x && (bounds == null || mousePos.x >= bounds.x)) {
                    owner.transform.position.x = mousePos.x;
                    owner.transform.scale.x = newW;
                }
                if (newH >= minSize.y && (bounds == null || mousePos.y >= bounds.y)) {
                    owner.transform.position.y = mousePos.y;
                    owner.transform.scale.y = newH;
                }
                break;
            }
            case TOP_RIGHT: {
                float newW = mousePos.x - ownerPos.x;
                float newH = (ownerPos.y + ownerSize.y) - mousePos.y;

                if (newW >= minSize.x && (bounds == null || mousePos.x <= bounds.x + bounds.width)) {
                    owner.transform.scale.x = newW;
                }
                if (newH >= minSize.y && (bounds == null || mousePos.y >= bounds.y)) {
                    owner.transform.position.y = mousePos.y;
                    owner.transform.scale.y = newH;
                }
                break;
            }
            case BOTTOM_LEFT: {
                float newW = (ownerPos.x + ownerSize.x) - mousePos.x;
                float newH = mousePos.y - ownerPos.y;

                if (newW >= minSize.x && (bounds == null || mousePos.x >= bounds.x)) {
                    owner.transform.position.x = mousePos.x;
                    owner.transform.scale.x = newW;
                }
                if (newH >= minSize.y && (bounds == null || mousePos.y <= bounds.y + bounds.height)) {
                    owner.transform.scale.y = newH;
                }
                break;
            }
            case BOTTOM_RIGHT: {
                float newW = mousePos.x - ownerPos.x;
                float newH = mousePos.y - ownerPos.y;

                if (newW >= minSize.x && (bounds == null || mousePos.x <= bounds.x + bounds.width)) {
                    owner.transform.scale.x = newW;
                }
                if (newH >= minSize.y && (bounds == null || mousePos.y <= bounds.y + bounds.height)) {
                    owner.transform.scale.y = newH;
                }
                break;
            }
        }
    }

    @Override
    public void draw(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(hovering || isDragging() ? hoverColor : normalColor);
        g.fillOval(
            transform.position.xToInt(),
            transform.position.yToInt(),
            transform.scale.xToInt(),
            transform.scale.yToInt()
        );
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    }

    @Override
    public void onCollision(GameObject collider) {}
}
