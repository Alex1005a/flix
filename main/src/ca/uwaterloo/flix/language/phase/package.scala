/*
 * Copyright 2015-2016 Magnus Madsen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ca.uwaterloo.flix.language

import ca.uwaterloo.flix.language.ast.{SourceLocation, Symbol}

package object phase {

  class GenSym() {

    /**
      * An internal counter.
      */
    private var number: Int = 0

    /**
      * Returns a freshly generated unique id.
      */
    def freshId(): Int = {
      number = number + 1
      number
    }

    // TODO: deprecated
    def freshDefn(parts: List[String]): Symbol.DefnSym = {
      number = number + 1
      val namespace = if (parts.isEmpty) Nil else parts.init
      val name = if (parts.isEmpty) "$" + number else parts.last + "$" + number
      new Symbol.DefnSym(namespace, name, SourceLocation.Unknown)
    }

  }

}
