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

package ca.uwaterloo.flix

import java.io.File

import ca.uwaterloo.flix.api._
import ca.uwaterloo.flix.util._

/**
  * The main entry point for the Flix compiler and runtime.
  */
object Main {

  /**
    * The main method.
    */
  def main(argv: Array[String]): Unit = {

    // parse command line options.
    val cmdOpts: CmdOpts = parseCmdOpts(argv).getOrElse {
      Console.err.println("Unable to parse command line arguments. Will now exit.")
      System.exit(1)
      null
    }

    // construct flix options.
    val options = Options.Default.copy(
      debug = cmdOpts.debug,
      evaluation = if (cmdOpts.interpreter) Evaluation.Interpreted else Evaluation.Compiled,
      monitor = cmdOpts.monitor,
      threads = cmdOpts.threads,
      verbosity = if (cmdOpts.verbose) Verbosity.Verbose else Verbosity.Normal,
      verifier = cmdOpts.verifier
    )

    // configure Flix and add the paths.
    val builder = new Flix()
    builder.setOptions(options)
    for (file <- cmdOpts.files) {
      builder.addPath(file.toPath)
    }

    // compute the least model.
    try {
      builder.solve() match {
        case Validation.Success(model, errors) =>
          errors.foreach(e => println(e.message))

          val print = cmdOpts.print
          for (name <- print) {
            PrettyPrint.print(name, model)
          }
        case Validation.Failure(errors) =>
          errors.foreach(e => println(e.message))
      }
    } catch {
      case UserException(msg, loc) =>
        Console.err.println("User error " + loc.format)
        Console.err.println()
        Console.err.println(loc.underline(new AnsiConsole))
        System.exit(1)
      case MatchException(msg, loc) =>
        Console.err.println("Non-exhaustive match " + loc.format)
        Console.err.println()
        Console.err.println(loc.underline(new AnsiConsole))
        System.exit(1)
      case SwitchException(msg, loc) =>
        Console.err.println("Non-exhaustive switch " + loc.format)
        Console.err.println()
        Console.err.println(loc.underline(new AnsiConsole))
        System.exit(1)
    }

  }

  /**
    * A case class representing the parsed command line options.
    */
  case class CmdOpts(monitor: Boolean = false,
                     print: Seq[String] = Seq(),
                     threads: Int = -1,
                     verbose: Boolean = false,
                     verifier: Boolean = false,
                     debug: Boolean = false,
                     interpreter: Boolean = false,
                     files: Seq[File] = Seq())

  /**
    * Parse command line options.
    *
    * @param args the arguments array.
    */
  def parseCmdOpts(args: Array[String]): Option[CmdOpts] = {
    val parser = new scopt.OptionParser[CmdOpts]("flix") {

      // Head
      head("The Flix Programming Language", Version.CurrentVersion.toString)

      // Help.
      help("help").text("prints this usage information.")

      // Monitor.
      opt[Unit]('m', "monitor").action((_, c) => c.copy(monitor = true)).
        text("enables the debugger and profiler.")

      // Print.
      opt[Seq[String]]('p', "print").action((xs, c) => c.copy(print = xs)).
        valueName("<name>...").
        text("selects the relations/lattices to print.")

      // Threads.
      opt[Int]('t', "threads").action((i, c) => c.copy(threads = i)).
        validate(x => if (x > 0) success else failure("Value <n> must be at least 1.")).
        valueName("<n>").
        text("selects the number of threads to use.")

      // Verbose.
      opt[Unit]('v', "verbose").action((_, c) => c.copy(verbose = true))
        .text("enables verbose output.")

      // Verifier.
      opt[Unit]("verifier").action((_, c) => c.copy(verifier = true)).
        text("enables the verifier.")

      // Version.
      version("version").text("prints the version number.")

      // Experimental options:

      // XDebug.
      opt[Unit]("Xdebug").action((_, c) => c.copy(debug = true)).
        text("[experimental] enables otuput of debugging information.")

      // XInterpreter.
      opt[Unit]("Xinterpreter").action((_, c) => c.copy(interpreter = true)).
        text("[experimental] enables interpreted evaluation.")

      // Input files.
      arg[File]("<file>...").action((x, c) => c.copy(files = c.files :+ x))
        .unbounded()
        .text("input Flix source code files.")

    }

    parser.parse(args, CmdOpts())
  }

}
