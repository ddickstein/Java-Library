package library.debugger;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Stream;
import library.option.Attempt;
import library.tuple.Tuple2;

public class Debugger {
  public static <A> void debug(A obj) {
    if (obj == null) { // doing a standard null check b/c we don't want to make assumptions about null safety here
      System.out.println("null");
    } else {
      Attempt attempt = new Attempt();
      attempt.anticipate(SecurityException.class);
      attempt.attempt(() -> obj.getClass().getDeclaredFields()).forEach((fields) -> {
        Attempt attempt2 = new Attempt();
        attempt2.anticipate(IllegalAccessException.class);
        attempt2.anticipate(IllegalArgumentException.class);
        Arrays.stream(fields)
          .flatMap((field) -> attempt2.attempt(() -> { field.setAccessible(true); return field.get(obj); })
            .map((value) -> new Tuple2<String, Object>(field.getName(), value)).toStream())
          .forEach((tuple) -> System.out.println(tuple._1 + ": " + tuple._2));
      });
    }
  }
}