package GameEngine.Core.gameObject.FuncInt;

/**
 * A functional interface for callbacks with two parameters.
 * Used for event handling where two values need to be passed.
 *
 * @param <T> The type of the first parameter
 * @param <V> The type of the second parameter
 */
@FunctionalInterface
public interface FuncIntTwo<T, V> {
    /**
     * Executes the callback function with two parameters.
     *
     * @param t The first parameter value
     * @param v The second parameter value
     */
    void call(T t, V v);
}