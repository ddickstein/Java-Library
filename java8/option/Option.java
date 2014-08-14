// Copyright (c) 2014 Daniel S. Dickstein

package library.java8.option;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;
import library.java8.function.Block;
import static java.util.function.Function.identity;

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

  public Stream<A> stream() {
    return applyOrElse(Stream::of, Stream::empty);
  }

  public static <A> Option<A> wrap(A value) {
    return (value == null) ? new None<A>() : new Some<A>(value);
  }

  public static <A, B> Function<A, Option<B>> wrapFunc(Function<A, B> func) {
    return func.andThen(value -> wrap(value));
  }

  public static <A> Option<A> wrapIf(boolean pred, A value) {
    return pred ? wrap(value) : new None<A>();
  }

  public static <A> Option<A> flatWrap(Optional<A> optional) {
    return optional.map(value -> wrap(value)).orElseGet(() -> new None<A>());
  }

  // Designed to be used with a Stream's collect() method, to achieve the effect of flatMapping over
  // List<Option<A>> to get a List<A> from all the Some values in the original list
  // Invoke with stream()...collect(new Option.Flattener<A>())
  public static class Flattener<A> implements Collector<Option<A>, List<A>, List<A>> {
    public BiConsumer<List<A>, Option<A>> accumulator() {
      return (list, elemOpt) -> elemOpt.doIfDefined(elem -> list.add(elem));
    }

    public BinaryOperator<List<A>> combiner() {
      return (list1, list2) -> { list1.addAll(list2); return list1; };
    }

    public Supplier<List<A>> supplier() {
      return () -> new ArrayList<A>();
    }

    public Function<List<A>, List<A>> finisher() {
      return identity();
    }

    public Set<Collector.Characteristics> characteristics() {
      Set<Collector.Characteristics> charSet = new HashSet<>();
      charSet.add(Collector.Characteristics.CONCURRENT);
      charSet.add(Collector.Characteristics.IDENTITY_FINISH);
      return charSet;
    }
  }
}