package library.tuple;

import java.util.function.BiFunction;

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
}