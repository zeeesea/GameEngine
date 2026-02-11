package GameEngine.Core.gameObject.Obj;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A styled string that supports multiple colors for different parts of the text.
 * Similar to StringBuilder but with color styling support.
 *
 * Usage examples:
 * <pre>
 * // Builder pattern
 * StyledString styled = new StyledString.Builder()
 *     .append("Hello ", Color.GREEN)
 *     .append("World", Color.RED)
 *     .build();
 *
 * // From ANSI-style string
 * StyledString styled = StyledString.fromAnsi("\u001b[92mHello \u001b[91mWorld", Color.WHITE);
 *
 * // Simple coloring
 * StyledString styled = new StyledString("Hello World", Color.WHITE)
 *     .colorRange(0, 5, Color.GREEN)   // "Hello" in green
 *     .colorRange(6, 11, Color.RED);   // "World" in red
 * </pre>
 */
public class StyledString {

    private final List<StyledSegment> segments;

    /**
     * A segment of text with a specific color.
     */
    public static class StyledSegment {
        public final String text;
        public final Color color;

        public StyledSegment(String text, Color color) {
            this.text = text;
            this.color = color;
        }
    }

    //<editor-fold desc="CONSTRUCTORS">
    /**
     * Creates a styled string with a single color for all text.
     */
    public StyledString(String text, Color color) {
        this.segments = new ArrayList<>();
        if (text != null && !text.isEmpty()) {
            this.segments.add(new StyledSegment(text, color));
        }
    }

    /**
     * Creates a styled string from existing segments.
     */
    private StyledString(List<StyledSegment> segments) {
        this.segments = new ArrayList<>(segments);
    }

    /**
     * Creates an empty styled string.
     */
    public StyledString() {
        this.segments = new ArrayList<>();
    }
    //</editor-fold>

    //<editor-fold desc="BUILDER">
    /**
     * Builder for creating styled strings with multiple colored segments.
     */
    public static class Builder {
        private final List<StyledSegment> segments = new ArrayList<>();

        /**
         * Appends text with the specified color.
         */
        public Builder append(String text, Color color) {
            if (text != null && !text.isEmpty()) {
                segments.add(new StyledSegment(text, color));
            }
            return this;
        }

        /**
         * Appends text with white color (default).
         */
        public Builder append(String text) {
            return append(text, Color.WHITE);
        }

        /**
         * Appends another styled string.
         */
        public Builder append(StyledString styledString) {
            segments.addAll(styledString.segments);
            return this;
        }

        /**
         * Builds the final StyledString.
         */
        public StyledString build() {
            return new StyledString(segments);
        }
    }
    //</editor-fold>

    //<editor-fold desc="FACTORY METHODS">
    /**
     * Creates a StyledString from an ANSI-style escaped string.
     * Supports common ANSI color codes:
     * - \u001b[30m - Black
     * - \u001b[31m - Red
     * - \u001b[32m - Green
     * - \u001b[33m - Yellow
     * - \u001b[34m - Blue
     * - \u001b[35m - Magenta
     * - \u001b[36m - Cyan
     * - \u001b[37m - White
     * - \u001b[90m-97m - Bright variants
     * - \u001b[0m - Reset to default
     *
     * @param ansiString The string with ANSI escape codes
     * @param defaultColor The default color to use when no color is specified
     * @return A StyledString with the appropriate colors
     */
    public static StyledString fromAnsi(String ansiString, Color defaultColor) {
        if (ansiString == null || ansiString.isEmpty()) {
            return new StyledString();
        }

        List<StyledSegment> segments = new ArrayList<>();
        // Pattern to match ANSI escape codes: \u001b[XXm or \033[XXm
        Pattern pattern = Pattern.compile("\u001b\\[(\\d+)m|\\\\u001b\\[(\\d+)m");
        Matcher matcher = pattern.matcher(ansiString);

        Color currentColor = defaultColor;
        int lastEnd = 0;

        while (matcher.find()) {
            // Add text before this escape code
            if (matcher.start() > lastEnd) {
                String text = ansiString.substring(lastEnd, matcher.start());
                if (!text.isEmpty()) {
                    segments.add(new StyledSegment(text, currentColor));
                }
            }

            // Parse the color code
            String codeStr = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
            int code = Integer.parseInt(codeStr);
            currentColor = ansiCodeToColor(code, defaultColor);

            lastEnd = matcher.end();
        }

        // Add remaining text
        if (lastEnd < ansiString.length()) {
            String text = ansiString.substring(lastEnd);
            if (!text.isEmpty()) {
                segments.add(new StyledSegment(text, currentColor));
            }
        }

        return new StyledString(segments);
    }

    /**
     * Creates a StyledString from an ANSI-style escaped string with white as default.
     */
    public static StyledString fromAnsi(String ansiString) {
        return fromAnsi(ansiString, Color.WHITE);
    }

