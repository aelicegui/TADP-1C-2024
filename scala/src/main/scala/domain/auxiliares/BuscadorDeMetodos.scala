package domain.auxiliares

import scala.reflect.runtime.universe._

object BuscadorDeMetodos {
  def encontrarElValor(objeto: Any, nombreDelMetodo: String): Option[Double] = {
    val mirror = runtimeMirror(objeto.getClass.getClassLoader)
    val instanceMirror = mirror.reflect(objeto)
    val methodSymbol = instanceMirror.symbol.typeSignature.member(TermName(nombreDelMetodo)).asMethod

    if (methodSymbol.isMethod) {
      val methodMirror = instanceMirror.reflectMethod(methodSymbol)
      val result = methodMirror()

      result match {
        case value: Double => Some(value)
        case _ => None
      }
    } else {
      None
    }
  }
}

