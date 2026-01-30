package GameEngine.Core.gameObject.FuncInt;

@FunctionalInterface
public interface FuncIntTwo<T,V> {
    void call(T t, V v);
}