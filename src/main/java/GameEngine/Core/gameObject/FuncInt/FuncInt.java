package GameEngine.Core.gameObject.FuncInt;

/**
 * A functional interface for callbacks with no parameters.
 * Used for event handling like onClick, onHover, etc.
 */
@FunctionalInterface
public interface FuncInt {
    /**
     * Executes the callback function.
     */
    void call();
}
