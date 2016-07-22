package testData

/**
 * This is test gosu code for replacing where(cond).Count with hasElement()
 */
class CollectionWhereTypeIsCountCode {
  static final var names = {"Matthew", "Mark", "Luke", "John"}
  var mark : String

  //All this statements are actually proper one. Should not be picked up.
  function trickInspectorWhereFirst() {
    var matthew2 = names.whereTypeIs(String).hashCode() != 0
    var matthew3 = names.whereTypeIs(String).toTypedArray().Count >= 1
    mark = names.subList(1, 3).Count == 0
//    mark = names.whereTypeIs( \ name -> name.startsWith("M")).Count > = 1
  }

  function wrongWhereFirst1() : boolean {
    var hasJ = names.whereTypeIs(String).Count > 0
    var hasL = names.
        whereTypeIs(String)
        .Count >= 1
    var hasM = names.whereTypeIs(String).Count == 0
    return names.whereTypeIs(String).Count != 0
  }

  function wrongWhereFirst2() {
    var hasM = names.whereTypeIs(String)
        .Count > 0
  }

  function wrongWhereFirst3( luke : String ) {
    var hasN = names.whereTypeIs(String).
        Count != 0
  }

  function wrongWhereFirst4() : String {
    return names.whereTypeIs(String).Count >= 1
  }

  function wrongWhereFirst5() {
    var num = (names.whereTypeIs(String).Count == 0) ? 0 : 1
  }

  function wrongWhereFirst6() {
    var hasAny = names
        .whereTypeIs(String)
        .Count != 0
  }

  function wrongWhereFirst7() {
    var matthew = this.arrayReturningMethod().whereTypeIs(String).Count >= 1
  }

  function wrongWhereFirst8() {
    var matthew = this.collectionReturningMethod().whereTypeIs(String).Count > 0
  }

  function collectionReturningMethod() : java.util.HashSet {
    return new java.util.HashSet<String>();
  }

  function arrayReturningMethod() : Array {
    return {"a", "b"}
  }

}