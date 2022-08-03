TextFileFinder
=================================================

For this homework assignment, you will create a class that finds all the valid files (not directories) that end in the `.txt` or `.text` extension case-insensitive. You must use streams and lambda functions for this assignment. You cannot use the `File` class in Java! See the Javadoc comments in the template code for additional details.

## Requirements ##

The official name of this homework is `TextFileFinder`. This should be the name you use for your Eclipse Java project and the name you use when running the homework test script.

See the [Homework Guides](https://usf-cs212-2020.github.io/guides/homework/) for additional details on homework requirements and submission.

## Hints ##

Below are some hints that may help with this homework assignment:

  - If you are more comfortable with anonymous classes and the `File` class, start there. You will fail some of the tests, but you will be able to see whether you are finding the expected text files. Then, convert one thing at a time to use what is required.
  
  - Check out the [`Files`](https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/nio/file/Files.html) class in Java. The `walk(...)` and `find(...)` methods will be helpful here.
  
  - Use the [`FileVisitOption.FOLLOW_LINKS`](https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/nio/file/FileVisitOption.html#FOLLOW_LINKS) option when using the `walk(...)` or `find(...)` method. (This is important mostly for the project later.)

You are not required to use these hints in your solution. There may be multiple approaches to solving this homework.