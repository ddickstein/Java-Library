## Options for Java

Java programs are rife with `== null` checks in an attempt to ward off the omnipresent NullPointerExceptions, but can never quite vanquish the foe.  A single missing check can cause the walls to come tumbling down.  Java 8 introduces the [Optional](http://docs.oracle.com/javase/8/docs/api/java/util/Optional.html) class to resolve this, which is definitely a step in the right direction.  I found it a bit clunky to use, so I wrote my own implementation.

I also added an `Attempt.java` class that allows programmers to integrate options into error handling.  Here is the basic syntax:

    Attempt attempt = new Attempt();
    attempt.anticipate(/* class of exception to catch */); // repeat for as many exceptions as you like
    Option<T> result = attempt.attempt(() -> /* code to try */);
