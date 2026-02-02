package GameEngine.Core.gameObject.Obj;

import GameEngine.Core.gameObject.FuncInt.*;
import GameEngine.Core.gameObject.GameObject;
import GameEngine.Core.gameObject.Transform;
import GameEngine.Core.input.Input;
import GameEngine.Core.util.Vector2;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A dropdown/select UI component that displays a list of selectable options.
 * When clicked, it expands to show all available options and allows the user to select one.
 * Supports rounded corners, custom colors, and various callback events.
 */
public class Dropdown extends GameObject {
    //<editor-fold desc="VARIABLES">
    // Statische Variable um zu tracken welches Dropdown gerade offen ist
    private static Dropdown expandedDropdown = null;

    private List<String> options = new ArrayList<>();
    private int selectedIndex = 0;
    private boolean expanded = false;

    private Color backgroundColor = new Color(40, 40, 40);
    private Color selectedColor = new Color(60, 60, 60);
    private Color hoverColor = new Color(80, 80, 80);
    private Color borderColor = Color.GRAY;
    private Color textColor = Color.WHITE;
    private Font font = new Font("Arial", Font.PLAIN, 14);
    private int padding = 10;
    private int itemHeight = 30;
    private int maxVisibleItems = 5;
    private int cornerRadius = 0;

    private int hoveredIndex = -1;
    private boolean lastMouseState = false;

    private FuncIntOne<Integer> onIndexChanged;
    private FuncIntOne<String> onSelectionChanged;
    private FuncIntTwo<Integer, String> onItemSelected;
    //</editor-fold>

    //<editor-fold desc="STATIC INPUT BLOCKING">
    /**
     * Checks if input events should be blocked for other UI elements.
     * Returns true when a dropdown is open, preventing elements behind it from receiving input.
     *
     * @param point The position to check (currently unused, blocks all input when any dropdown is open)
     * @return true if input should be blocked, false otherwise
     */
    public static boolean isInputBlocked(Vector2 point) {
        if (expandedDropdown == null) return false;
        // Blockiere alle Inputs solange ein Dropdown offen ist
        return true;
    }

    /**
     * Checks if any dropdown is currently expanded/open.
     *
     * @return true if a dropdown is expanded, false otherwise
     */
    public static boolean isAnyDropdownExpanded() {
        return expandedDropdown != null;
    }

    /**
     * Returns the currently expanded dropdown instance.
     *
     * @return The expanded Dropdown, or null if none is open
     */
    public static Dropdown getExpandedDropdown() {
        return expandedDropdown;
    }
    //</editor-fold>

    //<editor-fold desc="CONSTRUCTOR/BUILDER">
    private Dropdown(Rectangle rect) {
        transform = new Transform(rect);
        renderOrder = 100;
    }

    public static class Builder {
        private Rectangle rect = new Rectangle(0, 0, 200, 30);
        private List<String> options = new ArrayList<>();
        private int selectedIndex = 0;

        private Color backgroundColor = new Color(40, 40, 40);
        private Color selectedColor = new Color(60, 60, 60);
        private Color hoverColor = new Color(80, 80, 80);
        private Color borderColor = Color.GRAY;
        private Color textColor = Color.WHITE;
        private Font font = new Font("Arial", Font.PLAIN, 14);
        private int maxVisibleItems = 5;
        private int cornerRadius = 0;

        private FuncIntOne<Integer> onIndexChanged;
        private FuncIntOne<String> onSelectionChanged;
        private FuncIntTwo<Integer, String> onItemSelected;

        public Builder() {}

        public Builder rect(Rectangle rect) { this.rect = rect; return this; }
        public Builder pos(Vector2 pos) { rect.x = pos.xToInt(); rect.y = pos.yToInt(); return this; }
        public Builder size(Vector2 size) { rect.width = size.xToInt(); rect.height = size.yToInt(); return this; }
        public Builder options(String... options) { this.options = new ArrayList<>(Arrays.asList(options)); return this; }
        public Builder options(List<String> options) { this.options = new ArrayList<>(options); return this; }
        public Builder selectedIndex(int index) { this.selectedIndex = index; return this; }
        public Builder backgroundColor(Color color) { this.backgroundColor = color; return this; }
        public Builder selectedColor(Color color) { this.selectedColor = color; return this; }
        public Builder hoverColor(Color color) { this.hoverColor = color; return this; }
        public Builder borderColor(Color color) { this.borderColor = color; return this; }
        public Builder textColor(Color color) { this.textColor = color; return this; }
        public Builder font(Font font) { this.font = font; return this; }
        public Builder maxVisibleItems(int count) { this.maxVisibleItems = count; return this; }
        public Builder cornerRadius(int radius) { this.cornerRadius = Math.max(0, radius); return this; }
        public Builder onIndexChanged(FuncIntOne<Integer> callback) { this.onIndexChanged = callback; return this; }
        public Builder onSelectionChanged(FuncIntOne<String> callback) { this.onSelectionChanged = callback; return this; }
        public Builder onItemSelected(FuncIntTwo<Integer, String> callback) { this.onItemSelected = callback; return this; }

