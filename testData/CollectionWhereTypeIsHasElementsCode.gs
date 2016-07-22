package testData

/**
 * This is test gosu code for replacing where(cond).Count with hasElement()
 */
class CollectionWhereTypeIsHasElementsCode {
  static final var names = {"Matthew", "Mark", "Luke", "John"}
  var mark : String

  //All this statements are actually proper one. Should not be picked up.
  function trickInspectorWhereFirst() {
    var matthew2 = names.whereTypeIs(String).hashCode() != 0
    var matthew3 = names.whereTypeIs(String).toTypedArray().HasElements
    mark = names.subList(1, 3).HasElements
//    mark = names.where( \ name -> name.startsWith("M")).Count > = 1
  }

  function wrongWhereFirst1() : boolean {
    var hasJ = names.whereTypeIs(String).HasElements
    var hasL = names.
        whereTypeIs(String)
        .HasElements
    var hasM = names.whereTypeIs(String).HasElements
    return names.whereTypeIs(String).HasElements
  }

  function wrongWhereFirst2() {
    var hasM = names.whereTypeIs(String)
        .HasElements
  }

  function wrongWhereFirst3( luke : String ) {
    var hasN = names.whereTypeIs(String).
        HasElements
  }

  function wrongWhereFirst4() : String {
    return names.whereTypeIs(String).HasElements
  }

  function wrongWhereFirst5() {
    var num = (names.whereTypeIs(String).HasElements) ? 0 : 1
  }

  function wrongWhereFirst6() {
    var hasAny = names
        .whereTypeIs(String)
        .HasElements
  }

  function wrongWhereFirst7() {
    var matthew = this.arrayReturningMethod().whereTypeIs(String).HasElements
  }

  function wrongWhereFirst8() {
    var matthew = this.collectionReturningMethod().whereTypeIs(String).HasElements
  }

  function collectionReturningMethod() : java.util.HashSet {
    return new java.util.HashSet<String>();
  }

  function arrayReturningMethod() : Array {
    return {"a", "b"}
  }

}