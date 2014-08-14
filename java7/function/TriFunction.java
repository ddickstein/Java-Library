// Copyright (c) 2014 Daniel S. Dickstein

package library.java7.function;

public interface TriFunction<T, U, V, R> {
  public R apply(T t, U u, V v);
}