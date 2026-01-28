package GameEngine.Core.input;

import GameEngine.Core.GameEngine;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class InputKeyListener extends KeyAdapter {

    private final GameEngine engine;

    // Mapping zwischen AWT KeyEvent → deinem eigenen KeyCode-Enum
    private static final Map<Integer, Input.KeyCode> keyMap = new HashMap<>();

    static {
        // --- Movement ---
        keyMap.put(KeyEvent.VK_W, Input.KeyCode.W);
        keyMap.put(KeyEvent.VK_A, Input.KeyCode.A);
        keyMap.put(KeyEvent.VK_S, Input.KeyCode.S);
        keyMap.put(KeyEvent.VK_D, Input.KeyCode.D);
        keyMap.put(KeyEvent.VK_UP, Input.KeyCode.UP);
        keyMap.put(KeyEvent.VK_DOWN, Input.KeyCode.DOWN);
        keyMap.put(KeyEvent.VK_LEFT, Input.KeyCode.LEFT);
        keyMap.put(KeyEvent.VK_RIGHT, Input.KeyCode.RIGHT);

        // --- Space + Modifiers ---
        keyMap.put(KeyEvent.VK_SPACE, Input.KeyCode.SPACE);
        keyMap.put(KeyEvent.VK_SHIFT, Input.KeyCode.SHIFT);
        keyMap.put(KeyEvent.VK_CONTROL, Input.KeyCode.CONTROL);
        keyMap.put(KeyEvent.VK_ALT, Input.KeyCode.ALT);
        keyMap.put(KeyEvent.VK_TAB, Input.KeyCode.TAB);
        keyMap.put(KeyEvent.VK_CAPS_LOCK, Input.KeyCode.CAPS_LOCK);

        // --- System keys ---
        keyMap.put(KeyEvent.VK_ESCAPE, Input.KeyCode.ESCAPE);
        keyMap.put(KeyEvent.VK_ENTER, Input.KeyCode.ENTER);
        keyMap.put(KeyEvent.VK_BACK_SPACE, Input.KeyCode.BACKSPACE);
        keyMap.put(KeyEvent.VK_DELETE, Input.KeyCode.DELETE);
        keyMap.put(KeyEvent.VK_INSERT, Input.KeyCode.INSERT);
        keyMap.put(KeyEvent.VK_HOME, Input.KeyCode.HOME);
        keyMap.put(KeyEvent.VK_END, Input.KeyCode.END);
        keyMap.put(KeyEvent.VK_PAGE_UP, Input.KeyCode.PAGE_UP);
        keyMap.put(KeyEvent.VK_PAGE_DOWN, Input.KeyCode.PAGE_DOWN);

        // --- Numbers (top row) ---
        keyMap.put(KeyEvent.VK_0, Input.KeyCode.DIGIT_0);
        keyMap.put(KeyEvent.VK_1, Input.KeyCode.DIGIT_1);
        keyMap.put(KeyEvent.VK_2, Input.KeyCode.DIGIT_2);
        keyMap.put(KeyEvent.VK_3, Input.KeyCode.DIGIT_3);
        keyMap.put(KeyEvent.VK_4, Input.KeyCode.DIGIT_4);
        keyMap.put(KeyEvent.VK_5, Input.KeyCode.DIGIT_5);
        keyMap.put(KeyEvent.VK_6, Input.KeyCode.DIGIT_6);
        keyMap.put(KeyEvent.VK_7, Input.KeyCode.DIGIT_7);
        keyMap.put(KeyEvent.VK_8, Input.KeyCode.DIGIT_8);
        keyMap.put(KeyEvent.VK_9, Input.KeyCode.DIGIT_9);

        // --- Letters ---
        for (char c = 'A'; c <= 'Z'; c++) {
            keyMap.put(KeyEvent.getExtendedKeyCodeForChar(c), Input.KeyCode.valueOf(String.valueOf(c)));
        }

        // --- Function keys ---
        keyMap.put(KeyEvent.VK_F1, Input.KeyCode.F1);
        keyMap.put(KeyEvent.VK_F2, Input.KeyCode.F2);
        keyMap.put(KeyEvent.VK_F3, Input.KeyCode.F3);
        keyMap.put(KeyEvent.VK_F4, Input.KeyCode.F4);
        keyMap.put(KeyEvent.VK_F5, Input.KeyCode.F5);
        keyMap.put(KeyEvent.VK_F6, Input.KeyCode.F6);
        keyMap.put(KeyEvent.VK_F7, Input.KeyCode.F7);
        keyMap.put(KeyEvent.VK_F8, Input.KeyCode.F8);
        keyMap.put(KeyEvent.VK_F9, Input.KeyCode.F9);
        keyMap.put(KeyEvent.VK_F10, Input.KeyCode.F10);
        keyMap.put(KeyEvent.VK_F11, Input.KeyCode.F11);
        keyMap.put(KeyEvent.VK_F12, Input.KeyCode.F12);

        // --- Numpad ---
        keyMap.put(KeyEvent.VK_NUMPAD0, Input.KeyCode.NUMPAD_0);
        keyMap.put(KeyEvent.VK_NUMPAD1, Input.KeyCode.NUMPAD_1);
        keyMap.put(KeyEvent.VK_NUMPAD2, Input.KeyCode.NUMPAD_2);
        keyMap.put(KeyEvent.VK_NUMPAD3, Input.KeyCode.NUMPAD_3);
        keyMap.put(KeyEvent.VK_NUMPAD4, Input.KeyCode.NUMPAD_4);
        keyMap.put(KeyEvent.VK_NUMPAD5, Input.KeyCode.NUMPAD_5);
        keyMap.put(KeyEvent.VK_NUMPAD6, Input.KeyCode.NUMPAD_6);
        keyMap.put(KeyEvent.VK_NUMPAD7, Input.KeyCode.NUMPAD_7);
        keyMap.put(KeyEvent.VK_NUMPAD8, Input.KeyCode.NUMPAD_8);
        keyMap.put(KeyEvent.VK_NUMPAD9, Input.KeyCode.NUMPAD_9);
        keyMap.put(KeyEvent.VK_ADD, Input.KeyCode.NUMPAD_ADD);
        keyMap.put(KeyEvent.VK_SUBTRACT, Input.KeyCode.NUMPAD_SUBTRACT);
        keyMap.put(KeyEvent.VK_MULTIPLY, Input.KeyCode.NUMPAD_MULTIPLY);
        keyMap.put(KeyEvent.VK_DIVIDE, Input.KeyCode.NUMPAD_DIVIDE);
        keyMap.put(KeyEvent.VK_DECIMAL, Input.KeyCode.NUMPAD_DECIMAL);

        // --- Symbols ---
        keyMap.put(KeyEvent.VK_COMMA, Input.KeyCode.COMMA);
        keyMap.put(KeyEvent.VK_PERIOD, Input.KeyCode.PERIOD);
        keyMap.put(KeyEvent.VK_SLASH, Input.KeyCode.SLASH);
        keyMap.put(KeyEvent.VK_SEMICOLON, Input.KeyCode.SEMICOLON);
        keyMap.put(KeyEvent.VK_QUOTE, Input.KeyCode.QUOTE);
        keyMap.put(KeyEvent.VK_BACK_SLASH, Input.KeyCode.BACKSLASH);
        keyMap.put(KeyEvent.VK_MINUS, Input.KeyCode.MINUS);
        keyMap.put(KeyEvent.VK_EQUALS, Input.KeyCode.EQUALS);
        keyMap.put(KeyEvent.VK_OPEN_BRACKET, Input.KeyCode.OPEN_BRACKET);
        keyMap.put(KeyEvent.VK_CLOSE_BRACKET, Input.KeyCode.CLOSE_BRACKET);
    }

    public InputKeyListener(GameEngine engine) {
        this.engine = engine;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        Input.KeyCode code = keyMap.get(e.getKeyCode());
        if (code != null) Input.setKey(code, true);

        engine.onKeyPressed(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        Input.KeyCode code = keyMap.get(e.getKeyCode());
        if (code != null) Input.setKey(code, false);

        engine.onKeyReleased(e.getKeyCode());
    }
}
