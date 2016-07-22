package testData

uses com.aviva.somepackage.SomeClass

/**
 * This is test gosu code for replacing where(cond).first() with firstwhere()
 */
class CollectionWhereSingleCode {
  static final var names = {"Matthew", "Mark", "Luke", "John"}
  var mark : String

  //All this statements are actually propery one. Should not be picked up.
  function trickInspectorWhereSingle() {
    var matthew2 = names.where( \ name -> name.startsWith("M")).Count
    var matthew3 = names.where( \ name -> name.startsWith("M")).Child.single()
    mark = names.subSet(1, 3).single()
  }

  function wrongWhereSingle1() {
    var matthew = names.where( \ name -> name.startsWith("M")).single()
    mark = names.
                 where( \ n -> n.startsWith("L"))
                      .single()
    return names.where( \b -> true ).single().returnSomeObject( matthew, mark )
  }

  function wrongWhereSingle2() {
    mark = names.where( \ name -> name.startsWith("M"))
                  .single()
  }

  function wrongWhereSingle3( luke : String ) {
    luck = names.where( \ name -> name.startsWith("M")).
                  single()
  }

  function wrongWhereSingle4() : String {
    return names.where( \ name -> true ).single()
  }

  function wrongWhereFirst5() {
    names.where( \ name -> true ).single().doSomething()
  }

  function wrongWhereSingle6() {
    var matthew = names
                    .where( \ name -> true )
                        .single()
  }

  function wrongWhereSingle7() {
    var matthew = this.arrayReturningMethod().where( \ name -> true ).single()
  }

  function wrongWhereSingle8() {
    var matthew = this.collectionReturningMethod().where( \ name -> true ).single()
  }

  function collectionReturningMethod() : java.util.HashSet {
    return new java.util.HashSet<String>();
  }

  function arrayReturningMethod() : Array {
    return {"a", "b"}
  }
}