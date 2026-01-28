package GameEngine.Core.util;

public class Vector2 {
    public float x, y;

    public static final Vector2 zero =  new Vector2(0,0);
    public static final Vector2 up = new Vector2(0,1);
    public static final Vector2 down = new Vector2(0,-1);
    public static final Vector2 left = new Vector2(-1,0);
    public static final Vector2 right = new Vector2(1,0);

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }
    public Vector2(Vector2 v) {
        this.x = v.x;
        this.y = v.y;
    }
    public Vector2(float xy) {
        this.x = xy;
        this.y = xy;
    }

    public int xToInt() {
        return Math.round(x);
    }
    public int yToInt() {
        return Math.round(y);
    }

    public Vector2 add(Vector2 other) {
        return new Vector2(this.x + other.x, this.y + other.y);
    }
    public Vector2 subtract(Vector2 other) {
        return new Vector2(this.x - other.x, this.y - other.y);
    }
    public Vector2 multiply(float scalar) {
        return new Vector2(this.x * scalar, this.y * scalar);
    }
    public Vector2 divide(float scalar) {
        return new Vector2(this.x / scalar, this.y / scalar);
    }
    public float length() {
        return (float) Math.sqrt(x * x + y * y);
    }
    public float sqrLength() {
        return x * x + y * y;
    }
    public Vector2 reflect(Vector2 normal) {
        float dot = this.dot(normal);
        return this.subtract(normal.multiply(2 * dot));
    }
    public Vector2 normalize() {
        float len = length();
        if (len == 0) return new Vector2(0, 0);
        return divide(len);
    }
    public Vector2 normalize(float scalar) {
        float len = length();
        if (len == 0) return new Vector2(0, 0);
        return new Vector2((x / len) * scalar, (y / len) * scalar);
    }
    public Vector2 abs() { return new Vector2(Math.abs(x), Math.abs(y)); }
    public float dot(Vector2 other) {
        return this.x * other.x + this.y * other.y;
    }
    public float distance(Vector2 other) {
        return subtract(other).length();
    }
    public Vector2 lerp(Vector2 target, float t) {
        t = Math.max(0, Math.min(1, t));
        return new Vector2(
                this.x + (target.x - this.x) * t,
                this.y + (target.y - this.y) * t
        );
    }
    public float toAngleRadiants() {
        return (float) Math.atan2(y, x);
    }
    public float toAngleDegrees() {
        return (float) Math.toDegrees(Math.atan2(y, x));
    }

    public Vector2 directionTo(Vector2 target) {
        return target.subtract(this).normalize();
    }
    public Vector2 copy() {
        return new Vector2(this.x, this.y);
    }
    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }
    public void set(Vector2 v) {
        this.x = v.x;
        this.y = v.y;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Vector2 vector2 = (Vector2) obj;
        return Float.compare(vector2.x, x) == 0 && Float.compare(vector2.y, y) == 0;
    }
    @Override
    public String toString() {
        return "Vector2(" + x + ", " + y + ")";
    }
    public Vector2 clamp(float min, float max) {
        return new Vector2(
                Math.max(min, Math.min(max, x)),
                Math.max(min, Math.min(max, y))
        );
    }
    public Vector2 clamp(float minX, float maxX, float minY, float maxY) {
        return new Vector2(
                Math.max(minX, Math.min(maxX, x)),
                Math.max(minY, Math.min(maxY, y))
        );
    }

    public static Vector2 fromAngleRadiants(float angle) {
        return new Vector2(
                (float)Math.cos(angle),
                (float)Math.sin(angle)
        );
    }
    public static Vector2 fromAngleDegrees(float angleDegrees) {
        float angleRad = (float) Math.toRadians(angleDegrees); // Grad → Radiant
        return new Vector2(
                (float) Math.cos(angleRad),
                (float) Math.sin(angleRad)
        );
    }
}