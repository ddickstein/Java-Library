// Copyright (c) 2014 Daniel S. Dickstein

package library.java8.examples;

import library.java8.debugger.Debugger;
import library.java8.debugger.Debuggable;

class ExampleObject {
  @Debuggable private String foo;
  @Debuggable private int bar;

  @Debuggable private String baz() {
    return foo + "(" + bar + ")";
  }

  public ExampleObject(String foo, int bar) {
    this.foo = foo;
    this.bar = bar;
  }
}

public class DebuggerExample {
  public static void main(String[] args) throws Exception {
    ExampleObject[] objects = {
      new ExampleObject("one two three", 123),
      new ExampleObject("four five six", 456),
      new ExampleObject("seven eight nine", 789)
    };
    Debugger.debugArray(objects);

    Debugger debugger = new Debugger();
    for (int row = 0; row < 3; row++) {
      debugger.log(row, "row");
      for (int col = 0; col < 3; col++) {
        debugger.log(col, "col");
        debugger.log(row * col, "prod");
      }
    }
    debugger.report();
  }
}