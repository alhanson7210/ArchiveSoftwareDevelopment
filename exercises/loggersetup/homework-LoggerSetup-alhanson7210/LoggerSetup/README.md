Logger Setup
=================================================

This mini homework assignment requires you to setup log4j2 and learn how to configure the log output. The **only thing** you should do for this homework assignment is setup the `log4j2` user library and create a log4j2 configuration file in the correct location with the correct configuration.

Configuration
-------------------------------------------------

Setup `log4j2` as a user library in Eclipse. Make sure to name the library `log4j2` in all lowercase. Do *not* add the `jar` files directly to the classpath.

Configure the `LoggerSetup` logger  to output `WARN` messages and higher (more severe) to the console and `ALL` messages to a `debug.log` file in the current working directory, and configure the root logger to output `INFO` messages and higher to the console only. You can use the `log4j2.xml` example from lecture as a starting point.

Only output the message and short error message (if appropriate) to the console. The expected console output should look like:

```
wren
egret eagle
Catching finch
ibis
wren
egret eagle
Catching finch
```

Include the sequence number, level (using only 3 letters), class name, method name, thread name, 3 lines from any stack trace (if appropriate), and a newline character (`%n`) in the `debug.log` file. See the `test/debug.log` file for the expected file output. It is also included below:

```
[1 TRA] LoggerSetup.outputMessages main: tucan
[2 DEB] LoggerSetup.outputMessages main: dodo
[3 INF] LoggerSetup.outputMessages main: ibis
[4 WAR] LoggerSetup.outputMessages main: wren
[5 ERR] LoggerSetup.outputMessages main: egret java.lang.Exception: eagle
	at LoggerSetup.outputMessages(LoggerSetup.java:20)
	at LoggerSetup.main(LoggerSetup.java:30)
[6 FAT] LoggerSetup.outputMessages main: Catching java.lang.RuntimeException: finch
	at LoggerSetup.outputMessages(LoggerSetup.java:21)
	at LoggerSetup.main(LoggerSetup.java:30)
```

You should **NOT** modify the `LoggerSetup.java`, `LoggerSetupTest.java`, or `test/debug.log` files. You should only create a **NEW** file in the correct location to configure log4j2.

## Requirements

The official name of this homework is `LoggerSetup`. This should be the name you use for your Eclipse Java project and the name you use when running the homework test script.

See the [Homework Guides](https://usf-cs212-2020.github.io/guides/homework/) for additional details on homework requirements and submission.

Hints
-------------------------------------------------

1. The `log4j2.xml` file in the [lecture examples](https://github.com/usf-cs212-2020/lectures/blob/master/Debugging/src/log4j2.xml) is a good starting point.

1. For configuring the output locations (where to output, file versus console), take a look at the [ConsoleAppender](https://logging.apache.org/log4j/2.x/manual/appenders.html#ConsoleAppender) and [FileAppender](https://logging.apache.org/log4j/2.x/manual/appenders.html#FileAppender) information pages.

1. For configuring the output format (what to output), I recommend you take a look at the [PatternLayout](https://logging.apache.org/log4j/2.x/manual/layouts.html#PatternLayout) information page. It includes all of the possible patterns, like `class`, `date`, `throwable`, `file`, `location`, `line`, `message`, `method`, `n`, `level`, `sequenceNumber`, `threadId`, and `threadName` (you will only use some of these).

**Do NOT overwrite the `test/debug.log` file. You should configure log4j2 to write to the path `debug.log` instead.**