    /**
     * Converts ANSI color code to Java Color.
     */
    private static Color ansiCodeToColor(int code, Color defaultColor) {
        return switch (code) {
            case 0 -> defaultColor;  // Reset
            case 30 -> Color.BLACK;
            case 31 -> new Color(170, 0, 0);      // Red
            case 32 -> new Color(0, 170, 0);      // Green
            case 33 -> new Color(170, 170, 0);    // Yellow
            case 34 -> new Color(0, 0, 170);      // Blue
            case 35 -> new Color(170, 0, 170);    // Magenta
            case 36 -> new Color(0, 170, 170);    // Cyan
            case 37 -> new Color(170, 170, 170);  // White
            case 90 -> new Color(85, 85, 85);     // Bright Black (Gray)
            case 91 -> new Color(255, 85, 85);    // Bright Red
            case 92 -> new Color(85, 255, 85);    // Bright Green
            case 93 -> new Color(255, 255, 85);   // Bright Yellow
            case 94 -> new Color(85, 85, 255);    // Bright Blue
            case 95 -> new Color(255, 85, 255);   // Bright Magenta
            case 96 -> new Color(85, 255, 255);   // Bright Cyan
            case 97 -> Color.WHITE;               // Bright White
            default -> defaultColor;
        };
    }
    //</editor-fold>

    //<editor-fold desc="MODIFICATION METHODS">
    /**
     * Colors a range of characters in the string.
     * This creates a new StyledString with the specified range colored.
     *
     * @param startIndex Start index (inclusive)
     * @param endIndex End index (exclusive)
     * @param color The color to apply
     * @return A new StyledString with the colored range
     */
    public StyledString colorRange(int startIndex, int endIndex, Color color) {
        String fullText = getPlainText();
        if (startIndex < 0 || endIndex > fullText.length() || startIndex >= endIndex) {
            return this;
        }

        List<StyledSegment> newSegments = new ArrayList<>();

        // We need to rebuild segments based on the new coloring
        int currentPos = 0;
        for (StyledSegment segment : segments) {
            int segmentStart = currentPos;
            int segmentEnd = currentPos + segment.text.length();

            // Check if this segment overlaps with the color range
            if (segmentEnd <= startIndex || segmentStart >= endIndex) {
                // No overlap, keep segment as is
                newSegments.add(segment);
            } else {
                // There is overlap, split the segment

                // Part before the color range
                if (segmentStart < startIndex) {
                    int beforeEnd = Math.min(startIndex, segmentEnd) - segmentStart;
                    newSegments.add(new StyledSegment(
                        segment.text.substring(0, beforeEnd),
                        segment.color
                    ));
                }

                // The colored part
                int colorStart = Math.max(0, startIndex - segmentStart);
                int colorEnd = Math.min(segment.text.length(), endIndex - segmentStart);
                if (colorStart < colorEnd) {
                    newSegments.add(new StyledSegment(
                        segment.text.substring(colorStart, colorEnd),
                        color
                    ));
                }

                // Part after the color range
                if (segmentEnd > endIndex) {
                    int afterStart = endIndex - segmentStart;
                    newSegments.add(new StyledSegment(
                        segment.text.substring(afterStart),
                        segment.color
                    ));
                }
            }

            currentPos = segmentEnd;
        }

        return new StyledString(newSegments);
    }

    /**
     * Colors specific characters by their indices.
     *
     * @param indices Array of character indices to color
     * @param color The color to apply
     * @return A new StyledString with the colored characters
     */
    public StyledString colorIndices(int[] indices, Color color) {
        StyledString result = this;
        // Sort and process indices in reverse to avoid offset issues
        java.util.Arrays.sort(indices);
        for (int i = indices.length - 1; i >= 0; i--) {
            int idx = indices[i];
            result = result.colorRange(idx, idx + 1, color);
        }
        // Merge adjacent segments with same color
        return result.optimize();
    }

    /**
     * Colors all occurrences of a substring.
     *
     * @param substring The substring to find and color
     * @param color The color to apply
     * @return A new StyledString with colored occurrences
     */
    public StyledString colorSubstring(String substring, Color color) {
        String fullText = getPlainText();
        StyledString result = this;

        int index = 0;
        while ((index = fullText.indexOf(substring, index)) != -1) {
            result = result.colorRange(index, index + substring.length(), color);
            index += substring.length();
        }

        return result.optimize();
    }

    /**
     * Optimizes the styled string by merging adjacent segments with the same color.
     */
    public StyledString optimize() {
        if (segments.isEmpty()) {
            return this;
        }

        List<StyledSegment> optimized = new ArrayList<>();
        StyledSegment current = segments.get(0);

        for (int i = 1; i < segments.size(); i++) {
            StyledSegment next = segments.get(i);
            if (current.color.equals(next.color)) {
                // Merge segments
                current = new StyledSegment(current.text + next.text, current.color);
            } else {
                optimized.add(current);
                current = next;
            }
        }
        optimized.add(current);

        return new StyledString(optimized);
    }
    //</editor-fold>

    //<editor-fold desc="GETTERS">
    /**
     * Returns the plain text without any styling information.
     */
    public String getPlainText() {
        StringBuilder sb = new StringBuilder();
        for (StyledSegment segment : segments) {
            sb.append(segment.text);
        }
        return sb.toString();
    }

    /**
     * Returns the list of styled segments.
     */
    public List<StyledSegment> getSegments() {
        return new ArrayList<>(segments);
    }

    /**
     * Returns the total length of the text.
     */
    public int length() {
        int len = 0;
        for (StyledSegment segment : segments) {
            len += segment.text.length();
        }
        return len;
    }

    /**
     * Returns true if the styled string is empty.
     */
    public boolean isEmpty() {
        return segments.isEmpty() || getPlainText().isEmpty();
    }
    //</editor-fold>

    @Override
    public String toString() {
        return getPlainText();
    }
}
