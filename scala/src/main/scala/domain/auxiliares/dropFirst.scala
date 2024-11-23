package domain.auxiliares

object dropFirst{
  def apply[M](lista: List[M], condicion: M => Boolean): List[M] = {
    if (lista.isEmpty) {
      return lista
    }
    if (condicion(lista.head)) {
      lista.tail
    }
    else {
      List(lista.head) ++ dropFirst(lista.tail, condicion)
    }
  }
}
