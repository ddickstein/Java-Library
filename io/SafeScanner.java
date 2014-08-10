package library.io;

import java.util.Scanner;
import java.util.function.Function;
import library.option.Option;
import library.option.Attempt;

public class SafeScanner {
  private Scanner scanner;

  public SafeScanner(Scanner scanner) {
    this.scanner = scanner;
  }

  public Option<Integer> nextInt() {
    Attempt attempt = new Attempt();
    attempt.anticipate(NumberFormatException.class);
    return attempt.attempt(() -> Integer.parseInt(scanner.nextLine()));
  }

  public Option<Double> nextDouble() {
    Attempt attempt = new Attempt();
    attempt.anticipate(NumberFormatException.class);
    return attempt.attempt(() -> Double.parseDouble(scanner.nextLine()));
  }

  public String nextLine() {
    return scanner.nextLine();
  }

  public int promptInt() {
    return promptInt("Enter an integer: ");
  }

  public int promptInt(String prompt) {
    System.out.print(prompt);
    return nextInt().getOrElse(() -> promptInt(prompt));
  }

  public double promptDouble() {
    return promptDouble("Enter a number: ");
  }

  public double promptDouble(String prompt) {
    System.out.print(prompt);
    return nextDouble().getOrElse(() -> promptDouble(prompt));
  }

  public <A> A prompt(String prompt, Function<String, A> conversion) {
    return prompt(prompt, conversion, (value) -> true);
  }

  public <A> A prompt(String prompt, Function<String, A> conversion, Function<A, Boolean> constraint) {
    System.out.print(prompt);
    Attempt attempt = new Attempt();
    attempt.anticipate(ClassCastException.class);
    attempt.anticipate(NumberFormatException.class);

    return attempt.attempt(() -> conversion.apply(nextLine()))
      .filter(constraint)
      .getOrElse(() -> prompt(prompt, conversion, constraint));
  }
}