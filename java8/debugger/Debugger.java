// Copyright (c) 2014 Daniel S. Dickstein

package library.java8.debugger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import library.java8.lists.Lists;
import library.java8.option.Option;
import library.java8.option.Attempt;
import library.java8.strings.Strings;
import library.java8.tuple.Tuple2;

public class Debugger {
  private Map<String, List<String>> encounterMap;
  private List<String> encounters;

  public Debugger() {
    encounterMap = new LinkedHashMap<>();
    encounters = new ArrayList<>();
  }

  public Object log(Object unsafeObj, String unsafeName) {
    Option.wrap(unsafeName).doIfDefined(name -> {
      String value = Option.wrap(unsafeObj).map(obj -> obj.toString()).getOrElse("null");
      encounterMap.entrySet().stream().forEach(entry -> {
        List<String> list = entry.getValue();
        list.add(entry.getKey().equals(name) ? value : list.get(list.size() - 1));
      });
      if (!encounterMap.containsKey(name)) {
        List<String> list = new ArrayList<>();
        Lists.fill(list, "", encounters.size());
        list.add(value);
        encounterMap.put(name, list);
      }
      encounters.add(name);
    });
    return unsafeObj;
  }

  public void report() {
    List<String> headers = new ArrayList<>();
    headers.add("encounter");
    headers.addAll(encounterMap.keySet());
    List<List<String>> contents = new ArrayList<List<String>>(encounterMap.values());
    contents.add(0, encounters);
    Object[][] contentsArray = Lists.transpose(contents).stream().map(row -> row.toArray()).toArray(Object[][]::new);
    List<List<Tuple2<String, Integer>>> tableInfo = getTableInfo(headers.toArray(), contentsArray);
    DebugTable debugTable = new DebugTable(Attempt.get(tableInfo, 0).getOrElse(Collections.emptyList()));
    Attempt subListAttempt = new Attempt();
    subListAttempt.anticipate(IndexOutOfBoundsException.class);
    debugTable.printTable(subListAttempt.attempt(() ->
      tableInfo.subList(1, tableInfo.size())).getOrElse(Collections.emptyList()));
    encounterMap.clear();
    encounters.clear();
  }

  public static <A> A debug(A unsafeObj) {
    Option.wrap(unsafeObj).doIfDefined(obj -> {
      List<Tuple2<String, Object>> info = getDebugInfo(obj);
      Object[] headerObjects = info.stream().map(tup -> tup._1).toArray();
      Object[][] valueObjects = { info.stream().map(tup -> tup._2).toArray() };
      List<List<Tuple2<String, Integer>>> tableInfo = getTableInfo(headerObjects, valueObjects);
      List<Tuple2<String, Integer>> header = Attempt.get(tableInfo, 0).getOrElse(Collections.emptyList());
      List<List<Tuple2<String, Integer>>> values = new ArrayList<>();
      values.add(Attempt.get(tableInfo, 1).getOrElse(Collections.emptyList()));
      DebugTable debugTable = new DebugTable(header);
      debugTable.printTable(values);
    }).doIfEmpty(() -> System.out.println("null"));
    return unsafeObj;
  }

  public static <A> List<A> debugList(List<A> unsafeList) {
    Option.wrap(unsafeList).doIfDefined(list -> {
      List<List<Tuple2<String, Object>>> infoMatrix = new ArrayList<>();
      for (int i = 0; i < list.size(); i++) {
        List<Tuple2<String, Object>> infoRow = new ArrayList<>();
        infoRow.add(new Tuple2<String, Object>("", i));
        infoRow.addAll(getDebugInfo(list.get(i)));
        infoMatrix.add(infoRow);
      }
      Object[] headerObjects = Attempt.get(infoMatrix, 0)
        .map(info -> info.stream().map(tup -> tup._1).toArray())
        .getOrElse(new Object[0]);
      Object[][] valueObjects = infoMatrix.stream()
        .map(info -> info.stream().map(tup -> tup._2).toArray())
        .toArray(Object[][]::new);
      List<List<Tuple2<String, Integer>>> tableInfo = getTableInfo(headerObjects, valueObjects);
      List<Tuple2<String, Integer>> header = Attempt.get(tableInfo, 0).getOrElse(Collections.emptyList());
      Attempt subListAttempt = new Attempt();
      subListAttempt.anticipate(IndexOutOfBoundsException.class);
      DebugTable debugTable = new DebugTable(header);
      debugTable.printTable(subListAttempt.attempt(() ->
        tableInfo.subList(1, tableInfo.size())).getOrElse(Collections.emptyList()));
    }).doIfEmpty(() -> System.out.println("null"));
    return unsafeList;
  }

  public static <A> A[] debugArray(A[] unsafeArray) {
    Option.wrap(unsafeArray)
      .doIfDefined(array -> debugList(Arrays.asList(array)))
      .doIfEmpty(() -> System.out.println("null"));
    return unsafeArray;
  }

  private static final Class<?>[] valueClasses = {
    Byte.class, Short.class, Integer.class, Long.class, Float.class,
    Double.class, Character.class, Boolean.class, String.class
  };

