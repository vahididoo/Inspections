package gw.acc.inspections.samples.naminganddeclarations

uses java.lang.RuntimeException
uses java.lang.Throwable

/**
 * Test cases for single-letter variable names.
 */
class SingleLetterVarName {
  public var q : int

  function foo() {
    // i in a loop should be fine
    for (var i in 1..10) {
      // So should j
      for (var j in 1..10) {
        print(i + "x" + j)
      }
    }

    try {
      print(10)
    // Error - single letter var name
    } catch (f) {
      print(f)
    }

    try {
      print(30)
    // Error - t is not a valid caught exception name
    } catch (t : RuntimeException) {
      print(t)
    // But it's fine if you're catching a Throwable
    } catch (t : Throwable) {
      print(t)
    }

    try {
      print(100)
    } catch (e) {
      print(e)
    }
  }
}