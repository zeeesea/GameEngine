package GameEngine.Core.input;

import GameEngine.Core.util.Vector2;

public class Input {

    static boolean[] currentKeys = new boolean[KeyCode.values().length];
    static boolean[] lastKeys    = new boolean[KeyCode.values().length];

    public enum KeyCode {
        // Movement
        W(0), A(1), S(2), D(3),
        UP(4), DOWN(5), LEFT(6), RIGHT(7),

        // Numbers (Top row)
        DIGIT_0(8), DIGIT_1(9), DIGIT_2(10), DIGIT_3(11), DIGIT_4(12),
        DIGIT_5(13), DIGIT_6(14), DIGIT_7(15), DIGIT_8(16), DIGIT_9(17),

        // Function Keys
        F1(18), F2(19), F3(20), F4(21), F5(22), F6(23),
        F7(24), F8(25), F9(26), F10(27), F11(28), F12(29),

        // Letters
        Q(30), E(31), R(32), T(33), Y(34), U(35), I(36), O(37), P(38),
        F(39), G(40), H(41), J(42), K(43), L(44),
        Z(45), X(46), C(47), V(48), B(49), N(50), M(51),

        // Modifiers
        SHIFT(52),
        CONTROL(53),
        ALT(54),
        TAB(55),
        CAPS_LOCK(56),

        // Special / System
        ESCAPE(57),
        ENTER(58),
        BACKSPACE(59),
        DELETE(60),
        INSERT(61),
        HOME(62),
        END(63),
        PAGE_UP(64),
        PAGE_DOWN(65),

        // Symbols
        SPACE(66),
        COMMA(67),
        PERIOD(68),
        SLASH(69),
        SEMICOLON(70),
        QUOTE(71),
        BACKSLASH(72),
        MINUS(73),
        EQUALS(74),
        OPEN_BRACKET(75),
        CLOSE_BRACKET(76),

        // Numpad
        NUMPAD_0(77),
        NUMPAD_1(78),
        NUMPAD_2(79),
        NUMPAD_3(80),
        NUMPAD_4(81),
        NUMPAD_5(82),
        NUMPAD_6(83),
        NUMPAD_7(84),
        NUMPAD_8(85),
        NUMPAD_9(86),
        NUMPAD_ADD(87),
        NUMPAD_SUBTRACT(88),
        NUMPAD_MULTIPLY(89),
        NUMPAD_DIVIDE(90),
        NUMPAD_ENTER(91),
        NUMPAD_DECIMAL(92),

        // Other
        PRINT_SCREEN(93),
        SCROLL_LOCK(94),
        PAUSE(95);

        private final int index;

        KeyCode(int idx) {
            this.index = idx;
        }

        public int getIndex() {
            return index;
        }
    }


    private static int mouseX, mouseY;
    static boolean[] currentMouse = new boolean[MouseCode.values().length];
    static boolean[] lastMouse    = new boolean[MouseCode.values().length];
    public enum MouseCode {
        LEFT(0),
        RIGHT(1),
        MIDDLE(2);

        private final int index;

        MouseCode(int idx) {
            this.index = idx;
        }
    }

    // Smooth Input State
    private static float horizontalSmooth = 0f;
    private static float verticalSmooth = 0f;
    private static final float INPUT_SMOOTHING = 10f;  // Acceleration speed
    private static final float INPUT_GRAVITY = 15f;    // Deceleration speed

    public static void update() {
        System.arraycopy(currentKeys, 0, lastKeys, 0, currentKeys.length);
        System.arraycopy(currentMouse, 0, lastMouse, 0, currentMouse.length);
    }

    public static boolean getKey(KeyCode c) {
        return currentKeys[c.index];
    }
    public static boolean getKeyDown(KeyCode c) {
        int i = c.getIndex();
        return currentKeys[i] && !lastKeys[i];
    }
    public static boolean getKeyUp(KeyCode c) {
        int i = c.getIndex();
        return !currentKeys[i] && lastKeys[i];
    }

    public static void setKey(KeyCode c, boolean b) {
        currentKeys[c.index] = b;
    }


