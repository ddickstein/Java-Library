// Copyright (c) 2014 Daniel S. Dickstein

package library.java7.function;

// Idea for this interface came from Alex Reinking - allows support for anticipating checked exceptions

public interface ExceptionalSupplier<T> {
  public T get() throws Exception;
}
