package GameEngine.Core.input;

import GameEngine.Core.GameEngine;

import java.awt.event.*;

public class WindowEventListener extends WindowAdapter {
    private final GameEngine engine;

    public WindowEventListener(GameEngine engine) {
        this.engine = engine;
    }

    @Override
    public void windowClosing(WindowEvent e) {
        engine.onWindowClosing();
    }

    @Override
    public void windowIconified(WindowEvent e) {
        engine.onWindowMinimized();
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        engine.onWindowRestored();
    }
}