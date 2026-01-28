package GameEngine.Core.input;
import GameEngine.Core.GameEngine;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class InputMouseListener extends MouseAdapter {
    private final GameEngine engine;

    public InputMouseListener(GameEngine engine) {
        this.engine = engine;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            Input.setMouseButton(Input.MouseCode.LEFT, true);
        }
        if (e.getButton() == MouseEvent.BUTTON3) {
            Input.setMouseButton(Input.MouseCode.RIGHT, true);
        }
        if (e.getButton() == MouseEvent.BUTTON2) {
            Input.setMouseButton(Input.MouseCode.MIDDLE, true);
        }
        engine.onMousePressed(e.getX(), e.getY(), e.getButton());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            Input.setMouseButton(Input.MouseCode.LEFT, false);
        }
        if (e.getButton() == MouseEvent.BUTTON3) {
            Input.setMouseButton(Input.MouseCode.RIGHT, false);
        }
        if (e.getButton() == MouseEvent.BUTTON2) {
            Input.setMouseButton(Input.MouseCode.MIDDLE, false);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        Input.setMousePosition(e.getX(), e.getY());
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Input.setMousePosition(e.getX(), e.getY());
    }
}