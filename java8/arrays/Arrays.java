// Copyright (c) 2014 Daniel S. Dickstein

package library.java8.arrays;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import library.java8.lists.Lists;
import library.java8.option.Attempt;
import library.java8.option.Option;
import static java.util.function.Function.identity;

import library.java8.debugger.Debugger;

public class Arrays {
  public static <A> boolean contains(A[] unsafeArray, A elemToFind) {
    return Option.wrap(unsafeArray).isDefinedAnd(array -> {
      for (A elem : array) {
        if (elem.equals(elemToFind))
          return true;
      }
      return false;
    });
  }

  public static boolean isEmpty(Object unsafeObj) {
    return Option.wrap(unsafeObj).isEmptyOr(obj -> obj.getClass().isArray() &&
      Attempt.cast(obj, new Object[0].getClass()).isDefinedAnd(arr ->
        java.util.Arrays.stream(arr).allMatch(Arrays::isEmpty)));
  }

  @SuppressWarnings("unchecked") // we will catch ClassCastExceptions (if any) at runtime
  public static <A> Option<A[][]> transpose(A[][] unsafeMatrix) {
    return Option.wrap(unsafeMatrix)
      .filter(matrix -> !isEmpty(matrix))
      .flatMap(matrix -> {
        Attempt castAttempt = new Attempt();
        castAttempt.anticipate(ClassCastException.class);
        return castAttempt.attempt(() -> (Class<A[][]>)(matrix.getClass())).flatMap(matClass -> {
          return castAttempt.attempt(() -> (Class<A[]>)(matrix[0].getClass())).map(rowClass -> {
            List<A[]> rowList = Lists.transpose(asListMatrix(matrix))
              .stream()
              .map(row -> java.util.Arrays.copyOf(row.toArray(), row.size(), rowClass))
              .collect(Collectors.toList());
            return java.util.Arrays.copyOf(rowList.toArray(), rowList.size(), matClass);
          });
        });
      });
  }

  public static <A> List<List<A>> asListMatrix(A[][] unsafeMatrix) {
    return Option.wrap(unsafeMatrix)
      .map(matrix -> java.util.Arrays.stream(unsafeMatrix).map(row -> asList(row)).collect(Collectors.toList()))
      .getOrElse(Collections.emptyList());
  }

  public static <A> List<A> asList(A[] unsafeArr) {
    return Option.wrap(unsafeArr).map(arr -> java.util.Arrays.asList(arr)).getOrElse(Collections.emptyList());
  }
}