  private static <A> List<Tuple2<String, Object>> getDebugInfo(A unsafeObj) {
    return Option.wrap(unsafeObj).flatMap(obj -> {
      if (library.java8.arrays.Arrays.contains(valueClasses, obj.getClass())) {
        List<Tuple2<String, Object>> list = new ArrayList<>();
        list.add(new Tuple2<String, Object>("value", obj));
        return Option.wrap(list);
      } else {
        Attempt securityAttempt = new Attempt();
        securityAttempt.anticipate(SecurityException.class);
        return securityAttempt.attempt(() -> obj.getClass().getDeclaredFields()).flatMap(fields -> {
          return securityAttempt.attempt(() -> obj.getClass().getDeclaredMethods()).map(methods -> {
            Field[] nonStaticFields = Arrays.stream(fields)
              .filter(f -> !Modifier.isStatic(f.getModifiers()))
              .toArray(Field[]::new);
            Field[] annotatedFields = Arrays.stream(fields)
              .filter(f -> f.getAnnotation(Debuggable.class) != null)
              .toArray(Field[]::new);
            Method[] annotatedMethods = Arrays.stream(methods)
              .filter(m -> m.getAnnotation(Debuggable.class) != null)
              .filter(m -> m.getParameterTypes().length == 0)
              .toArray(Method[]::new);
            boolean annotationsPresent = annotatedFields.length + annotatedMethods.length > 0;
            Field[] fieldsToDebug = annotationsPresent ? annotatedFields : nonStaticFields;
            Method[] methodsToDebug = annotationsPresent ? annotatedMethods : new Method[0];
            Attempt getAttempt = new Attempt();
            getAttempt.anticipate(IllegalAccessException.class);
            getAttempt.anticipate(IllegalArgumentException.class);
            getAttempt.anticipate(InvocationTargetException.class);
            Stream<Tuple2<String, Object>> fieldNamesAndValues = Arrays.stream(fieldsToDebug).flatMap(field -> {
              return securityAttempt.attempt(() -> field.setAccessible(true)).flatMap(_ignored_ -> {
                return Option.wrap(field.getAnnotation(Debuggable.class)).flatMap(debuggable -> {
                  String name = debuggable.name().equals("") ? field.getName() : debuggable.name();
                  return getAttempt.attempt(() -> new Tuple2<String, Object>(name, field.get(obj)));
                }).orElse(() -> getAttempt.attempt(() -> new Tuple2<String, Object>(field.getName(), field.get(obj))));
              }).stream();
            });
            Stream<Tuple2<String, Object>> methodNamesAndValues = Arrays.stream(methodsToDebug).flatMap(method -> {
              return securityAttempt.attempt(() -> method.setAccessible(true)).flatMap(_ignored_ -> {
                return Option.wrap(method.getAnnotation(Debuggable.class)).flatMap(debuggable -> {
                  String name = debuggable.name().equals("") ? method.getName() : debuggable.name();
                  return getAttempt.attempt(() -> new Tuple2<String, Object>(name, method.invoke(obj)));
                });
              }).stream();
            });
            return Stream.concat(fieldNamesAndValues, methodNamesAndValues).collect(Collectors.toList());
          });
        });
      }
    }).getOrElse(Collections.emptyList());
  }

  private static List<List<Tuple2<String, Integer>>> getTableInfo(Object[] headers, Object[][] values) {
    List<List<Object>> matrix =
      Arrays.asList(values).stream().map(row -> Arrays.asList(row)).collect(Collectors.toList());
    matrix.add(0, Arrays.asList(headers));
    int numColumns = matrix.stream().map(row -> row.size()).reduce(0, Math::max);
    int[] columnWidths = new int[numColumns];
    for (int r = 0; r < matrix.size(); r++) {
      for (int c = 0; c < matrix.get(r).size(); c++) {
        int cellWidth = Option.wrap(matrix.get(r).get(c)).applyOrElse(cell -> cell.toString().length(), () -> 0);
        columnWidths[c] = Math.max(columnWidths[c], cellWidth);
      }
    }
    List<List<Tuple2<String, Integer>>> tableInfo = new ArrayList<>();
    for (int r = 0; r < matrix.size(); r++) {
      List<Tuple2<String, Integer>> rowInfo = new ArrayList<>();
      for (int c = 0; c < matrix.get(r).size(); c++) {
        int columnWidth = columnWidths[c];
        rowInfo.add(Option.wrap(matrix.get(r).get(c))
          .map(cell -> new Tuple2<String, Integer>(cell.toString(), columnWidth))
          .getOrElse(new Tuple2<String, Integer>("", columnWidth)));
      }
      tableInfo.add(rowInfo);
    }
    return tableInfo;
  }
}

class DebugTable {
  private List<Integer> columnWidths;
  private List<String> headers;

  public DebugTable(List<Tuple2<String, Integer>> setupData) {
    Lists.unzip(setupData).forEach((headers, columnWidths) -> {
      this.headers = headers;
      this.columnWidths = columnWidths;
    });
  }

  private void printBorder() {
    String[] columnBorders = columnWidths.stream().map(width -> Strings.fill("-", width + 2)).toArray(String[]::new);
    System.out.println("+" + Strings.join(columnBorders, "+") + "+");
  }

  private void printRow(List<String> row) {
    Stream<String> missingColumns = IntStream.range(row.size(), headers.size())
      .mapToObj(index -> Strings.fill(" ", columnWidths.get(index) + 2));
    Stream<String> presentColumns = Lists.zipWithIndex(row)
      .stream()
      .limit(headers.size())
      .map(tup -> tup.map((elem, index) ->
        " " + elem + Strings.fill(" ", columnWidths.get(index) - elem.length() + 1)));
    String[] rowContents = Stream.concat(presentColumns, missingColumns).toArray(String[]::new);
    System.out.println("|" + Strings.join(rowContents, "|") + "|");
  }

  public void printTable(List<List<Tuple2<String, Integer>>> contents) {
    printBorder();
    printRow(headers);
    printBorder();
    contents.stream().forEach(row -> printRow(Lists.unzip(row)._1));
    printBorder();
  }
}