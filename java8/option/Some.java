// Copyright (c) 2014 Daniel S. Dickstein

package library.java8.option;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import library.java8.function.Block;

class Some<A> extends Option<A> {
  private A value;

  Some(A value) { this.value = value; }

  @Override
  public A getOrElse(A other) { return value; }

  @Override
  public A getOrElse(Supplier<A> other) { return value; }

  @Override
  public Option<A> orElse(Option<A> otherOption) { return this; }

  @Override
  public Option<A> orElse(Supplier<Option<A>> otherOption) { return this; }

  @Override
  public boolean isDefined() { return true; }

  @Override
  public boolean isEmpty() { return false; }

  @Override
  public boolean isDefinedAnd(Function<A, Boolean> func) { return func.apply(value); }

  @Override
  public boolean isEmptyOr(Function<A, Boolean> func) { return func.apply(value); }

  @Override
  public Option<A> filter(Function<A, Boolean> func) { return func.apply(value) ? this : new None<A>(); }

  @Override
  public Option<A> filterNot(Function<A, Boolean> func) { return !func.apply(value) ? this : new None<A>(); }

  @Override
  public Option<A> doIfDefined(Consumer<A> func) { func.accept(value); return this; }

  @Override
  public Option<A> doIfEmpty(Block func) { return this; }

  @Override
  public <B> Option<B> map(Function<A, B> func) {
    B result = func.apply(value);
    return (result == null) ? new None<B>() : new Some<B>(result);
  }

  @Override
  public <B> Option<B> flatMap(Function<A, Option<B>> func) { return func.apply(value); }

  @Override
  public String toString() {
    return "Some(" + value + ")";
  }
}