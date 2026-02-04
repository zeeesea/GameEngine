package GameEngine.Core.util;

import java.awt.*;
import java.util.Random;

/**
 * Utility class providing common mathematical operations.
 * Includes random number generation, interpolation, clamping, and collision detection helpers.
 */
public final class MathUtils {

    private static final Random random = new Random();

    private MathUtils() {}

    /**
     * Returns a random integer within the specified range (inclusive).
     *
     * @param min The minimum value
     * @param max The maximum value
     * @return A random integer between min and max
     */
    public static int randomInt(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    /**
     * Returns a random float within the specified range.
     *
     * @param min The minimum value
     * @param max The maximum value
     * @return A random float between min and max
     */
    public static float randomFloat(float min, float max) {
        return min + random.nextFloat() * (max - min);
    }

    /**
     * Returns a random RGB color.
     *
     * @return A random Color
     */
    public static Color randomColor() {
        return new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    /**
     * Linearly interpolates between two values.
     *
     * @param start The start value
     * @param end The end value
     * @param t The interpolation factor (0-1)
     * @return The interpolated value
     */
    public static float lerp(float start, float end, float t) {
        return start + (end - start) * t;
    }

    /**
     * Linearly interpolates between two angles, handling wrap-around.
     *
     * @param a The start angle in degrees
     * @param b The end angle in degrees
     * @param t The interpolation factor (0-1)
     * @return The interpolated angle
     */
    public static float lerpAngle(float a, float b, float t) {
        float diff = ((b - a + 180 + 360) % 360) - 180;
        return a + diff * t;
    }

    /**
     * Linearly interpolates between two colors
     *
     * @param start The start color
     * @param end The end color
     * @param ratio The interpolation factor (0-1)
     * @return The interpolated color
     */
    public static Color lerpColor(Color start, Color end, float ratio) {
        //Colors
        Color from = start != null ? start : Color.WHITE;
        Color to = end != null ? end : from;

        if (Float.isNaN(ratio) || Float.isInfinite(ratio)) ratio = 0f;
        ratio = MathUtils.clamp(ratio, 0f, 1f);

        int r = (int) MathUtils.clamp(MathUtils.lerp(from.getRed(), to.getRed(), ratio), 0f, 255f);
        int g = (int) MathUtils.clamp(MathUtils.lerp(from.getGreen(), to.getGreen(), ratio), 0f, 255f);
        int b = (int) MathUtils.clamp(MathUtils.lerp(from.getBlue(), to.getBlue(), ratio), 0f, 255f);

        return new Color(r, g, b);
    }

    /**
     * Returns the shortest difference between two angles.
     *
     * @param current The current angle in degrees
     * @param target The target angle in degrees
     * @return The angle difference (-180 to 180)
     */
    public static float angleDiff(float current, float target) {
        float diff = (target - current) % 360f;
        if (diff > 180f) diff -= 360f;
        if (diff < -180f) diff += 360f;
        return diff;
    }

    /**
     * Clamps a value to the specified range.
     *
     * @param value The value to clamp
     * @param min The minimum value
     * @param max The maximum value
     * @return The clamped value
     */
    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Maps a value from one range to another.
     *
     * @param value The value to map
     * @param start1 The start of the input range
     * @param stop1 The end of the input range
     * @param start2 The start of the output range
     * @param stop2 The end of the output range
     * @return The mapped value
     */
    public static float map(float value, float start1, float stop1, float start2, float stop2) {
        return start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1));
    }

    /**
     * Calculates the distance between two points.
     *
     * @param x1 First point x
     * @param y1 First point y
     * @param x2 Second point x
     * @param y2 Second point y
     * @return The distance
     */
    public static float distance(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Checks if two rectangles collide.
     *
     * @param x1 First rect x
     * @param y1 First rect y
     * @param w1 First rect width
     * @param h1 First rect height
     * @param x2 Second rect x
     * @param y2 Second rect y
     * @param w2 Second rect width
     * @param h2 Second rect height
     * @return true if rectangles overlap
     */
    public static boolean rectCollision(float x1, float y1, float w1, float h1, float x2, float y2, float w2, float h2) {
        return x1 < x2 + w2 && x1 + w1 > x2 &&
                y1 < y2 + h2 && y1 + h1 > y2;
    }

    /**
     * Checks if two circles collide.
     *
     * @param x1 First circle center x
     * @param y1 First circle center y
     * @param r1 First circle radius
     * @param x2 Second circle center x
     * @param y2 Second circle center y
     * @param r2 Second circle radius
     * @return true if circles overlap
     */
    public static boolean circleCollision(float x1, float y1, float r1, float x2, float y2, float r2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        return distance < r1 + r2;
    }

    /**
     * Checks if a point is inside a rectangle.
     *
     * @param px Point x
     * @param py Point y
     * @param x Rect x
     * @param y Rect y
     * @param w Rect width
     * @param h Rect height
     * @return true if point is inside
     */
    public static boolean pointInRect(float px, float py, float x, float y, float w, float h) {
        return px >= x && px <= x + w && py >= y && py <= y + h;
    }

    /**
     * Calculates the distance between two vectors.
     *
     * @param a First vector
     * @param b Second vector
     * @return The distance
     */
    public static float distance(Vector2 a, Vector2 b) {
        return distance(a.x, a.y, b.x, b.y);
    }

    /**
     * Checks if two rectangles collide (Vector2 version).
     *
     * @param pos1 First rect position
     * @param size1 First rect size
     * @param pos2 Second rect position
     * @param size2 Second rect size
     * @return true if rectangles overlap
     */
    public static boolean rectCollision(Vector2 pos1, Vector2 size1, Vector2 pos2, Vector2 size2) {
        return rectCollision(pos1.x, pos1.y, size1.x, size1.y, pos2.x, pos2.y, size2.x, size2.y);
    }

    /**
     * Checks if a point is inside a rectangle (Vector2 version).
     *
     * @param point The point to check
     * @param pos The rect position
     * @param size The rect size
     * @return true if point is inside
     */
    public static boolean pointInRect(Vector2 point, Vector2 pos, Vector2 size) {
        return pointInRect(point.x, point.y, pos.x, pos.y, size.x, size.y);
    }

    /**
     * Clamps a vector to the specified ranges.
     *
     * @param value The vector to clamp
     * @param minX Minimum x value
     * @param maxX Maximum x value
     * @param minY Minimum y value
     * @param maxY Maximum y value
     * @return The clamped vector
     */
    public static Vector2 clamp(Vector2 value, float minX, float maxX, float minY, float maxY) {
        return new Vector2(
                clamp(value.x, minX, maxX),
                clamp(value.y, minY, maxY)
        );
    }

    /**
     * Linearly interpolates between two vectors.
     *
     * @param a The start vector
     * @param b The end vector
     * @param t The interpolation factor (0-1)
     * @return The interpolated vector
     */
    public static Vector2 lerpVector(Vector2 a, Vector2 b, float t) {
        t = Math.max(0, Math.min(1, t));
        return new Vector2(
                a.x + (b.x - a.x) * t,
                a.y + (b.y - a.y) * t
        );
    }

}
