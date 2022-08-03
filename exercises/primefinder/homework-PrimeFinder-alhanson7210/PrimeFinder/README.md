# Prime Finder

For this homework assignment, you must:

  - Add a `pending` variable to the `WorkQueue` class to track unfinished work and implement a `finish()` method that waits until there is no more pending work. Use this new functionality in `PrimeFinder`; there should *not* be a `pending` variable in the `PrimeFinder` class!  
  

  - Implement the multithreaded `findPrimes(int, int, int)` method in `PrimeFinder` using your modified `WorkQueue` class. There should be 1 task or `Runnable` object for each number being tested and the `run()` method of these objects should use the `isPrime(int)` method.

## Requirements

The official name of this homework is `PrimeFinder`. This should be the name you use for your Eclipse Java project and the name you use when running the homework test script.
