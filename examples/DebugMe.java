package library.examples;

class ExampleObject {
  private String foo;
  private int bar;

  public ExampleObject(String foo, int bar) {
    this.foo = foo;
    this.bar = bar;
  }
}

public class DebugMe {
  public static void main(String[] args) throws Exception {
    library.debugger.Debugger.debug(new ExampleObject("one two three", 123));
  }
}