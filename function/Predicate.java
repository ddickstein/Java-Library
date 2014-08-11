package library.function;

// Interface for Java 7 compatibility with Java 8 tools

public interface Predicate<T> {
  public boolean test(T t);
}