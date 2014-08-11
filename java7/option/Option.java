package library.java7.option;

import library.function.Consumer;
import library.function.Function;
import library.function.Supplier;

public abstract class Option<A> {
  public abstract A getOrElse(A other);
  public abstract A getOrElse(Supplier<A> other);
  public abstract Option<? super A> orElse(Option<? super A> otherOption);
  public abstract Option<? super A> orElse(Supplier<Option<? super A>> otherOption);
  public abstract boolean isDefined();
  public abstract boolean isEmpty();
  public abstract boolean isDefinedAnd(Function<A, Boolean> func);
  public abstract boolean isEmptyOr(Function<A, Boolean> func);
  public abstract Option<A> filter(Function<A, Boolean> func);
  public abstract Option<A> filterNot(Function<A, Boolean> func);
  public abstract void forEach(Consumer<A> func);
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