package library.java7.option;

import library.java7.function.Function;
import library.java7.function.Supplier;

class Some<A> extends Option<A> {
  private A value;

  Some(A value) { this.value = value; }

  @Override
  public A getOrElse(A other) { return value; }

  @Override
  public A getOrElse(Supplier<A> other) { return value; }

  @Override
  public Option<? super A> orElse(Option<? super A> otherOption) { return this; }

  @Override
  public Option<? super A> orElse(Supplier<Option<? super A>> otherOption) { return this; }

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
  public <B> Option<B> map(Function<A, B> func) {
    B result = func.apply(value);
    return (result == null) ? new None<B>() : new Some<B>(result);
  }

  @Override
  public <B> Option<B> flatMap(Function<A, Option<B>> func) { return func.apply(value); }

  @Override
  public <B> B applyOrElse(Function<A, B> funcIfSome, Supplier<B> funcIfNone) { return funcIfSome.apply(value); }

  @Override
  public String toString() {
    return "Some(" + value + ")";
  }
}