package GameEngine.Core.gameObject.Obj;

import GameEngine.Core.gameObject.FuncInt.*;
import GameEngine.Core.gameObject.GameObject;
import GameEngine.Core.gameObject.Transform;
import GameEngine.Core.input.Input;
import GameEngine.Core.util.Vector2;

import java.awt.*;
import java.awt.event.KeyEvent;

public class TextField extends GameObject {
    //<editor-fold desc="VARIABLES">
    private String content = "";
    private String placeholder = "";
    private boolean focused = false;
    private int maxLength = 50;

    // Visual
    private Color backgroundColor = new Color(40, 40, 40);
    private Color focusedColor = new Color(60, 60, 60);
    private Color borderColor = Color.GRAY;
    private Color focusedBorderColor = Color.CYAN;
    private Color textColor = Color.WHITE;
    private Color placeholderColor = Color.GRAY;
    private Font font = new Font("Arial", Font.PLAIN, 16);
    private int padding = 10;
    private int textOffsetX = 0;


    // Cursor
    private boolean showCursor = false;
    private float cursorTimer = 0;
    private float cursorBlinkRate = 0.5f;
    private int cursorPosition = 0;


    // Events
    private FuncIntOne<String> onTextChanged;
    private FuncIntOne<String> onSubmit;
    private FuncInt onFocus;
    private FuncInt onUnfocus;

    // Input Filter
    private InputFilter inputFilter = InputFilter.ALL;

    public enum InputFilter {
        ALL, NUMBERS_ONLY, LETTERS_ONLY, ALPHANUMERIC, EMAIL, PASSWORD
    }
    //</editor-fold>

    //<editor-fold desc="CONSTRUCTOR/BUILDER">
    private TextField(Rectangle rect) {
        transform = new Transform(rect);
        renderOrder = 100; // UI should render on top
    }

    public static class Builder {
        private Rectangle rect = new Rectangle(0, 0, 200, 40);
        private String placeholder = "Enter text...";
        private String initialText = "";
        private int maxLength = 50;

        private Color backgroundColor = new Color(40, 40, 40);
        private Color focusedColor = new Color(60, 60, 60);
        private Color borderColor = Color.GRAY;
        private Color focusedBorderColor = Color.CYAN;
        private Color textColor = Color.WHITE;
        private Color placeholderColor = Color.GRAY;
        private Font font = new Font("Arial", Font.PLAIN, 16);

        private InputFilter inputFilter = InputFilter.ALL;

        private FuncIntOne<String> onTextChanged;
        private FuncIntOne<String> onSubmit;
        private FuncInt onFocus;
        private FuncInt onUnfocus;

        public Builder() {}

        public Builder rect(Rectangle rect) {
            this.rect = rect;
            return this;
        }

        public Builder pos(Vector2 pos) {
            rect.x = pos.xToInt();
            rect.y = pos.yToInt();
            return this;
        }

        public Builder size(Vector2 size) {
            rect.width = size.xToInt();
            rect.height = size.yToInt();
            return this;
        }

        public Builder placeholder(String text) {
            this.placeholder = text;
            return this;
        }

        public Builder initialText(String text) {
            this.initialText = text;
            return this;
        }

        public Builder maxLength(int length) {
            this.maxLength = length;
            return this;
        }

        public Builder backgroundColor(Color color) {
            this.backgroundColor = color;
            return this;
        }

        public Builder focusedColor(Color color) {
            this.focusedColor = color;
            return this;
        }

        public Builder borderColor(Color color) {
            this.borderColor = color;
            return this;
        }

        public Builder focusedBorderColor(Color color) {
            this.focusedBorderColor = color;
            return this;
        }

        public Builder textColor(Color color) {
            this.textColor = color;
            return this;
        }

        public Builder placeholderColor(Color color) {
            this.placeholderColor = color;
            return this;
        }

        public Builder font(Font font) {
            this.font = font;
            return this;
        }

        public Builder inputFilter(InputFilter filter) {
            this.inputFilter = filter;
            return this;
        }

