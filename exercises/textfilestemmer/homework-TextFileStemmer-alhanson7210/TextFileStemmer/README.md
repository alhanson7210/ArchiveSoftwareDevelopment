TextFileStemmer
=================================================

For this homework assignment, you will create a class that is able to clean and parse text into stemmed words. Use `UTF8` when writing your files. See the Javadoc comments in the template code for additional details.

## Requirements ##

The official name of this homework is `TextFileStemmer`. This should be the name you use for your Eclipse Java project and the name you use when running the homework test script.

See the [Homework Guides](https://usf-cs212-2020.github.io/guides/homework/) for additional details on homework requirements and submission.

## Hints ##

Below are some hints that may help with this homework assignment:

  - You need to have the third-party [Apache OpenNLP](http://opennlp.apache.org/) library setup as a user library in Eclipse for this assignment. See the [Adding User Libraries to Eclipse](https://usf-cs212-2020.github.io/guides/eclipse/adding-user-libraries-in-eclipse.html) guide for details.

  - The methods for cleaning and parsing text are already provided in the `TextParser` class. You can use this class for your homework and project.

  - You should use try-with-resources and buffered readers and writers. See the [Files and Exceptions](https://github.com/usf-cs212-2020/lectures/tree/master/Files%20and%20Exceptions) examples from lecture.
  
  - Look for opportunities to reduce duplicate code. For example, you could create a new helper method that is reused in multiple other methods.

You are not required to use these hints in your solution. There may be multiple approaches to solving this homework.