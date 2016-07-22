package gw.acc.inspections.samples.comments

class TesClass {
  var _aCount = 0

  /**
   *   print a message
   */
  public function printMessage() {
    print("Hello There")
  }

  private function incrementCount(amount: int) {
    _aCount += amount
  }

  protected function printFoobar() {
    print("Foobar")
  }

  public function doSomething() {    // this should highlight a warning
    var i = 0
    var j = 5

    if (i > j) {
      print("max is " + i)
    }
    else {
      print("max is " + j)
    }
  }
}