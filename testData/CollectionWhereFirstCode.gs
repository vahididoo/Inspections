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
    var matthew2 = names.where( \ name -> name.startsWith("M")).Count
    var matthew3 = names.where( \ name -> name.startsWith("M")).Child.first()
    mark = names.subSet(1, 3).first()
  }

  function wrongWhereFirst1() {
    var matthew = names.where( \ name -> name.startsWith("M")).first()
    mark = names.
                 where( \ n -> n.startsWith("L"))
                      .first()
    return names.where( \b -> true ).first().returnSomeObject( matthew, mark )
  }

  function wrongWhereFirst2() {
    mark = names.where( \ name -> name.startsWith("M"))
                  .first()
  }

  function wrongWhereFirst3( luke : String ) {
    luck = names.where( \ name -> name.startsWith("M")).
                  first()
  }

  function wrongWhereFirst4() : String {
    return names.where( \ name -> true ).first()
  }

  function wrongWhereFirst5() {
    names.where( \ name -> true ).first().doSomething()
  }

  function wrongWhereFirst6() {
    var matthew = names
                    .where( \ name -> true )
                        .first()
  }

  function wrongWhereFirst7() {
    var matthew = this.arrayReturningMethod().where( \ name -> true ).first()
  }

  function wrongWhereFirst8() {
    var matthew = this.collectionReturningMethod().where( \ name -> true ).first()
  }

  function collectionReturningMethod() : java.util.HashSet {
    return new java.util.HashSet<String>();
  }

  function arrayReturningMethod() : Array {
    return {"a", "b"}
  }
}