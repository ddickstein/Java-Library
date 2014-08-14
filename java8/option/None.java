// Copyright (c) 2014 Daniel S. Dickstein

package library.java8.option;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import library.java8.function.Block;

class None<A> extends Option<A> {
  @Override
  public A getOrElse(A other) { return other; }

  @Override
  public A getOrElse(Supplier<A> other) { return other.get(); }

  @Override
  public Option<A> orElse(Option<A> otherOption) { return otherOption; }

  @Override
  public Option<A> orElse(Supplier<Option<A>> otherOption) { return otherOption.get(); }

  @Override
  public boolean isDefined() { return false; }

  @Override
  public boolean isEmpty() { return true; }

  @Override
  public boolean isDefinedAnd(Function<A, Boolean> func) { return false; }

  @Override
  public boolean isEmptyOr(Function<A, Boolean> func) { return true; }

  @Override
  public Option<A> filter(Function<A, Boolean> func) { return this; }

  @Override
  public Option<A> filterNot(Function<A, Boolean> func) { return this; }

  @Override
  public Option<A> doIfDefined(Consumer<A> func) { return this; }

  @Override
  public Option<A> doIfEmpty(Block func) { func.execute(); return this; }

  @Override
  public <B> Option<B> map(Function<A, B> func) { return new None<B>(); }

  @Override
  public <B> Option<B> flatMap(Function<A, Option<B>> func) { return new None<B>(); }

  @Override
  public String toString() {
    return "None";
  }
}