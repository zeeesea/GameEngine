package GameEngine.Core.gameObject.Obj;

import GameEngine.Core.gameObject.FuncInt.*;
import GameEngine.Core.gameObject.GameObject;
import GameEngine.Core.input.Input;
import GameEngine.Core.util.Vector2;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A radio button UI component for single-selection within a group.
 * Multiple radio buttons with the same group name allow only one selection.
 * Supports hover effects, custom colors, labels, and selection callbacks.
 * Use the Builder pattern to create instances.
 */
public class RadioButton extends GameObject {
    //<editor-fold desc="VARIABLES">
    private static List<RadioButtonGroup> groups = new ArrayList<>();

    private String groupName = "default";
    private boolean selected = false;
    private String label = "";

    private int circleSize = 20;
    private Color circleColor = new Color(40, 40, 40);
    private Color selectedColor = new Color(100, 200, 100);
    private Color borderColor = Color.GRAY;
    private Color hoverBorderColor = Color.CYAN;
    private Color labelColor = Color.WHITE;
    private Font labelFont = new Font("Arial", Font.PLAIN, 14);
    private int labelSpacing = 10;

    private boolean hovering = false;
    private boolean lastMouseState = false;

    private FuncInt onSelected;
    private FuncInt onDeselected;
    private FuncIntOne<String> onGroupChanged;
    //</editor-fold>

    //<editor-fold desc="CONSTRUCTOR/BUILDER">
    private RadioButton(Vector2 pos) {
        transform.position = pos;
        transform.scale = new Vector2(circleSize, circleSize);
        renderOrder = 99;
    }

    public static class Builder {
        private Vector2 pos = new Vector2(0, 0);
        private String groupName = "default";
        private String label = "";
        private boolean initialSelected = false;

        private int circleSize = 20;
        private Color circleColor = new Color(40, 40, 40);
        private Color selectedColor = new Color(100, 200, 100);
        private Color borderColor = Color.GRAY;
        private Color hoverBorderColor = Color.CYAN;
        private Color labelColor = Color.WHITE;
        private Font labelFont = new Font("Arial", Font.PLAIN, 14);
        private int labelSpacing = 10;

        private FuncInt onSelected;
        private FuncInt onDeselected;
        private FuncIntOne<String> onGroupChanged;

        public Builder() {}

        public Builder pos(Vector2 pos) { this.pos = pos; return this; }
        public Builder group(String groupName) { this.groupName = groupName; return this; }
        public Builder label(String label) { this.label = label; return this; }
        public Builder selected(boolean selected) { this.initialSelected = selected; return this; }
        public Builder circleSize(int size) { this.circleSize = size; return this; }
        public Builder circleColor(Color color) { this.circleColor = color; return this; }
        public Builder selectedColor(Color color) { this.selectedColor = color; return this; }
        public Builder borderColor(Color color) { this.borderColor = color; return this; }
        public Builder hoverBorderColor(Color color) { this.hoverBorderColor = color; return this; }
        public Builder labelColor(Color color) { this.labelColor = color; return this; }
        public Builder labelFont(Font font) { this.labelFont = font; return this; }
        public Builder labelSpacing(int spacing) { this.labelSpacing = spacing; return this; }
        public Builder onSelected(FuncInt callback) { this.onSelected = callback; return this; }
        public Builder onDeselected(FuncInt callback) { this.onDeselected = callback; return this; }
        public Builder onGroupChanged(FuncIntOne<String> callback) { this.onGroupChanged = callback; return this; }

        public RadioButton build() {
            RadioButton rb = new RadioButton(pos);
            rb.groupName = groupName;
            rb.label = label;
            rb.selected = initialSelected;
            rb.circleSize = circleSize;
            rb.circleColor = circleColor;
            rb.selectedColor = selectedColor;
            rb.borderColor = borderColor;
            rb.hoverBorderColor = hoverBorderColor;
            rb.labelColor = labelColor;
            rb.labelFont = labelFont;
            rb.labelSpacing = labelSpacing;
            rb.onSelected = onSelected;
            rb.onDeselected = onDeselected;
            rb.onGroupChanged = onGroupChanged;
            rb.transform.scale = new Vector2(circleSize, circleSize);

            RadioButtonGroup group = getOrCreateGroup(groupName);
            group.addButton(rb);

            if (initialSelected) rb.setSelected(true);

            return rb;
        }
    }
    //</editor-fold>