        public Dropdown build() {
            Dropdown dd = new Dropdown(rect);
            dd.options = new ArrayList<>(options);
            dd.selectedIndex = Math.max(0, Math.min(selectedIndex, options.size() - 1));
            dd.backgroundColor = backgroundColor;
            dd.selectedColor = selectedColor;
            dd.hoverColor = hoverColor;
            dd.borderColor = borderColor;
            dd.textColor = textColor;
            dd.font = font;
            dd.itemHeight = rect.height;
            dd.maxVisibleItems = maxVisibleItems;
            dd.cornerRadius = cornerRadius;
            dd.onIndexChanged = onIndexChanged;
            dd.onSelectionChanged = onSelectionChanged;
            dd.onItemSelected = onItemSelected;
            return dd;
        }
    }
    //</editor-fold>

    //<editor-fold desc="UPDATE/INPUT">
    @Override
    public void init() {}

    @Override
    public void update(double deltaTime) {
        if (!active) return;

        Vector2 mousePos = Input.getMousePosition();
        boolean mousePressed = Input.getMouseButton(Input.MouseCode.LEFT);

        if (expanded) {
            updateExpanded(mousePos, mousePressed);
        } else {
            updateCollapsed(mousePos, mousePressed);
        }

        lastMouseState = mousePressed;
    }

    private void updateCollapsed(Vector2 mousePos, boolean mousePressed) {
        boolean isOver = isMouseInsideMain(mousePos);

        if (isOver && mousePressed && !lastMouseState) {
            expanded = true;
            expandedDropdown = this;
            hoveredIndex = -1;
        }
    }

    private void updateExpanded(Vector2 mousePos, boolean mousePressed) {
        boolean isOverDropdown = isMouseOverDropdown(mousePos);

        // Schließe Dropdown wenn außerhalb geklickt wurde
        if (!isOverDropdown && mousePressed && !lastMouseState) {
            expanded = false;
            expandedDropdown = null;
            hoveredIndex = -1;
            return;
        }

        // Update hovering
        hoveredIndex = getHoveredItemIndex(mousePos);

        // Wähle Item aus wenn geklickt
        if (hoveredIndex >= 0 && mousePressed && !lastMouseState) {
            selectItem(hoveredIndex);
            expanded = false;
            expandedDropdown = null;
        }
    }
    //</editor-fold>

    private boolean isMouseInsideMain(Vector2 point) {
        return point.x >= transform.position.x &&
                point.x <= transform.position.x + transform.scale.x &&
                point.y >= transform.position.y &&
                point.y <= transform.position.y + itemHeight;
    }

    private boolean isMouseOverDropdown(Vector2 point) {
        int x = transform.position.xToInt();
        int y = transform.position.yToInt();
        int width = transform.scale.xToInt();
        int visibleItems = Math.min(options.size(), maxVisibleItems);
        int totalHeight = itemHeight * (1 + visibleItems);

        return point.x >= x && point.x <= x + width &&
                point.y >= y && point.y <= y + totalHeight;
    }

    private int getHoveredItemIndex(Vector2 point) {
        int x = transform.position.xToInt();
        int y = transform.position.yToInt() + itemHeight;
        int width = transform.scale.xToInt();

        for (int i = 0; i < options.size(); i++) {
            int itemY = y + i * itemHeight;

            if (point.x >= x && point.x <= x + width &&
                    point.y >= itemY && point.y <= itemY + itemHeight) {
                return i;
            }
        }

        return -1;
    }
    //</editor-fold>

