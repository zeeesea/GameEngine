package GameEngine.Tools.UIGenerator;

import GameEngine.Core.gameObject.GameObject;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Background renderer for the UI Generator.
 * Draws the sidebar, canvas, and grid.
 */
public class UIGeneratorBackground extends GameObject {

    private final int SIDEBAR_WIDTH;
    private final int TOP_BAR_HEIGHT;
    private final int BOTTOM_BAR_HEIGHT;
    private final int CORNER_RADIUS;

    private final Color SIDEBAR_COLOR;
    private final Color CANVAS_BG;
    private final Color BORDER_COLOR;

    private Rectangle canvasBounds;

    public UIGeneratorBackground(int sidebarWidth, int topBarHeight, int bottomBarHeight,
                                  int cornerRadius, Color sidebarColor, Color canvasBg,
                                  Color borderColor, Rectangle canvasBounds) {
        this.SIDEBAR_WIDTH = sidebarWidth;
        this.TOP_BAR_HEIGHT = topBarHeight;
        this.BOTTOM_BAR_HEIGHT = bottomBarHeight;
        this.CORNER_RADIUS = cornerRadius;
        this.SIDEBAR_COLOR = sidebarColor;
        this.CANVAS_BG = canvasBg;
        this.BORDER_COLOR = borderColor;
        this.canvasBounds = canvasBounds;

        this.renderOrder = -100; // Draw first (background)
    }

    @Override
    public void init() {}

    @Override
    public void update(double deltaTime) {}

    @Override
    public void draw(Graphics2D g) {
        if (canvasBounds == null) return;

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw Sidebar Background
        g.setColor(SIDEBAR_COLOR);
        g.fillRect(0, TOP_BAR_HEIGHT, SIDEBAR_WIDTH, getScreenHeight() - TOP_BAR_HEIGHT);

        // Draw Sidebar Border
        g.setColor(BORDER_COLOR);
        g.drawLine(SIDEBAR_WIDTH, TOP_BAR_HEIGHT, SIDEBAR_WIDTH, getScreenHeight());

        // Draw Top Bar Border
        g.setColor(BORDER_COLOR);
        g.drawLine(0, TOP_BAR_HEIGHT, getScreenWidth(), TOP_BAR_HEIGHT);

        // Draw Bottom Bar Border
        g.setColor(BORDER_COLOR);
        g.drawLine(0, getScreenHeight() - BOTTOM_BAR_HEIGHT, getScreenWidth(), getScreenHeight() - BOTTOM_BAR_HEIGHT);

        // Draw Canvas Background
        g.setColor(CANVAS_BG);
        RoundRectangle2D canvasRect = new RoundRectangle2D.Float(
            canvasBounds.x, canvasBounds.y, canvasBounds.width, canvasBounds.height,
            CORNER_RADIUS * 2, CORNER_RADIUS * 2
        );
        g.fill(canvasRect);

        // Draw Canvas Border
        g.setColor(BORDER_COLOR);
        g.setStroke(new BasicStroke(2));
        g.draw(canvasRect);

        // Draw grid on canvas
        drawCanvasGrid(g);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    }

    private void drawCanvasGrid(Graphics2D g) {
        g.setColor(new Color(60, 60, 65, 100));
        g.setStroke(new BasicStroke(1));

        int gridSize = 20;

        for (int x = canvasBounds.x; x <= canvasBounds.x + canvasBounds.width; x += gridSize) {
            g.drawLine(x, canvasBounds.y, x, canvasBounds.y + canvasBounds.height);
        }

        for (int y = canvasBounds.y; y <= canvasBounds.y + canvasBounds.height; y += gridSize) {
            g.drawLine(canvasBounds.x, y, canvasBounds.x + canvasBounds.width, y);
        }
    }

    @Override
    public void onCollision(GameObject collider) {}
}
