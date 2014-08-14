// Copyright (c) 2014 Daniel S. Dickstein

package library.java8.tuple;

import java.util.function.BiFunction;
import java.util.function.BiConsumer;

public class Tuple2<A, B> {
  public A _1;
  public B _2;

  public Tuple2(A a, B b) {
    this._1 = a;
    this._2 = b;
  }

  public <R> R map(BiFunction<A, B, R> func) {
    return func.apply(_1, _2);
  }

  public void forEach(BiConsumer<A, B> func) {
    func.accept(_1, _2);
  }
}