    //<editor-fold desc="HELPER METHODS">
    private void selectItem(int index) {
        if (index < 0 || index >= options.size()) return;
        if (index == selectedIndex) return;

        selectedIndex = index;

        if (onIndexChanged != null) onIndexChanged.call(index);
        if (onSelectionChanged != null) onSelectionChanged.call(options.get(index));
        if (onItemSelected != null) onItemSelected.call(index, options.get(index));
    }

    /**
     * Sets the currently selected option by index.
     *
     * @param index The index of the option to select
     */
    public void setSelectedIndex(int index) { selectItem(index); }

    /**
     * Returns the index of the currently selected option.
     *
     * @return The selected index
     */
    public int getSelectedIndex() { return selectedIndex; }

    /**
     * Returns the text of the currently selected option.
     *
     * @return The selected option text, or empty string if no valid selection
     */
    public String getSelectedItem() {
        if (selectedIndex >= 0 && selectedIndex < options.size()) {
            return options.get(selectedIndex);
        }
        return "";
    }

    /**
     * Replaces all options with new ones.
     *
     * @param newOptions The new options to set
     */
    public void setOptions(String... newOptions) {
        options = new ArrayList<>(Arrays.asList(newOptions));
        selectedIndex = Math.min(selectedIndex, options.size() - 1);
    }

    /**
     * Adds a new option to the dropdown.
     *
     * @param option The option text to add
     */
    public void addOption(String option) { options.add(option); }

    /**
     * Removes an option at the specified index.
     *
     * @param index The index of the option to remove
     */
    public void removeOption(int index) {
        if (index >= 0 && index < options.size()) {
            options.remove(index);
            selectedIndex = Math.min(selectedIndex, options.size() - 1);
        }
    }

    /**
     * Removes all options from the dropdown.
     */
    public void clearOptions() { options.clear(); selectedIndex = 0; }

    /**
     * Returns the corner radius for rounded corners.
     *
     * @return The corner radius in pixels
     */
    public int getCornerRadius() { return cornerRadius; }

    /**
     * Sets the corner radius for rounded corners.
     *
     * @param radius The corner radius in pixels (minimum 0)
     */
    public void setCornerRadius(int radius) { this.cornerRadius = Math.max(0, radius); }

    /**
     * Checks if the dropdown is currently expanded.
     *
     * @return true if expanded, false otherwise
     */
    public boolean isExpanded() { return expanded; }

