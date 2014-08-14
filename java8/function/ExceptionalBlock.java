// Copyright (c) 2014 Daniel S. Dickstein

package library.java8.function;

@FunctionalInterface
public interface ExceptionalBlock {
  public void execute() throws Exception;
}