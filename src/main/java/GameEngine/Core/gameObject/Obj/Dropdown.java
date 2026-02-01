package GameEngine.Core.gameObject.Obj;

import GameEngine.Core.gameObject.FuncInt.*;
import GameEngine.Core.gameObject.GameObject;
import GameEngine.Core.gameObject.Transform;
import GameEngine.Core.input.Input;
import GameEngine.Core.util.Vector2;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Dropdown extends GameObject {
    //<editor-fold desc="VARIABLES">
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

    private int hoveredIndex = -1;
    private boolean lastMouseState = false;

    private FuncIntOne<Integer> onIndexChanged;
    private FuncIntOne<String> onSelectionChanged;
    private FuncIntTwo<Integer, String> onItemSelected;
    //</editor-fold>

    //<editor-fold desc="CONSTRUCTOR/BUILDER">
    private Dropdown(Rectangle rect) {
        transform = new Transform(rect);
        renderOrder = 101;
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
            hoveredIndex = -1;
        }
    }

    private void updateExpanded(Vector2 mousePos, boolean mousePressed) {
        boolean clickedOutside = mousePressed && !lastMouseState && !isMouseOverDropdown(mousePos);

        if (clickedOutside) {
            expanded = false;
            hoveredIndex = -1;
            return;
        }

        hoveredIndex = getHoveredItemIndex(mousePos);

        if (hoveredIndex >= 0 && mousePressed && !lastMouseState) {
            selectItem(hoveredIndex);
            expanded = false;
        }
    }

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

    public void setSelectedIndex(int index) { selectItem(index); }
    public int getSelectedIndex() { return selectedIndex; }

    public String getSelectedItem() {
        if (selectedIndex >= 0 && selectedIndex < options.size()) {
            return options.get(selectedIndex);
        }
        return "";
    }

    public void setOptions(String... newOptions) {
        options = new ArrayList<>(Arrays.asList(newOptions));
        selectedIndex = Math.min(selectedIndex, options.size() - 1);
    }

    public void addOption(String option) { options.add(option); }
    public void removeOption(int index) {
        if (index >= 0 && index < options.size()) {
            options.remove(index);
            selectedIndex = Math.min(selectedIndex, options.size() - 1);
        }
    }
    public void clearOptions() { options.clear(); selectedIndex = 0; }
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
        g.setColor(backgroundColor);
        g.fillRect(x, y, width, itemHeight);

        g.setColor(borderColor);
        g.drawRect(x, y, width, itemHeight);

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

            if (i == hoveredIndex) {
                g.setColor(hoverColor);
            } else if (i == selectedIndex) {
                g.setColor(selectedColor);
            } else {
                g.setColor(backgroundColor);
            }
            g.fillRect(x, itemY, width, itemHeight);

            g.setColor(borderColor);
            g.drawRect(x, itemY, width, itemHeight);

            g.setFont(font);
            g.setColor(textColor);
            FontMetrics fm = g.getFontMetrics();
            int textY = itemY + (itemHeight + fm.getAscent()) / 2 - 2;
            g.drawString(options.get(i), x + padding, textY);
        }
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