    /**
     * Sets the expanded state of the dropdown.
     *
     * @param expanded true to expand, false to collapse
     */
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
        if (expanded) {
            expandedDropdown = this;
        } else if (expandedDropdown == this) {
            expandedDropdown = null;
        }
    }
    //</editor-fold>

    //<editor-fold desc="DRAW">
    @Override
    public void draw(Graphics2D g) {
        if (!active) return;

        int x = transform.position.xToInt();
        int y = transform.position.yToInt();
        int width = transform.scale.xToInt();

        drawMainBox(g, x, y, width);

        if (expanded) {
            drawDropdownList(g, x, y + itemHeight, width);
        }
    }

    private void drawMainBox(Graphics2D g, int x, int y, int width) {
        if (cornerRadius > 0) {
            drawRoundedBox(g, x, y, width, itemHeight, backgroundColor, borderColor);
        } else {
            g.setColor(backgroundColor);
            g.fillRect(x, y, width, itemHeight);
            g.setColor(borderColor);
            g.drawRect(x, y, width, itemHeight);
        }

        g.setFont(font);
        g.setColor(textColor);
        String text = selectedIndex >= 0 && selectedIndex < options.size()
                ? options.get(selectedIndex) : "";

        FontMetrics fm = g.getFontMetrics();
        int textY = y + (itemHeight + fm.getAscent()) / 2 - 2;
        g.drawString(text, x + padding, textY);

        int arrowX = x + width - 20;
        int arrowY = y + itemHeight / 2;
        drawArrow(g, arrowX, arrowY, expanded);
    }

    private void drawDropdownList(Graphics2D g, int x, int y, int width) {
        int visibleItems = Math.min(options.size(), maxVisibleItems);

        for (int i = 0; i < options.size(); i++) {
            if (i >= visibleItems) break;

            int itemY = y + i * itemHeight;
            Color itemBg;

            if (i == hoveredIndex) {
                itemBg = hoverColor;
            } else if (i == selectedIndex) {
                itemBg = selectedColor;
            } else {
                itemBg = backgroundColor;
            }

            // Bestimme ob erste oder letzte Item für abgerundete Ecken
            boolean isFirst = (i == 0);
            boolean isLast = (i == Math.min(options.size() - 1, visibleItems - 1));

            if (cornerRadius > 0 && (isFirst || isLast)) {
                drawRoundedDropdownItem(g, x, itemY, width, itemHeight, itemBg, borderColor, isFirst, isLast);
            } else {
                g.setColor(itemBg);
                g.fillRect(x, itemY, width, itemHeight);
                g.setColor(borderColor);
                g.drawRect(x, itemY, width, itemHeight);
            }

            g.setFont(font);
            g.setColor(textColor);
            FontMetrics fm = g.getFontMetrics();
            int textY = itemY + (itemHeight + fm.getAscent()) / 2 - 2;
            g.drawString(options.get(i), x + padding, textY);
        }
    }

    private void drawRoundedBox(Graphics2D g, int x, int y, int width, int height, Color fillColor, Color borderColor) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        RoundRectangle2D roundRect = new RoundRectangle2D.Float(
                x, y, width, height, cornerRadius * 2, cornerRadius * 2
        );

        g.setColor(fillColor);
        g.fill(roundRect);
        g.setColor(borderColor);
        g.draw(roundRect);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    }

    private void drawRoundedDropdownItem(Graphics2D g, int x, int y, int width, int height,
                                         Color fillColor, Color borderColor, boolean isFirst, boolean isLast) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (isFirst && !isLast) {
            // Nur oben abgerundet
            g.setColor(fillColor);
            g.fillRect(x, y + cornerRadius, width, height - cornerRadius);
            RoundRectangle2D roundedTop = new RoundRectangle2D.Float(
                    x, y, width, cornerRadius * 2, cornerRadius * 2, cornerRadius * 2
            );
            g.fill(roundedTop);

            g.setColor(borderColor);

            g.drawLine(x, y + cornerRadius, x, y + height);
            g.drawLine(x + width, y + cornerRadius, x + width, y + height);
            g.drawLine(x, y + height, x + width, y + height);

            g.drawLine(x, y + cornerRadius, x, y + height);
            g.drawLine(x + width, y + cornerRadius, x + width, y + height);
            g.drawLine(x, y + height, x + width, y + height);
            g.drawArc(x, y, cornerRadius * 2, cornerRadius * 2, 90, 90); // links oben
            g.drawArc(x + width - cornerRadius * 2, y, cornerRadius * 2, cornerRadius * 2, 0, 90); // rechts oben
            g.drawLine(x + cornerRadius, y, x + width - cornerRadius, y);

        } else if (isLast && !isFirst) {


            // Nur unten abgerundet
            g.setColor(fillColor);
            g.fillRect(x, y, width, height - cornerRadius);
            RoundRectangle2D roundedBottom = new RoundRectangle2D.Float(
                    x, y + height - cornerRadius * 2, width, cornerRadius * 2, cornerRadius * 2, cornerRadius * 2
            );
            g.fill(roundedBottom);

            g.setColor(borderColor);
            g.drawLine(x, y, x, y + height - cornerRadius);
            g.drawLine(x + width, y, x + width, y + height - cornerRadius);

            g.drawLine(x, y, x, y + height - cornerRadius);
            g.drawLine(x + width, y, x + width, y + height - cornerRadius);
            g.drawLine(x, y, x + width, y);
            g.drawArc(x, y + height - cornerRadius * 2, cornerRadius * 2, cornerRadius * 2, 180, 90); // links unten
            g.drawArc(x + width - cornerRadius * 2, y + height - cornerRadius * 2,
                    cornerRadius * 2, cornerRadius * 2, 270, 90); // rechts unten
            g.drawLine(x + cornerRadius, y + height, x + width - cornerRadius, y + height);

        } else if (isFirst && isLast) {
            // Komplett abgerundet
            drawRoundedBox(g, x, y, width, height, fillColor, borderColor);
        }

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    }

    private void drawArrow(Graphics2D g, int x, int y, boolean up) {
        g.setColor(textColor);
        int size = 5;

        if (up) {
            int[] xPoints = {x, x + size, x + size * 2};
            int[] yPoints = {y + size, y, y + size};
            g.fillPolygon(xPoints, yPoints, 3);
        } else {
            int[] xPoints = {x, x + size, x + size * 2};
            int[] yPoints = {y, y + size, y};
            g.fillPolygon(xPoints, yPoints, 3);
        }
    }

    @Override
    public void onCollision(GameObject collider) {}
    //</editor-fold>
}