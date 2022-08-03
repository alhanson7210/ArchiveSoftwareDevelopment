SimpleJsonWriter
=================================================

For this homework assignment, you will create a class that outputs various collections as "pretty" JSON objects or arrays to file. Use **two spaces** for each level of indentation and use line separators between elements. For example, a "pretty" JSON array looks like:

```json
[
  1,
  2,
  3
]
```

Note that there is no comma after the last element in the array. Objects look like:

```json
{
  "ant": 1,
  "bat": 2,
  "cat": 3
}
```

Note that strings and keys are always in `"` quotation marks, but numbers are not. A nested object looks like:

```json
{
  "ant": [
    1
  ],
  "bat": [
    1,
    2
  ],
  "cat": [
    1,
    2,
    3
  ]
}
```

Use `UTF8` when writing your files. See the Javadoc comments in the template code for additional details.

## Requirements ##

The official name of this homework is `SimpleJsonWriter`. This should be the name of the associated Eclipse Java project and the name used when running the `homework` test script on the lab computers.

See the [Homework Guides](https://usf-cs212-2020.github.io/guides/homework/) for additional details on homework requirements and submission.

## Optional ##

It is possible to pass the tests with a less-than ideal implementation. Focus first on passing the tests by the deadline. Once you have working implementations, try to improve your solution as follows:

  - [ ] Do not use String replacement.

  - [ ] Reuse methods as much as possible. You should be able to use `asArray` within your `asNestedArray` method.

  - [ ] Try to avoid needing extra logic within your loops. For example, avoid testing *every* element to see if it is the last one.

The above does not need to be completed by the homework deadline, but will be required to pass code reviews for similar functionality in the projects.

## Hints ##

Below are some hints that may help with this homework assignment:

  - Eclipse has built-in file comparison functionality. It can show you exactly how your file output differs from the expected output (even if its just a trailing space at the end of a line).

  - If you directly call `write()` an `int` or `Integer`, it will often be seen as a character code. For example, `65` is the character code for `A`. If you want to actually write the digits `65` to file, convert to a `String` object first. For example, try running this:
    ```java
    PrintWriter writer = new PrintWriter(System.out);
    Integer i = 65;
    writer.write(i);
    writer.flush();
    ```

    Compare that output to:
    ```java
    PrintWriter writer = new PrintWriter(System.out);
    Integer i = 65;
    writer.write(i.toString());
    writer.flush();
    ```

    Note this is not a problem for most `print()` and `println()` methods, so it depends on what you use to write to file.

  - The [official JSON documentation](http://www.json.org/) can be a little difficult to parse. There [are](https://developer.mozilla.org/en-US/docs/Learn/JavaScript/Objects/JSON) [many](https://en.wikipedia.org/wiki/JSON) [other](http://www.vogella.com/tutorials/JSON/article.html) [tutorials](https://www.google.com/search?q=json+examples) out there.

You are not required to use these hints in your solution. There may be multiple approaches to solving this homework.