        public Builder onTextChanged(FuncIntOne<String> callback) {
            this.onTextChanged = callback;
            return this;
        }

        public Builder onSubmit(FuncIntOne<String> callback) {
            this.onSubmit = callback;
            return this;
        }

        public Builder onFocus(FuncInt callback) {
            this.onFocus = callback;
            return this;
        }

        public Builder onUnfocus(FuncInt callback) {
            this.onUnfocus = callback;
            return this;
        }

        public TextField build() {
            TextField tf = new TextField(rect);
            tf.placeholder = placeholder;
            tf.content = initialText;
            tf.maxLength = maxLength;
            tf.backgroundColor = backgroundColor;
            tf.focusedColor = focusedColor;
            tf.borderColor = borderColor;
            tf.focusedBorderColor = focusedBorderColor;
            tf.textColor = textColor;
            tf.placeholderColor = placeholderColor;
            tf.font = font;
            tf.inputFilter = inputFilter;
            tf.onTextChanged = onTextChanged;
            tf.onSubmit = onSubmit;
            tf.onFocus = onFocus;
            tf.onUnfocus = onUnfocus;
            tf.cursorPosition = initialText.length();
            return tf;
        }
    }
    //</editor-fold>

    //<editor-fold desc="UPDATE/INPUT">
    @Override
    public void init() {}

    @Override
    public void update(double deltaTime) {
        if (!active) return;

        // Focus handling
        if (Input.getMouseButtonDown(Input.MouseCode.LEFT)) {
            Vector2 mousePos = Input.getMousePosition();
            boolean wasInside = isMouseInside(mousePos);

            if (wasInside && !focused) {
                setFocused(true);
            } else if (!wasInside && focused) {
                setFocused(false);
            }
        }

        // Cursor blink
        if (focused) {
            cursorTimer += (float) deltaTime;
            if (cursorTimer >= cursorBlinkRate) {
                showCursor = !showCursor;
                cursorTimer = 0;
            }
        }
    }

    private boolean isMouseInside(Vector2 point) {
        return point.x >= transform.position.x &&
                point.x <= transform.position.x + transform.scale.x &&
                point.y >= transform.position.y &&
                point.y <= transform.position.y + transform.scale.y;
    }

    @Override
    public void onKeyPressed(int keyCode) {
        if (!focused || !active) return;

        if (keyCode == KeyEvent.VK_ENTER) {
            if (onSubmit != null) onSubmit.call(content);
            setFocused(false);
            return;
        }

        if (keyCode == KeyEvent.VK_ESCAPE) {
            setFocused(false);
            return;
        }

        if (keyCode == KeyEvent.VK_BACK_SPACE) {
            if (content.length() > 0 && cursorPosition > 0) {
                content = content.substring(0, cursorPosition - 1) +
                        content.substring(cursorPosition);
                cursorPosition--;
                triggerTextChanged();
            }
            return;
        }

        if (keyCode == KeyEvent.VK_DELETE) {
            if (cursorPosition < content.length()) {
                content = content.substring(0, cursorPosition) +
                        content.substring(cursorPosition + 1);
                triggerTextChanged();
            }
            return;
        }

        if (keyCode == KeyEvent.VK_LEFT) {
            cursorPosition = Math.max(0, cursorPosition - 1);
            return;
        }

        if (keyCode == KeyEvent.VK_RIGHT) {
            cursorPosition = Math.min(content.length(), cursorPosition + 1);
            return;
        }

        if (keyCode == KeyEvent.VK_HOME) {
            cursorPosition = 0;
            return;
        }

        if (keyCode == KeyEvent.VK_END) {
            cursorPosition = content.length();
            return;
        }

        // Ignore modifier keys, function keys, and other non-printable keys entirely
        if (isNonCharacterKey(keyCode)) return;

        // Spacebar handled explicitly so it always works regardless of filter
        if (keyCode == KeyEvent.VK_SPACE) {
            if (content.length() < maxLength) {
                content = content.substring(0, cursorPosition) + ' ' +
                        content.substring(cursorPosition);
                cursorPosition++;
                triggerTextChanged();
            }
            return;
        }

        // Resolve the actual printable character (Shift-aware)
        char c = resolveCharacter(keyCode);
        if (c == 0) return; // not a printable key

        if (isValidChar(c) && content.length() < maxLength) {
            content = content.substring(0, cursorPosition) + c +
                    content.substring(cursorPosition);
            cursorPosition++;
            triggerTextChanged();
        }
    }
    //</editor-fold>

