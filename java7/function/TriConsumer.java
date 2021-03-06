// Copyright (c) 2014 Daniel S. Dickstein

package library.java7.function;

// Interface for Java 7 compatibility with Java 8 tools

public interface TriConsumer<T, U, V> {
  public void accept(T t, U u, V v);
}