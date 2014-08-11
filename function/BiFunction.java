package library.function;

// Interface for Java 7 compatibility with Java 8 tools

public interface BiFunction<T, U, R> {
  public R apply(T t, U u);
}