package GameEngine.Core.util;

import java.awt.*;
import java.util.Random;

public final class MathUtils {

    private static final Random random = new Random();

    private MathUtils() {}

    // Zufallswerte
    public static int randomInt(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }
    public static float randomFloat(float min, float max) {
        return min + random.nextFloat() * (max - min);
    }
    public static Color randomColor() {
        return new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    // Mathematische Hilfen
    public static float lerp(float start, float end, float t) {
        return start + (end - start) * t;
    }
    public static float lerpAngle(float a, float b, float t) {
        float diff = ((b - a + 180 + 360) % 360) - 180;
        return a + diff * t;
    }
    public static float angleDiff(float current, float target) {
    float diff = (target - current) % 360f;
    if (diff > 180f) diff -= 360f;
    if (diff < -180f) diff += 360f;
    return diff;
}


    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
    public static float map(float value, float start1, float stop1, float start2, float stop2) {
        return start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1));
    }
    public static float distance(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    // Kollisionen
    public static boolean rectCollision(float x1, float y1, float w1, float h1, float x2, float y2, float w2, float h2) {
        return x1 < x2 + w2 && x1 + w1 > x2 &&
                y1 < y2 + h2 && y1 + h1 > y2;
    }
    public static boolean circleCollision(float x1, float y1, float r1, float x2, float y2, float r2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        return distance < r1 + r2;
    }
    public static boolean pointInRect(float px, float py, float x, float y, float w, float h) {
        return px >= x && px <= x + w && py >= y && py <= y + h;
    }

    //Mit Vector2
    public static float distance(Vector2 a, Vector2 b) {
        return distance(a.x, a.y, b.x, b.y);
    }
    public static boolean rectCollision(Vector2 pos1, Vector2 size1, Vector2 pos2, Vector2 size2) {
        return rectCollision(pos1.x, pos1.y, size1.x, size1.y, pos2.x, pos2.y, size2.x, size2.y);
    }
    public static boolean pointInRect(Vector2 point, Vector2 pos, Vector2 size) {
        return pointInRect(point.x, point.y, pos.x, pos.y, size.x, size.y);
    }
    public static Vector2 clamp(Vector2 value, float minX, float maxX, float minY, float maxY) {
        return new Vector2(
                clamp(value.x, minX, maxX),
                clamp(value.y, minY, maxY)
        );
    }
    public static Vector2 lerpVector(Vector2 a, Vector2 b, float t) {
        t = Math.max(0, Math.min(1, t));
        return new Vector2(
                a.x + (b.x - a.x) * t,
                a.y + (b.y - a.y) * t
        );
    }

}
