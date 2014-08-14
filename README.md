## Introduction to my extended Java Library

This repository is a collection of miscellaneous tools I find / have found useful, and I will continue to update it with new utilities as I projects require.  Feel free to use any or all of them.  The repository is divided into Java SE 7 and Java SE 8 tools - the Java SE 7 library should mirror the Java SE 8 one but may fall behind it because I develop mostly in Java SE 8 and keeping the two in sync is time consuming.  If you would like to add your own tools to this collection, please submit a pull request.

The remainder of this README is an overview of the various tools available.

## Options

Java programs are rife with `== null` checks in an attempt to ward off the omnipresent NullPointerExceptions, but programmers are left without the guarantee that their programs are safe.  Many functional programming languages have done away with nulls, instead preferring to pass around Option wrappers for values, where a Some(value) indicates the value's presence and a None indicates its absence.  Java SE 8 introduces the [Optional](http://docs.oracle.com/javase/8/docs/api/java/util/Optional.html) class in this spirit.  I found its functionality somewhat limiting, so I wrote [my own implementation](java8/option/Option.java).

In my experience there are two approaches to defensive programming with Options.  The first is to wrap all values that your functions take as input before you begin working with them - this is analagous to the `== null` checks at the beginning of many methods that you currently see in Java, albeit possibly more elegant.  The second approach is to simply disavow the usage of nulls in your code base entirely, and be diligent about wrapping all sources of null values before working with them.  This means that if a function accepts a value of type X, you assume it cannot handle the case of `value == null` and you wrap the value before invoking your function.  In the utilities for this library I used the former approach, since I want the tools to integrate into pre-existing code bases that may not be dilligent about null checking at the source.  However, if you are starting a fresh code base, it may be cleaner to use the second approach.

Options also work nicely as optional parameters - for example, if you want your function to optionally accept an integer, rather than overloading it you can accept an `Option<Integer>` where Some(value) indicates the presence of the optional parameter and None indicates its absence.

I marked the constructors for `Some` and `None` as `private` because I want to prevent the creation of `Some(null)`, which I think is a fundamentally confusing concept and can easily lead to mistakes.  Instead, if you want `Some(value)`, call `Option.wrap(value)`.  If you want `None`, call `Option.wrap(null)`.

## Attempts

To give options more punch, I added [Attempts](java8/option/Attempt.java), which are objects that handle exceptions and wrap the results as options.  An successful attempt yields `Some(result)` and failure yields `None`.  This idea is one I first saw implemented in Scala at Foursquare (although the implementation varies considerably). Here is the basic syntax:

    Attempt attempt = new Attempt();
    attempt.anticipate(/* class of exception to catch */); // repeat for as many exceptions as you like
    Option<T> result = attempt.attempt(() -> /* code to try */);

The above 3 lines will entirely replace the try-catch block that you would have otherwise needed to write.  Moreover, you can use the same attempt object multiple times if you want to anticipate the same errors in multiple places. Not bad, huh?

## Tuples

I added [Tuple2](java8/tuple/Tuple2.java) and [Tuple3](java8/tuple/Tuple3.java) classes for representing tuples of 2 and 3 elements respectively.  I also added zipping and unzipping functions to the Lists section of the library.  As per tuple tradition, they are indexed at 1.

    Tuple2<String, Integer> myTuple = new Tuple2<>("hi", 5);
    String str = myTuple._1;
    int x = myTuple._2;

## Safe Scanner

[SafeScanner](java8/io/SafeScanner.java) is a `Scanner` wrapper that is primarily designed for keyboard I/O but could be used for other input streams.  It is entirely line-oriented, which means that both `nextInt` and `nextDouble` consume an entire line of input and attempt to parse it as an `int` or `double`.  This may not be ideal for file reading, but I think it results in a more intuitive experience for an application that interacts via the console w/ a user.  I also use the `Attempt` utility to catch any I/O exceptions.  Finally, there are special `prompt` methods for repeatedly prompting the user to enter a certain kind of value until a valid entry is provided.

## Debugger

By far the coolest tool in the suite, the [Debugger](java8/debugger/Debugger.java) is designed for situations where you don't have a fully functional debugger available and you are relying on print-debugging.  The `Debugger` gives nice clean print outs of objects in your code, and also lets you customize exactly what the debuggable output should be for your own classes.  Finally, the `Debugger` also comes with special functionality for debugging logic flow.  You can mark certain listening points in your code, and the `Debugger` will print out a table of all the listening points and how their values changed over time.

The `Debugger` class comes with 3 class methods: `debug`, `debugList`, and `debugArray`.  These should be used to debug objects in your code (`debugList` and `debugArray` will provide more helpful information than `debug` for lists and arrays respectively).  For a given object a table will be printed with the values of all the `@Debuggable` fields and methods in that class.  In the case where no `@Debuggable` annotations appear, we assume that you want to debug all the instance fields and none of the methods or class fields.  Methods can only be debugged if they take no parameters as input.

`@Debuggable` should primarily be used on a method if you want to affect the representation of data contained in the raw fields.  For example, on a `User` object you might have a `firstName` and `lastName` field, but rather than marking those as `@Debuggable` you can define a method `@Debuggable public String fullName() { return firstName + " " + lastName; }` to make the debugging output easier to read.

To use the logic flow analysis feature of the debugger, you must create a new instance `Debugger myDebugger = new Debugger()` and then invoke `myDebugger.log(value, name)`.  Finally, after all the logging points, you can write `myDebugger.report()` and a table will be printed with a column for each logging point and a record of how the value changed over time, relative to the other logging points.

For a full example of this utility see [DebuggerExample.java](java8/examples/DebuggerExample.java).
