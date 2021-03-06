// Copyright (c) 2014 Daniel S. Dickstein

package library.java7.option;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import library.java7.function.ExceptionalBlock;
import library.java7.function.ExceptionalSupplier;

public class Attempt {
  private List<Class<? extends Exception>> anticipatedExceptions;
  
  public Attempt() {
    anticipatedExceptions = new ArrayList<>();  
  }

  public void anticipate(Class<? extends Exception> exception) {
    anticipatedExceptions.add(exception);
  }

  public <A> Option<A> attempt(ExceptionalSupplier<A> func) {
    try {
      return Option.wrap(func.get());
    } catch (Exception e) {
      for (Class<? extends Exception> anticipatedException : anticipatedExceptions) {
        if (anticipatedException == e.getClass())
          return new None<A>();
      }
      throw new AttemptException(e);
    }
  }

  public Option<Boolean> attempt(ExceptionalBlock block) {
    return attempt(new ExceptionalSupplier<Boolean>() {
      @Override
      public Boolean get() throws Exception { block.execute(); return true; }
    });
  }

  public static <A> Option<A> get(A[] arr, int index) {
    Attempt attempt = new Attempt();
    attempt.anticipate(ArrayIndexOutOfBoundsException.class);
    return attempt.attempt(new ExceptionalSupplier<A>() {
      @Override
      public A get() { return arr[index]; }
    });
  }

  public static <A> Option<A> get(List<A> list, int index) {
    Attempt attempt = new Attempt();
    attempt.anticipate(IndexOutOfBoundsException.class);
    return attempt.attempt(new ExceptionalSupplier<A>() {
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
    return attempt.attempt(new ExceptionalSupplier<B>() {
      @Override
      public B get() { return toClass.cast(value); }
    });
  }
}

class AttemptException extends RuntimeException {
  private static final long serialVersionUID = 1L;
  public AttemptException(Exception e) {
    super(e);
  }
}