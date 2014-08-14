// Copyright (c) 2014 Daniel S. Dickstein

package library.java7.option;

import library.java7.function.Block;
import library.java7.function.Consumer;
import library.java7.function.Function;
import library.java7.function.Supplier;

public abstract class Option<A> {
  public abstract A getOrElse(A other);
  public abstract A getOrElse(Supplier<A> other);
  public abstract Option<A> orElse(Option<A> otherOption);
  public abstract Option<A> orElse(Supplier<Option<A>> otherOption);
  public abstract boolean isDefined();
  public abstract boolean isEmpty();
  public abstract boolean isDefinedAnd(Function<A, Boolean> func);
  public abstract boolean isEmptyOr(Function<A, Boolean> func);
  public abstract Option<A> filter(Function<A, Boolean> func);
  public abstract Option<A> filterNot(Function<A, Boolean> func);
  public abstract Option<A> doIfDefined(Consumer<A> func);
  public abstract Option<A> doIfEmpty(Block func);
  public abstract <B> Option<B> map(Function<A, B> func);
  public abstract <B> Option<B> flatMap(Function<A, Option<B>> func);
  
  public <B> B applyOrElse(Function<A, B> funcIfSome, Supplier<B> funcIfNone) {
    return map(funcIfSome).getOrElse(funcIfNone);
  }

  public static <A> Option<A> wrap(A value) {
    return (value == null) ? new None<A>() : new Some<A>(value);
  }

  public static <A, B> Function<A, Option<B>> wrapFunc(Function<A, B> func) {
    return new Function<A, Option<B>>() {
      @Override
      public Option<B> apply(A value) {
        return wrap(func.apply(value));
      }
    };
  }

  public static <A> Option<A> wrapIf(boolean pred, A value) {
    return pred ? wrap(value) : new None<A>();
  }
}