    //<editor-fold desc="HELPER METHODS">
    /**
     * Resolves the printable character for a keycode, taking Shift into account.
     * Returns '\0' if the key does not produce a printable character.
     * Uses a standard US keyboard layout for shifted symbols.
     */
    private char resolveCharacter(int keyCode) {
        boolean shift = Input.getKey(Input.KeyCode.SHIFT);

        // Letters A-Z
        if (keyCode >= KeyEvent.VK_A && keyCode <= KeyEvent.VK_Z) {
            char base = (char) keyCode; // VK_A == 'A', etc.
            return shift ? Character.toUpperCase(base) : Character.toLowerCase(base);
        }

        // Digits and their shifted symbols (US layout)
        switch (keyCode) {
            case KeyEvent.VK_0: return shift ? ')' : '0';
            case KeyEvent.VK_1: return shift ? '!' : '1';
            case KeyEvent.VK_2: return shift ? '@' : '2';
            case KeyEvent.VK_3: return shift ? '#' : '3';
            case KeyEvent.VK_4: return shift ? '$' : '4';
            case KeyEvent.VK_5: return shift ? '%' : '5';
            case KeyEvent.VK_6: return shift ? '^' : '6';
            case KeyEvent.VK_7: return shift ? '&' : '7';
            case KeyEvent.VK_8: return shift ? '*' : '8';
            case KeyEvent.VK_9: return shift ? '(' : '9';

            // Punctuation / symbols (US layout)
            case KeyEvent.VK_MINUS:       return shift ? '_' : '-';
            case KeyEvent.VK_EQUALS:      return shift ? '+' : '=';
            case KeyEvent.VK_OPEN_BRACKET:  return shift ? '{' : '[';
            case KeyEvent.VK_CLOSE_BRACKET: return shift ? '}' : ']';
            case KeyEvent.VK_BACK_SLASH:  return shift ? '|' : '\\';
            case KeyEvent.VK_SEMICOLON:   return shift ? ':' : ';';
            case KeyEvent.VK_QUOTE:       return shift ? '"' : '\'';
            case KeyEvent.VK_COMMA:       return shift ? '<' : ',';
            case KeyEvent.VK_PERIOD:      return shift ? '>' : '.';
            case KeyEvent.VK_SLASH:       return shift ? '?' : '/';
            case KeyEvent.VK_BACK_QUOTE:  return shift ? '~' : '`';
        }

        return '\0'; // not a printable key
    }

    private boolean isNonCharacterKey(int keyCode) {
        // Modifier keys
        if (keyCode == KeyEvent.VK_SHIFT || keyCode == KeyEvent.VK_CONTROL ||
                keyCode == KeyEvent.VK_ALT || keyCode == KeyEvent.VK_META) return true;
        // Function keys
        if (keyCode >= KeyEvent.VK_F1 && keyCode <= KeyEvent.VK_F12) return true;
        // Navigation / system keys
        if (keyCode == KeyEvent.VK_TAB || keyCode == KeyEvent.VK_CAPS_LOCK ||
                keyCode == KeyEvent.VK_NUM_LOCK || keyCode == KeyEvent.VK_SCROLL_LOCK ||
                keyCode == KeyEvent.VK_INSERT || keyCode == KeyEvent.VK_PAUSE ||
                keyCode == KeyEvent.VK_PAGE_UP || keyCode == KeyEvent.VK_PAGE_DOWN ||
                keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_DOWN) return true;
        return false;
    }

