package GameEngine.Core.util;

/**
 * A 2D vector class for mathematical operations.
 * Used for positions, directions, scales, and velocities throughout the engine.
 */
public class Vector2 {
    /** The x component of the vector. */
    public float x;
    /** The y component of the vector. */
    public float y;

    //<editor-fold desc="FINAL VEC2s">
    private static final Vector2 zero =  new Vector2(0,0);
    private static final Vector2 up = new Vector2(0,1);
    private static final Vector2 down = new Vector2(0,-1);
    private static final Vector2 left = new Vector2(-1,0);
    private static final Vector2 right = new Vector2(1,0);

    /**
     * Returns a copy of the zero vector (0, 0).
     *
     * @return A new Vector2 with both components set to 0
     */
    public static Vector2 zero() { return zero.copy(); }

    /**
     * Returns a copy of the up vector (0, 1).
     *
     * @return A new Vector2 pointing upward
     */
    public static Vector2 up() { return up.copy(); }

    /**
     * Returns a copy of the down vector (0, -1).
     *
     * @return A new Vector2 pointing downward
     */
    public static Vector2 down() { return down.copy(); }

    /**
     * Returns a copy of the left vector (-1, 0).
     *
     * @return A new Vector2 pointing left
     */
    public static Vector2 left() { return left.copy(); }

    /**
     * Returns a copy of the right vector (1, 0).
     *
     * @return A new Vector2 pointing right
     */
    public static Vector2 right() { return right.copy(); }
    //</editor-fold>

    //<editor-fold desc="CONSTRUCTORS">
    /**
     * Creates a new Vector2 with the specified x and y components.
     *
     * @param x The x component
     * @param y The y component
     */
    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Creates a new Vector2 as a copy of another vector.
     *
     * @param v The vector to copy
     */
    public Vector2(Vector2 v) {
        this.x = v.x;
        this.y = v.y;
    }

    /**
     * Creates a new Vector2 with both components set to the same value.
     *
     * @param xy The value for both x and y components
     */
    public Vector2(float xy) {
        this.x = xy;
        this.y = xy;
    }
    //</editor-fold>

    //<editor-fold desc="HELPER METHODS">
    /**
     * Returns the x component as an integer (rounded).
     *
     * @return The x component rounded to the nearest integer
     */
    public int xToInt() {
        return Math.round(x);
    }

    /**
     * Returns the y component as an integer (rounded).
     *
     * @return The y component rounded to the nearest integer
     */
    public int yToInt() {
        return Math.round(y);
    }

    /**
     * Adds another vector to this vector.
     *
     * @param other The vector to add
     * @return A new Vector2 with the sum
     */
    public Vector2 add(Vector2 other) {
        return new Vector2(this.x + other.x, this.y + other.y);
    }

    /**
     * Adds a scalar value to both components.
     *
     * @param xy The value to add to both components
     * @return A new Vector2 with the sum
     */
    public Vector2 add(float xy) {
        return add(new Vector2(xy));
    }

    /**
     * Subtracts another vector from this vector.
     *
     * @param other The vector to subtract
     * @return A new Vector2 with the difference
     */
    public Vector2 subtract(Vector2 other) {
        return new Vector2(this.x - other.x, this.y - other.y);
    }

    /**
     * Subtracts a scalar value from both components.
     *
     * @param xy The value to subtract from both components
     * @return A new Vector2 with the difference
     */
    public Vector2 subtract(float xy) {
        return subtract(new Vector2(xy));
    }

    /**
     * Multiplies both components by a scalar.
     *
     * @param scalar The value to multiply by
     * @return A new Vector2 with the product
     */
    public Vector2 multiply(float scalar) {
        return new Vector2(this.x * scalar, this.y * scalar);
    }

    /**
     * Divides both components by a scalar.
     *
     * @param scalar The value to divide by
     * @return A new Vector2 with the quotient
     */
    public Vector2 divide(float scalar) {
        return new Vector2(this.x / scalar, this.y / scalar);
    }