    public static Vector2 getMousePosition() {
        return new Vector2(mouseX, mouseY);
    }
    public static boolean getMouseButton(MouseCode c) {
        if (c == MouseCode.LEFT) {
            return currentMouse[MouseCode.LEFT.index];
        } else if (c == MouseCode.RIGHT) {
            return currentMouse[MouseCode.RIGHT.index];
        } else if (c == MouseCode.MIDDLE) {
            return currentMouse[MouseCode.MIDDLE.index];
        }
        return false;
    }
    public static boolean getMouseButtonDown(MouseCode c) {
        int i = c.index;
        return currentMouse[i] && !lastMouse[i];
    }

    public static boolean getMouseButtonUp(MouseCode c) {
        int i = c.index;
        return !currentMouse[i] && lastMouse[i];
    }
    public static void setMousePosition(int x, int y) {
        mouseX = x;
        mouseY = y;
    }
    public static void setMouseButton(MouseCode c, boolean pressed) {
        if (c == MouseCode.LEFT) currentMouse[MouseCode.LEFT.index] = pressed;
        else if (c == MouseCode.RIGHT) currentMouse[MouseCode.RIGHT.index] = pressed;
        else if (c == MouseCode.MIDDLE) currentMouse[MouseCode.MIDDLE.index] = pressed;
    }

    // === AXIS INPUT ===

    /**
     * Raw Input (-1, 0, oder 1) - INSTANT, kein Smoothing
     */
    public static float getAxisRaw(String axisName) {
        switch (axisName.toLowerCase()) {
            case "horizontal":
                int h = 0;
                if (getKey(KeyCode.D) || getKey(KeyCode.RIGHT)) h += 1;
                if (getKey(KeyCode.A) || getKey(KeyCode.LEFT)) h -= 1;
                return h;

            case "vertical":
                int v = 0;
                if (getKey(KeyCode.W) || getKey(KeyCode.UP)) v -= 1;
                if (getKey(KeyCode.S) || getKey(KeyCode.DOWN)) v += 1;
                return v;

            default:
                return 0;
        }
    }

    /**
     * Smooth Input (0 bis 1 oder -1) - mit Acceleration/Deceleration
     * Wie Unity's Input.GetAxis()
     */
    public static float getAxis(String axisName) {
        switch (axisName.toLowerCase()) {
            case "horizontal":
                return horizontalSmooth;
            case "vertical":
                return verticalSmooth;
            default:
                return 0;
        }
    }

    /**
     * Raw Input als Vector2 - INSTANT
     */
    public static Vector2 getInputRaw() {
        return new Vector2(
                getAxisRaw("Horizontal"),
                getAxisRaw("Vertical")
        );
    }

    /**
     * Smooth Input als Vector2 - mit Smoothing
     */
    public static Vector2 getInput() {
        return new Vector2(
                getAxis("Horizontal"),
                getAxis("Vertical")
        );
    }

    /**
     * Update smooth input values - MUSS jeden Frame aufgerufen werden!
     */
    public static void updateSmoothInput(float deltaTime) {
        // Horizontal
        float targetH = getAxisRaw("Horizontal");
        if (targetH != 0) {
            // Accelerate towards target
            horizontalSmooth = lerp(horizontalSmooth, targetH, INPUT_SMOOTHING * deltaTime);
        } else {
            // Decelerate to 0
            horizontalSmooth = lerp(horizontalSmooth, 0, INPUT_GRAVITY * deltaTime);
            if (Math.abs(horizontalSmooth) < 0.01f) horizontalSmooth = 0;
        }

        // Vertical
        float targetV = getAxisRaw("Vertical");
        if (targetV != 0) {
            verticalSmooth = lerp(verticalSmooth, targetV, INPUT_SMOOTHING * deltaTime);
        } else {
            verticalSmooth = lerp(verticalSmooth, 0, INPUT_GRAVITY * deltaTime);
            if (Math.abs(verticalSmooth) < 0.01f) verticalSmooth = 0;
        }
    }

    private static float lerp(float start, float end, float t) {
        t = Math.max(0, Math.min(1, t));
        return start + (end - start) * t;
    }
}