    private boolean isValidChar(char c) {
        switch (inputFilter) {
            case NUMBERS_ONLY:
                return Character.isDigit(c) || c == '.' || c == '-';
            case LETTERS_ONLY:
                return Character.isLetter(c) || c == ' ';
            case ALPHANUMERIC:
                return Character.isLetterOrDigit(c) || c == ' ';
            case EMAIL:
                return Character.isLetterOrDigit(c) ||
                        c == '@' || c == '.' || c == '_' || c == '-';
            case PASSWORD:
            case ALL:
            default:
                return !Character.isISOControl(c);
        }
    }

    private void updateTextOffset(FontMetrics fm) {
        String displayText = content;
        if (inputFilter == InputFilter.PASSWORD && !content.isEmpty()) {
            displayText = "*".repeat(content.length());
        }

        int visibleWidth = transform.scale.xToInt() - padding * 2;
        int cursorPx = fm.stringWidth(displayText.substring(0, cursorPosition));

        // Cursor moved past the right edge → scroll so cursor is at the right edge
        if (cursorPx - textOffsetX > visibleWidth) {
            textOffsetX = cursorPx - visibleWidth;
        }
        // Cursor moved past the left edge → scroll so cursor is at the left edge
        if (cursorPx - textOffsetX < 0) {
            textOffsetX = cursorPx;
        }
    }

    private void setFocused(boolean focus) {
        if (focused == focus) return;

        focused = focus;
        showCursor = focus;
        cursorTimer = 0;

        if (focus) {
            cursorPosition = content.length();
            if (onFocus != null) onFocus.call();
        } else {
            textOffsetX = 0; // reset scroll so unfocused text starts at the beginning
            if (onUnfocus != null) onUnfocus.call();
        }
    }

    private void triggerTextChanged() {
        if (onTextChanged != null) {
            onTextChanged.call(content);
        }
    }
    //</editor-fold>

    //<editor-fold desc="GETTERS/SETTERS">
    public String getText() {
        return content;
    }

    public void setText(String text) {
        this.content = text;
        this.cursorPosition = Math.min(cursorPosition, text.length());
        triggerTextChanged();
    }

    public void clear() {
        setText("");
    }

    public boolean isFocused() {
        return focused;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }
    //</editor-fold>

    //<editor-fold desc="DRAW">
    @Override
    public void draw(Graphics2D g) {
        if (!active) return;

        int x = transform.position.xToInt();
        int y = transform.position.yToInt();
        int w = transform.scale.xToInt();
        int h = transform.scale.yToInt();

        // Background
        g.setColor(focused ? focusedColor : backgroundColor);
        g.fillRect(x, y, w, h);

        // Border
        g.setColor(focused ? focusedBorderColor : borderColor);
        g.setStroke(new BasicStroke(focused ? 2 : 1));
        g.drawRect(x, y, w, h);

        // --- clip to the inner padded area so text never overflows visually ---
        Shape originalClip = g.getClip();
        g.setClip(x + padding, y + 1, w - padding * 2, h - 2);

        // Text
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();
        int textY = y + (h + fm.getAscent()) / 2 - 2;

        String displayText = content;
        if (inputFilter == InputFilter.PASSWORD && !content.isEmpty()) {
            displayText = "*".repeat(content.length());
        }

        // Update scroll offset so cursor stays visible (needs FontMetrics)
        if (focused) {
            updateTextOffset(fm);
        }

        int drawX = x + padding - textOffsetX; // base X shifted by scroll offset

        if (displayText.isEmpty() && !focused) {
            // Placeholder (never scrolled)
            g.setColor(placeholderColor);
            g.drawString(placeholder, x + padding, textY);
        } else {
            g.setColor(textColor);
            g.drawString(displayText, drawX, textY);

            // Cursor
            if (focused && showCursor) {
                String beforeCursor = displayText.substring(0, cursorPosition);
                int cursorX = drawX + fm.stringWidth(beforeCursor);

                g.setColor(textColor);
                g.fillRect(cursorX, textY - fm.getAscent(), 2, fm.getHeight());
            }
        }

        // Restore original clip
        g.setClip(originalClip);
    }

    @Override
    public void onCollision(GameObject collider) {}
    //</editor-fold>
}