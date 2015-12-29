package ca.uwaterloo.flix.language.library

import ca.uwaterloo.flix.language.ast.Name
import ca.uwaterloo.flix.language.ast.TypedAst.Type
import ca.uwaterloo.flix.language.ast.TypedAst.Type._

import scala.collection.immutable

object FOpt {

  // TODO: Implement using `null` and iterators.

  // TODO: Create cheatsheet

  /**
    * All option operations.
    */
  val Ops: immutable.Map[Name.Resolved, OptOperator] = List(
  // TODO: None/Some
  // TODO: isNone
  // TODO: isSome

  // TODO: null
  // TODO: get
  // TODO: getOrElse
  // TODO:  or
  // TODO: orElse: (Opt[A], Opt[A] => Opt[A]
  // TODO: filter
  // TODO: exists
  // TODO: forall

  // TODO: map2: ((Opt, Opt) => A, Opt, Opt) => Opt
  // TODO: flatMap2: see above
  // TODO: toSet
  // TODO: toList

    "Opt::map" -> map,
    "Opt::flatMap" -> flatMap
  ).map {
    case (name, op) => Name.Resolved.mk(name) -> op
  }.toMap

  /**
    * A common super-type for all option operations.
    */
  sealed trait OptOperator extends LibraryOperator

  /**
    * Generic type variables.
    */
  val A = Type.Var("A")
  val B = Type.Var("B")

  /////////////////////////////////////////////////////////////////////////////
  // Basic Operations                                                        //
  /////////////////////////////////////////////////////////////////////////////
  /**
    * The `map : (Opt[A], A => B) => Opt[B]` function.
    */
  object map extends OptOperator {
    val tpe = (Opt(A), A ~> B) ~> Opt(B)
  }

  /**
    * The `flatMap : (Opt[A], A => Opt[B]) => Opt[B]` function.
    */
  object flatMap extends OptOperator {
    val tpe = (Opt(A), A ~> Opt(B)) ~> Opt(B)
  }

}
