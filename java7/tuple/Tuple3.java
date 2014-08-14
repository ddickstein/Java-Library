// Copyright (c) 2014 Daniel S. Dickstein

package library.java7.tuple;

import library.java7.function.TriFunction;

public class Tuple3<A, B, C> {
  public A _1;
  public B _2;
  public C _3;

  public Tuple3(A a, B b, C c) {
    this._1 = a;
    this._2 = b;
    this._3 = c;
  }

  public <R> R map(TriFunction<A, B, C, R> func) {
    return func.apply(_1, _2, _3);
  }
}