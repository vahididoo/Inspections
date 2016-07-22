package gw.acc.inpsections.samples.layout

class CurlyBrackets {

  /**
   * Method containing statements that should trigger 4 "Curly Brackets in Logical Blocks" code inspection issues
   */
  function bad() {
    if (true) print("foo")
    else if (false) print("boo")
    else print("bar")

    while (false) print("foo")

    for (var i in 1..2) print("foo" + i)

    do print("foo") while (false)
  }

  /**
   * Method containing statements that should not trigger any "Curly Brackets in Logical Blocks" code inspection issues
   */
  function good() {
    if (true) {
      print("foo")
    } else if (false) {
      print("bar")
    } else {
      print("boo")
    }

    while (false) {
      print("foo")
    }

    for (var i in 1..2) {
      print("foo" + i)
    }

    do {
      print("foo")
    } while (false)
  }
}