    //<editor-fold desc="GROUP MANAGEMENT">
    private static class RadioButtonGroup {
        String name;
        List<RadioButton> buttons = new ArrayList<>();

        RadioButtonGroup(String name) { this.name = name; }
        void addButton(RadioButton button) { buttons.add(button); }

        void selectButton(RadioButton button) {
            for (RadioButton rb : buttons) {
                if (rb != button && rb.selected) {
                    rb.deselect();
                }
            }
        }

        RadioButton getSelected() {
            for (RadioButton rb : buttons) {
                if (rb.selected) return rb;
            }
            return null;
        }
    }

    private static RadioButtonGroup getOrCreateGroup(String name) {
        for (RadioButtonGroup group : groups) {
            if (group.name.equals(name)) return group;
        }
        RadioButtonGroup newGroup = new RadioButtonGroup(name);
        groups.add(newGroup);
        return newGroup;
    }

    public static RadioButton getSelectedInGroup(String groupName) {
        RadioButtonGroup group = getOrCreateGroup(groupName);
        return group.getSelected();
    }

    public static String getSelectedLabelInGroup(String groupName) {
        RadioButton selected = getSelectedInGroup(groupName);
        return selected != null ? selected.label : null;
    }
    //</editor-fold>

    //<editor-fold desc="UPDATE/INPUT">
    @Override
    public void init() {}

    @Override
    public void update(double deltaTime) {
        if (!active) return;

        // Prüfe ob ein Dropdown offen ist - dann kein Hover
        if (Dropdown.isAnyDropdownExpanded()) {
            hovering = false;
            return;
        }

        hovering = isMouseInsideCircle(Input.getMousePosition());
    }


    private boolean isMouseInsideCircle(Vector2 point) {
        float centerX = transform.position.x + circleSize / 2f;
        float centerY = transform.position.y + circleSize / 2f;
        float radius = circleSize / 2f;

        float dx = point.x - centerX;
        float dy = point.y - centerY;

        return (dx * dx + dy * dy) <= (radius * radius);
    }
    //</editor-fold>

    //<editor-fold desc="HELPER METHODS">
    public void setSelected(boolean selected) {
        if (this.selected == selected) return;

        if (selected) {
            RadioButtonGroup group = getOrCreateGroup(groupName);
            group.selectButton(this);

            this.selected = true;

            if (onSelected != null) onSelected.call();
            if (onGroupChanged != null) onGroupChanged.call(label);
        } else {
            deselect();
        }
    }

    @Override
    public void onMousePressed(int x, int y, int button) {
        // Prüfe ob ein Dropdown offen ist - dann keine Interaktion
        if (Dropdown.isAnyDropdownExpanded()) return;

        Vector2 mousePos = new Vector2(x, y);
        if (isMouseInsideCircle(mousePos)) {
            setSelected(true);
        }
    }


    private void deselect() {
        if (!selected) return;
        selected = false;
        if (onDeselected != null) onDeselected.call();
    }

    public boolean isSelected() { return selected; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public String getGroupName() { return groupName; }
    //</editor-fold>

    //<editor-fold desc="DRAW">
    @Override
    public void draw(Graphics2D g) {
        if (!active) return;

        int x = transform.position.xToInt();
        int y = transform.position.yToInt();

        g.setColor(circleColor);
        g.fillOval(x, y, circleSize, circleSize);

        g.setColor(hovering ? hoverBorderColor : borderColor);
        g.setStroke(new BasicStroke(hovering ? 2 : 1));
        g.drawOval(x, y, circleSize, circleSize);

        if (selected) {
            g.setColor(selectedColor);
            int dotSize = circleSize / 2;
            int dotOffset = circleSize / 4;
            g.fillOval(x + dotOffset, y + dotOffset, dotSize, dotSize);
        }

        if (!label.isEmpty()) {
            g.setFont(labelFont);
            g.setColor(labelColor);
            FontMetrics fm = g.getFontMetrics();
            int labelX = x + circleSize + labelSpacing;
            int labelY = y + (circleSize + fm.getAscent()) / 2 - 2;
            g.drawString(label, labelX, labelY);
        }
    }

    @Override
    public void onCollision(GameObject collider) {}
    //</editor-fold>
}