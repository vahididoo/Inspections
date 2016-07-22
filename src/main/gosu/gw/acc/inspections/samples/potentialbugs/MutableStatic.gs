package gw.acc.inspections.samples.potentialbugs

uses java.util.HashMap
uses java.util.WeakHashMap
uses java.util.ArrayList
uses java.lang.Integer
uses java.util.LinkedHashSet

/**
 * Test cases for static collections. (Detecting arbitrary mutable statics is
 * much harder.)
 */
class MutableStatic {
  private construct() {

  }

  // Error - static collection
  static final var _cache = new HashMap<String, Object>()

  // No error - weak hashmap
  static final var _weakCache = new WeakHashMap<String, Object>()

  // Error - static collection
  static final var _staticList = new ArrayList<Integer>()

  // Error - static set
  static final var _allObjects = new LinkedHashSet<Object>()
}