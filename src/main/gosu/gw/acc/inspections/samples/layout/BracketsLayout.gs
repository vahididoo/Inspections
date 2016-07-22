package gw.acc.inspections.samples.layout

/**
 * Some useful test cases for opening and closing brackets.
 */
// Error - no space
class BracketsLayout{

  // Error - opening brace on next line
  construct()
  {
    if (1 == 2) {
      // Error - closing brace on same line
      print("Math is broken")     }
  }

  // Inner class should report an error because of the missing space
  class Inner{

  }

  class Inner2
  // Two errors - opening and closing braces
  { var _name : String as Name }

  // No errors
  class InnerCorrect {
    var _name : String as Name
  }

  // Error - closing brace on same line
  property get Name() : String { return "foo" }

  // Error - Opening brace on next line
  property get TypeName() : String
  {
    return "TypeName"
  }

  // Should be an error here - no space
  property get NoSpaceName() : String{
    return "NoSpace"
  }

  function doWork()
  {

  }

  function doSomethingElse() {
    // No space
    if (1 == 2){
      print("Bad math")
    }

    if (2 == 3)
    {
      print("That doesn't work either")    }

    if (1 == 1) {
      print("All correct")
    }
  }

  function noSpaceHere(){
    var list = { "One", "Two" }
    // Error - no space
    for (var foo in list){
      print(foo)
    }

    // Error - opening brace on next line
    for (var x in list)
    {
      // Error - closing brace on same line
      print(x) }
  }

  function closeOnWrongLine() : void {
    var i = 0
    while (i < 10){
      print(i)
      i++
    }

    while (i > 0)
    {
      print(i)
      i--     }

    // Correct
    while (i < 10) {
      print(i)
      i++
    }

    // Error - closing brace on same line
    return }
}
