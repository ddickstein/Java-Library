package library.function;

public interface ExceptionalSupplier<T> {
  public T get() throws Exception;
}