package library.function;

// Idea for this class came from Alex Reinking

public interface ExceptionalSupplier<T> {
  public T get() throws Exception;
}