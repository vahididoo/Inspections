package gw.acc.inspections.samples.potentialbugs

uses java.util.Map
uses java.lang.Integer
uses java.util.Set

/**
 * Test cases for the uninitialized variable inspection.
 */
class UninitializedVariables {
  // Error: private variable not initialized
  var _member : String

  // No error: Initialized
  var _name = "My Name"

  // No error: writeable property
  var _prop : String as Property

  // Error: readonly property not initialized
  var _readMe : String as readonly ReadMe

  public var _breaksEncapsulation : String

  function foo() {
    // Error: uninitialized
    var val : int

    // Error: uninitialized, quick fix to {}
    var myMap : Map<String, String>

    // Error: uninitialized, quick fix to {}
    var myList : List<Integer>

    // Error: uninitialized, quick fix to {}
    var mySet : Set<String>

    // No error: initialized
    var value2 = "Some value"

    // No error: declared in loop
    for (var i in 1..10) {

      var inner : int

      print(i + inner)
    }
  }
}