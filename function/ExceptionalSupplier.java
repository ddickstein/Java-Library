package library.function;

// Idea for this interface came from Alex Reinking - allows support for anticipating checked exceptions

public interface ExceptionalSupplier<T> {
  public T get() throws Exception;
}
