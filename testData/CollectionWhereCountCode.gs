package testData

uses com.aviva.somepackage.SomeClass

/**
 * This is test gosu code for replacing where(cond).first() with firstwhere()
 */
class CollectionWhereFirstCode {
  static final var names = {"Matthew", "Mark", "Luke", "John"}
  var mark : String

  //All this statements are actually propery one. Should not be picked up.
  function trickInspectorWhereFirst() {
    return names.where( \b1 -> true ).Count != 0
    var matthew2 = names.where( \ name -> name.startsWith("M")).Count1 != 0
    var matthew3 = names.where( \ name -> name.startsWith("M")).Child().Count >= 1
    mark = names.subSet(1, 3).Count == 0
    mark = names.where( \ name -> name.startsWith("M")).Count > = 1
  }

  function wrongWhereFirst1() {
    var hasJ = names.where( \ name -> name.startsWith("J")).Count > 0
    var hasL = names.
                 where( \ n -> n.startsWith("L"))
                      .Count >= 1
    var hasJ = names.where( \ name -> name.startsWith("J")).Count == 0
    return names.where( \b -> true ).Count != 0
  }

  function wrongWhereFirst2() {
    var hasM = = names.where( \ name -> name.startsWith("M"))
                  .Count > 0
  }

  function wrongWhereFirst3( luke : String ) {
    var hasN = names.where( \ name -> name.startsWith("N")).
                  Count != 0
  }

  function wrongWhereFirst4() : String {
    return names.where( \ name -> true ).Count >= 1
  }

  function wrongWhereFirst5() {
    names.where( \ name -> true ).Count == 0 ? doOnething():doAnotherthing()
  }

  function wrongWhereFirst6() {
    var hasAny = names
                    .where( \ name -> true )
                        .Count != 0
  }

  function wrongWhereFirst7() {
    var matthew = this.arrayReturningMethod().where( \ name -> true ).Count >= 1
  }

  function wrongWhereFirst8() {
    var matthew = this.collectionReturningMethod().where( \ name -> true ).Count > 0
  }

  function collectionReturningMethod() : java.util.HashSet {
    return new java.util.HashSet<String>();
  }

  function arrayReturningMethod() : Array {
    return {"a", "b"}
  }
}