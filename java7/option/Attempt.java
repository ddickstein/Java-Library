package library.java7.option;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import library.java7.function.Supplier;

public class Attempt {
  private List<Class<? extends Exception>> anticipatedExceptions;
  
  public Attempt() {
    anticipatedExceptions = new ArrayList<>();  
  }

  public void anticipate(Class<? extends Exception> exception) {
    anticipatedExceptions.add(exception);
  }

  public <A> Option<A> attempt(Supplier<A> func) {
    try {
      return new Some<A>(func.get());
    } catch (Exception e) {
      for (Class<? extends Exception> anticipatedException : anticipatedExceptions) {
        if (anticipatedException == e.getClass())
          return new None<A>();
      }
      throw e;
    }
  }

  public static <A> Option<A> get(A[] arr, int index) {
    Attempt attempt = new Attempt();
    attempt.anticipate(ArrayIndexOutOfBoundsException.class);
    return attempt.attempt(new Supplier<A>() {
      @Override
      public A get() { return arr[index]; }
    });
  }

  public static <A> Option<A> get(List<A> list, int index) {
    Attempt attempt = new Attempt();
    attempt.anticipate(IndexOutOfBoundsException.class);
    return attempt.attempt(new Supplier<A>() {
      @Override
      public A get() { return list.get(index); }
    });
  }

  public static <A, B> Option<B> get(Map<A, B> map, A key) {
    return Option.wrap(map.get(key));
  }

  public static <A, B> Option<B> cast(A value, Class<B> toClass) {
    Attempt attempt = new Attempt();
    attempt.anticipate(ClassCastException.class);
    return attempt.attempt(new Supplier<B>() {
      @Override
      public B get() { return toClass.cast(value); }
    });
  }
}