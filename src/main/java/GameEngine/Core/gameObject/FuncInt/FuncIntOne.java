package GameEngine.Core.gameObject.FuncInt;

/**
 * A functional interface for callbacks with one parameter.
 * Used for event handling where a single value needs to be passed.
 *
 * @param <T> The type of the parameter passed to the callback
 */
@FunctionalInterface
public interface FuncIntOne<T> {
    /**
     * Executes the callback function with one parameter.
     *
     * @param t The parameter value
     */
    void call(T t);
}
