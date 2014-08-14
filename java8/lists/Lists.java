// Copyright (c) 2014 Daniel S. Dickstein

package library.java8.lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import library.java8.option.Attempt;
import library.java8.option.Option;
import library.java8.tuple.Tuple2;
import library.java8.tuple.Tuple3;

public class Lists {
  public static <A> void forceAdd(List<A> unsafeList, A elem, int index) {
    Option.wrapIf(index >= 0, unsafeList).doIfDefined(list -> {
      fill(list, null, index);
      list.add(index, elem);
    });
  }

  public static <A> void addColumn(List<List<A>> unsafeMatrix, List<A> unsafeColumn, int index) {
    Option.wrapIf(index >= 0, unsafeMatrix).doIfDefined(matrix -> {
      Option.wrap(unsafeColumn).doIfDefined(column -> {
        matrix.stream().limit(column.size()).forEach(row ->
          Attempt.get(column, index).doIfDefined(elem -> forceAdd(row, elem, index)));
      });
    });
  }

  public static <A> void fill(List<A> unsafeList, A value, int capacity) {
    Option.wrap(unsafeList).doIfDefined(list -> {
      for (int i = list.size(); i < capacity; i++) {
        list.add(value);
      }
    });
  }

  public static <A> List<List<A>> transpose(List<List<A>> unsafeMatrix) {
    return Option.wrap(unsafeMatrix).filter(matrix -> matrix.size() > 0).map(matrix -> {
      List<List<A>> transposed = IntStream.range(0, matrix.stream().map(row -> row.size()).reduce(0, Math::max))
        .mapToObj(_ignored_ -> new ArrayList<A>())
        .collect(Collectors.toList());
      matrix.stream().forEach(row -> zipWithIndex(row)
        .stream()
        .forEach(tup -> tup.forEach((elem, index) -> transposed.get(index).add(elem)))
      );
      return transposed;
    }).getOrElse(Collections.emptyList());
  }

  public static <A, B> List<Tuple2<A, B>> zip(List<A> unsafeList1, List<B> unsafeList2) {
    return Option.wrap(unsafeList1).flatMap(list1 -> Option.wrap(unsafeList2).map(list2 -> {
      List<Tuple2<A, B>> zippedList = new ArrayList<>();
      int length = Math.min(list1.size(), list2.size());
      for (int i = 0; i < length; i++) {
        zippedList.add(new Tuple2<A, B>(list1.get(i), list2.get(i)));
      }
      return zippedList;
    })).getOrElse(Collections.emptyList());
  }

  public static <A, B, C> List<Tuple3<A, B, C>> zip3(List<A> unsafeList1, List<B> unsafeList2, List<C> unsafeList3) {
    return Option.wrap(unsafeList1).flatMap(list1 ->
      Option.wrap(unsafeList2).flatMap(list2 -> 
        Option.wrap(unsafeList3).map(list3 -> {
          List<Tuple3<A, B, C>> zippedList = new ArrayList<>();
          int length = Math.min(Math.min(list1.size(), list2.size()), list3.size());
          for (int i = 0; i < length; i++) {
            zippedList.add(new Tuple3<A, B, C>(list1.get(i), list2.get(i), list3.get(i)));
          }
          return zippedList;
        })
      )
    ).getOrElse(Collections.emptyList());
  }

  public static <A, B> Tuple2<List<A>, List<B>> unzip(List<Tuple2<A, B>> unsafeList) {
    return Option.wrap(unsafeList).map(list -> {
      List<A> list1 = new ArrayList<>();
      List<B> list2 = new ArrayList<>();
      for (Tuple2<A, B> tuple2 : list) {
        list1.add(tuple2._1);
        list2.add(tuple2._2);
      }
      return new Tuple2<List<A>, List<B>>(list1, list2);
    }).getOrElse(new Tuple2<List<A>, List<B>>(Collections.emptyList(), Collections.emptyList()));
  }

  public static <A, B, C> Tuple3<List<A>, List<B>, List<C>> unzip3(List<Tuple3<A, B, C>> unsafeList) {
    return Option.wrap(unsafeList).map(list -> {
      List<A> list1 = new ArrayList<>();
      List<B> list2 = new ArrayList<>();
      List<C> list3 = new ArrayList<>();
      for (Tuple3<A, B, C> tuple3 : list) {
        list1.add(tuple3._1);
        list2.add(tuple3._2);
        list3.add(tuple3._3);
      }
      return new Tuple3<List<A>, List<B>, List<C>>(list1, list2, list3);
    }).getOrElse(new Tuple3<List<A>, List<B>, List<C>>(
      Collections.emptyList(),
      Collections.emptyList(),
      Collections.emptyList()
    ));
  }

  public static <A> List<Tuple2<A, Integer>> zipWithIndex(List<A> unsafeList) {
    return Option.wrap(unsafeList).map(list ->
      zip(list, IntStream.range(0, list.size()).boxed().collect(Collectors.toList()))
    ).getOrElse(Collections.emptyList());
  }
}