    /**
     * Returns the length (magnitude) of this vector.
     *
     * @return The length of the vector
     */
    public float length() {
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * Returns the squared length of this vector.
     * More efficient than length() when comparing distances.
     *
     * @return The squared length of the vector
     */
    public float sqrLength() {
        return x * x + y * y;
    }

    /**
     * Reflects this vector off a surface with the given normal.
     *
     * @param normal The surface normal to reflect off
     * @return A new reflected Vector2
     */
    public Vector2 reflect(Vector2 normal) {
        float dot = this.dot(normal);
        return this.subtract(normal.multiply(2 * dot));
    }

    /**
     * Returns a normalized version of this vector (length = 1).
     *
     * @return A new unit Vector2, or zero vector if length is 0
     */
    public Vector2 normalize() {
        float len = length();
        if (len == 0) return new Vector2(0, 0);
        return divide(len);
    }

    /**
     * Returns a normalized version of this vector scaled to the given length.
     *
     * @param scalar The desired length of the resulting vector
     * @return A new Vector2 with the specified length
     */
    public Vector2 normalize(float scalar) {
        float len = length();
        if (len == 0) return new Vector2(0, 0);
        return new Vector2((x / len) * scalar, (y / len) * scalar);
    }

    /**
     * Returns a new vector with absolute values of both components.
     *
     * @return A new Vector2 with positive components
     */
    public Vector2 abs() { return new Vector2(Math.abs(x), Math.abs(y)); }

    /**
     * Calculates the dot product with another vector.
     *
     * @param other The other vector
     * @return The dot product
     */
    public float dot(Vector2 other) {
        return this.x * other.x + this.y * other.y;
    }

    /**
     * Calculates the distance to another vector.
     *
     * @param other The other vector
     * @return The distance between the two vectors
     */
    public float distance(Vector2 other) {
        return subtract(other).length();
    }

    /**
     * Linearly interpolates between this vector and a target.
     *
     * @param target The target vector
     * @param t The interpolation factor (0-1)
     * @return A new interpolated Vector2
     */
    public Vector2 lerp(Vector2 target, float t) {
        t = Math.max(0, Math.min(1, t));
        return new Vector2(
                this.x + (target.x - this.x) * t,
                this.y + (target.y - this.y) * t
        );
    }

    /**
     * Converts this vector to an angle in radians.
     *
     * @return The angle in radians
     */
    public float toAngleRadiants() {
        return (float) Math.atan2(y, x);
    }

    /**
     * Converts this vector to an angle in degrees.
     *
     * @return The angle in degrees
     */
    public float toAngleDegrees() {
        return (float) Math.toDegrees(Math.atan2(y, x));
    }

    /**
     * Checks if this vector is smaller than another (both components).
     *
     * @param other The vector to compare with
     * @return true if both components are smaller
     */
    public boolean smallerThan(Vector2 other) {
        if (other == null) return false;
        return x < other.x && y < other.y;
    }

    /**
     * Checks if this vector is bigger than another (both components).
     *
     * @param other The vector to compare with
     * @return true if both components are bigger
     */
    public boolean biggerThan(Vector2 other) {
        if (other == null) return false;
        return x > other.x && y > other.y;
    }

    /**
     * Returns a normalized direction vector pointing to the target.
     *
     * @param target The target position
     * @return A unit vector pointing toward the target
     */
    public Vector2 directionTo(Vector2 target) {
        return target.subtract(this).normalize();
    }

    /**
     * Creates a copy of this vector.
     *
     * @return A new Vector2 with the same values
     */
    public Vector2 copy() {
        return new Vector2(this.x, this.y);
    }

    /**
     * Creates a clone of this vector (alias for copy).
     *
     * @return A new Vector2 with the same values
     */
    public Vector2 clone() {
        return copy();
    }

    /**
     * Sets the x and y components of this vector.
     *
     * @param x The new x value
     * @param y The new y value
     */
    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Sets this vector's components to match another vector.
     *
     * @param v The vector to copy values from
     */
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

    /**
     * Clamps both components to the specified range.
     *
     * @param min The minimum value for both components
     * @param max The maximum value for both components
     * @return A new clamped Vector2
     */
    public Vector2 clamp(float min, float max) {
        return new Vector2(
                Math.max(min, Math.min(max, x)),
                Math.max(min, Math.min(max, y))
        );
    }

    /**
     * Clamps each component to its own specified range.
     *
     * @param minX The minimum x value
     * @param maxX The maximum x value
     * @param minY The minimum y value
     * @param maxY The maximum y value
     * @return A new clamped Vector2
     */
    public Vector2 clamp(float minX, float maxX, float minY, float maxY) {
        return new Vector2(
                Math.max(minX, Math.min(maxX, x)),
                Math.max(minY, Math.min(maxY, y))
        );
    }

    /**
     * Creates a unit vector from an angle in radians.
     *
     * @param angle The angle in radians
     * @return A new unit Vector2 pointing in the specified direction
     */
    public static Vector2 fromAngleRadiants(float angle) {
        return new Vector2(
                (float)Math.cos(angle),
                (float)Math.sin(angle)
        );
    }

    /**
     * Creates a unit vector from an angle in degrees.
     *
     * @param angleDegrees The angle in degrees
     * @return A new unit Vector2 pointing in the specified direction
     */
    public static Vector2 fromAngleDegrees(float angleDegrees) {
        float angleRad = (float) Math.toRadians(angleDegrees);
        return new Vector2(
                (float) Math.cos(angleRad),
                (float) Math.sin(angleRad)
        );
    }
    //</editor-fold>
}