package testData

/**
 * This is test gosu code for replacing where(cond).Count with hasElement()
 */
class CollectionWhereHasElementsCode {
  static final var names = {"Matthew", "Mark", "Luke", "John"}
  var mark : String

  //All this statements are actually proper one. Should not be picked up.
  function trickInspectorWhereFirst() {
    var matthew2 = names.where(\name -> name.startsWith("M")).hashCode() != 0
    var matthew3 = names.where(\name -> name.startsWith("M")).toTypedArray().HasElements
    mark = names.subList(1, 3).HasElements
//    mark = names.where( \ name -> name.startsWith("M")).Count > = 1
  }

  function wrongWhereFirst1() : boolean {
    var hasJ = names.where(\name -> name.startsWith("M")).HasElements
    var hasL = names.
        where(\name -> name.startsWith("M"))
        .HasElements
    var hasM = names.where(\name -> name.startsWith("M")).HasElements
    return names.where(\name -> name.startsWith("M")).HasElements
  }

  function wrongWhereFirst2() {
    var hasM = names.where(\name -> name.startsWith("M"))
        .HasElements
  }

  function wrongWhereFirst3() {
    var hasN = names.where(\name -> name.startsWith("M")).
        HasElements
  }

  function wrongWhereFirst4() : String {
    return names.where(\name -> name.startsWith("M")).HasElements
  }

  function wrongWhereFirst5() {
    var num = (names.where(\name -> name.startsWith("M")).HasElements) ? 0 : 1
  }

  function wrongWhereFirst6() {
    var hasAny = names
        .where(\name -> name.startsWith("M"))
        .HasElements
  }

  function wrongWhereFirst7() {
    var matthew = this.arrayReturningMethod().where(\name -> name.startsWith("M")).HasElements
  }

  function wrongWhereFirst8() {
    var matthew = this.collectionReturningMethod().where(\name -> name.startsWith("M")).HasElements
  }

  function collectionReturningMethod() : java.util.HashSet {
    return new java.util.HashSet<\name -> name.startsWith("M")>();
  }

  function arrayReturningMethod() : Array {
    return {"a", "b"}
  }

}