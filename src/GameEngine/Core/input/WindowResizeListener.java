package GameEngine.Core.input;

import GameEngine.Core.GameEngine;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

// Für Resize
public class WindowResizeListener extends ComponentAdapter {
    private final GameEngine engine;

    public WindowResizeListener(GameEngine engine) {
        this.engine = engine;
    }

    @Override
    public void componentResized(ComponentEvent e) {
        engine.callWindowResized(e.getComponent().getWidth(), e.getComponent().getHeight());
    }
}
