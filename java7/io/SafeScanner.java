// Copyright (c) 2014 Daniel S. Dickstein

package library.java7.io;

import java.util.Scanner;
import library.java7.function.ExceptionalSupplier;
import library.java7.function.Supplier;
import library.java7.function.Function;
import library.java7.option.Option;
import library.java7.option.Attempt;

public class SafeScanner {
  private Scanner scanner;

  public SafeScanner(Scanner scanner) {
    this.scanner = scanner;
  }

  public Option<Integer> nextInt() {
    Attempt attempt = new Attempt();
    attempt.anticipate(NumberFormatException.class);
    return attempt.attempt(new ExceptionalSupplier<Integer>() {
      @Override
      public Integer get() {
        return Integer.parseInt(scanner.nextLine());
      }
    });
  }

  public Option<Double> nextDouble() {
    Attempt attempt = new Attempt();
    attempt.anticipate(NumberFormatException.class);
    return attempt.attempt(new ExceptionalSupplier<Double>() {
      @Override
      public Double get() {
        return Double.parseDouble(scanner.nextLine());
      }
    });
  }

  public String nextLine() {
    return scanner.nextLine();
  }

  public int promptInt() {
    return promptInt("Enter an integer: ");
  }

  public int promptInt(String prompt) {
    return prompt(prompt, new Function<String, Integer>() {
      @Override
      public Integer apply(String text) {
        return Integer.parseInt(text);
      }
    });
  }

  public double promptDouble() {
    return promptDouble("Enter a number: ");
  }

  public double promptDouble(String prompt) {
    return prompt(prompt, new Function<String, Double>() {
      @Override
      public Double apply(String text) {
        return Double.parseDouble(text);
      }
    });
  }

  public <A> A prompt(String prompt, Function<String, A> conversion) {
    return prompt(prompt, conversion, new Function<A, Boolean>() {
      @Override
      public Boolean apply(A value) { return true; }
    });
  }

  public <A> A prompt(String prompt, Function<String, A> conversion, Function<A, Boolean> constraint) {
    System.out.print(prompt);
    Attempt attempt = new Attempt();
    attempt.anticipate(ClassCastException.class);
    attempt.anticipate(NumberFormatException.class);
    return attempt.attempt(new ExceptionalSupplier<A>() {
      @Override
      public A get() {
        return conversion.apply(nextLine());
      }
    }).filter(constraint).getOrElse(new Supplier<A>() {
      @Override
      public A get() {
        return prompt(prompt, conversion, constraint);
      }
    });
  }
}