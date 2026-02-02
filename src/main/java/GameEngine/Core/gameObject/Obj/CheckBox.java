package GameEngine.Core.gameObject.Obj;

import GameEngine.Core.gameObject.FuncInt.*;
import GameEngine.Core.gameObject.GameObject;
import GameEngine.Core.input.Input;
import GameEngine.Core.util.Vector2;

import java.awt.*;

/**
 * A checkbox UI component for boolean on/off selection.
 * Supports hover effects, custom colors, labels, and change callbacks.
 * Use the Builder pattern to create instances.
 */
public class CheckBox extends GameObject {
    //<editor-fold desc="VARIABLES">
    private boolean checked = false;
    private String label = "";

    private int boxSize = 20;
    private Color boxColor = new Color(40, 40, 40);
    private Color checkedColor = new Color(100, 200, 100);
    private Color borderColor = Color.GRAY;
    private Color hoverBorderColor = Color.CYAN;
    private Color labelColor = Color.WHITE;
    private Font labelFont = new Font("Arial", Font.PLAIN, 14);
    private int labelSpacing = 10;

    private boolean hovering = false;
    private boolean lastMouseState = false;

    private FuncIntOne<Boolean> onChanged;
    private FuncInt onChecked;
    private FuncInt onUnchecked;
    //</editor-fold>

    //<editor-fold desc="CONSTRUCTOR/BUILDER">
    private CheckBox(Vector2 pos) {
        transform.position = pos;
        transform.scale = new Vector2(boxSize, boxSize);
        renderOrder = 100;
    }

    public static class Builder {
        private Vector2 pos = new Vector2(0, 0);
        private String label = "";
        private boolean initialChecked = false;

        private int boxSize = 20;
        private Color boxColor = new Color(40, 40, 40);
        private Color checkedColor = new Color(100, 200, 100);
        private Color borderColor = Color.GRAY;
        private Color hoverBorderColor = Color.CYAN;
        private Color labelColor = Color.WHITE;
        private Font labelFont = new Font("Arial", Font.PLAIN, 14);
        private int labelSpacing = 10;

        private FuncIntOne<Boolean> onChanged;
        private FuncInt onChecked;
        private FuncInt onUnchecked;

        public Builder() {}

        public Builder pos(Vector2 pos) {
            this.pos = pos;
            return this;
        }

        public Builder label(String label) {
            this.label = label;
            return this;
        }

        public Builder checked(boolean checked) {
            this.initialChecked = checked;
            return this;
        }

        public Builder boxSize(int size) {
            this.boxSize = size;
            return this;
        }

        public Builder boxColor(Color color) {
            this.boxColor = color;
            return this;
        }

        public Builder checkedColor(Color color) {
            this.checkedColor = color;
            return this;
        }

        public Builder borderColor(Color color) {
            this.borderColor = color;
            return this;
        }

        public Builder hoverBorderColor(Color color) {
            this.hoverBorderColor = color;
            return this;
        }

        public Builder labelColor(Color color) {
            this.labelColor = color;
            return this;
        }

        public Builder labelFont(Font font) {
            this.labelFont = font;
            return this;
        }

        public Builder labelSpacing(int spacing) {
            this.labelSpacing = spacing;
            return this;
        }

        public Builder onChanged(FuncIntOne<Boolean> callback) {
            this.onChanged = callback;
            return this;
        }

        public Builder onChecked(FuncInt callback) {
            this.onChecked = callback;
            return this;
        }

        public Builder onUnchecked(FuncInt callback) {
            this.onUnchecked = callback;
            return this;
        }

        public CheckBox build() {
            CheckBox cb = new CheckBox(pos);
            cb.label = label;
            cb.checked = initialChecked;
            cb.boxSize = boxSize;
            cb.boxColor = boxColor;
            cb.checkedColor = checkedColor;
            cb.borderColor = borderColor;
            cb.hoverBorderColor = hoverBorderColor;
            cb.labelColor = labelColor;
            cb.labelFont = labelFont;
            cb.labelSpacing = labelSpacing;
            cb.onChanged = onChanged;
            cb.onChecked = onChecked;
            cb.onUnchecked = onUnchecked;
            cb.transform.scale = new Vector2(boxSize, boxSize);
            return cb;
        }
    }
    //</editor-fold>

    //<editor-fold desc="UPDATE/INPUT">
    @Override
    public void init() {}

    @Override
    public void update(double deltaTime) {
        if (!active) return;

        // Prüfe ob ein Dropdown offen ist - dann keine Interaktion
        if (Dropdown.isAnyDropdownExpanded()) {
            hovering = false;
            return;
        }

        Vector2 mousePos = Input.getMousePosition();
        hovering = isMouseInside(mousePos);

        boolean mousePressed = Input.getMouseButton(Input.MouseCode.LEFT);

        if (hovering && mousePressed && !lastMouseState) {
            toggle();
        }

        lastMouseState = mousePressed;
    }

    private boolean isMouseInside(Vector2 point) {
        return point.x >= transform.position.x &&
                point.x <= transform.position.x + boxSize &&
                point.y >= transform.position.y &&
                point.y <= transform.position.y + boxSize;
    }
    //</editor-fold>

    //<editor-fold desc="HELPER METHODS">
    private void toggle() {
        setChecked(!checked);
    }

    public void setChecked(boolean checked) {
        if (this.checked == checked) return;

        this.checked = checked;

        if (onChanged != null) {
            onChanged.call(checked);
        }

        if (checked && onChecked != null) {
            onChecked.call();
        }

        if (!checked && onUnchecked != null) {
            onUnchecked.call();
        }
    }

    public boolean isChecked() {
        return checked;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
    //</editor-fold>

    //<editor-fold desc="DRAW">
    @Override
    public void draw(Graphics2D g) {
        if (!active) return;

        int x = transform.position.xToInt();
        int y = transform.position.yToInt();

        // Box background
        g.setColor(checked ? checkedColor : boxColor);
        g.fillRect(x, y, boxSize, boxSize);

        // Box border
        g.setColor(hovering ? hoverBorderColor : borderColor);
        g.setStroke(new BasicStroke(hovering ? 2 : 1));
        g.drawRect(x, y, boxSize, boxSize);

        // Checkmark
        if (checked) {
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(2));

            int offset = 4;
            int x1 = x + offset;
            int y1 = y + boxSize / 2;
            int x2 = x + boxSize / 2;
            int y2 = y + boxSize - offset;
            int x3 = x + boxSize - offset;
            int y3 = y + offset;

            g.drawLine(x1, y1, x2, y2);
            g.drawLine(x2, y2, x3, y3);
        }

        // Label
        if (!label.isEmpty()) {
            g.setFont(labelFont);
            g.setColor(labelColor);
            FontMetrics fm = g.getFontMetrics();
            int labelX = x + boxSize + labelSpacing;
            int labelY = y + (boxSize + fm.getAscent()) / 2 - 2;
            g.drawString(label, labelX, labelY);
        }
    }

    @Override
    public void onCollision(GameObject collider) {}
    //</editor-fold>
}