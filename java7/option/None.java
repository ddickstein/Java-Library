package library.java7.option;

import library.function.Consumer;
import library.function.Function;
import library.function.Supplier;

class None<A> extends Option<A> {
  @Override
  public A getOrElse(A other) { return other; }

  @Override
  public A getOrElse(Supplier<A> other) { return other.get(); }

  @Override
  public Option<? super A> orElse(Option<? super A> otherOption) { return otherOption; }

  @Override
  public Option<? super A> orElse(Supplier<Option<? super A>> otherOption) { return otherOption.get(); }

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
  public void forEach(Consumer<A> func) {}

  @Override
  public <B> Option<B> map(Function<A, B> func) { return new None<B>(); }

  @Override
  public <B> Option<B> flatMap(Function<A, Option<B>> func) { return new None<B>(); }

  @Override
  public String toString() {
    return "None";
  }
}