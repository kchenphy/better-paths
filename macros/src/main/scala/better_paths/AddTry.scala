package better_paths

import scala.annotation.StaticAnnotation
import scala.reflect.macros.whitebox.Context
import scala.language.experimental.macros

class AddTry extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro AddTry.impl
}

object AddTry {
  def impl(c: Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._

    val result = {
      annottees.map(_.tree).toList match {
        case q"$mods def $methodName[..$tpes](...$args): $returnType = { ..$body }" :: Nil => {

          val mn = methodName.toString
          val tryMethodName = TermName(s"try${mn.head.toUpper}${mn.tail}")

          q"""$mods def $methodName[..$tpes](...$args): $returnType = { ..$body }
              import scala.util.Try
              $mods def $tryMethodName[..$tpes](...$args) = Try { ..$body }
          """
        }

        case _ => c.abort(c.enclosingPosition, "Annotation @AddTry can be used only with methods")
      }
    }

    c.Expr[Any](result)
  }
}
