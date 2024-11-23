package domain.personas

import domain.personas.elementosDeHeroes.{CascoVikingo, Item}

case class Equipo (
                    grupo: List[Heroe],
                    nombre: String,
                    pozoComun: Float
             ){
  def agregarMiembro(nuevoMiembro: Heroe):Equipo = this.copy(grupo = grupo :+ nuevoMiembro)

  def ganarDinero(cantidad: Float): Equipo = this.copy(pozoComun = pozoComun + cantidad)

  def mejorSegun(criterio: (Heroe => Double)): Option[Heroe] = {
    mejorSegunRec(criterio,grupo) match{
      case head::Nil => Some(head)
      case _ => None
    }
  }

  def mejorSegunOption(criterio: (Heroe => Option[Double])): Option[Heroe] = {
    mejorSegunRec(criterio(_).get, grupo.filter(criterio(_).isDefined)).headOption
  }

  def mejorSegunRec(criterio: (Heroe => Double), subGrupo: List[Heroe]): List[Heroe] = {
    //subGrupo.sortBy(criterio).lastOption
    subGrupo match {
      case x::y::xs if(criterio(x)<criterio(y)) => mejorSegunRec(criterio, (List(y)++xs))
      case x::y::xs if(criterio(x)>criterio(y)) => mejorSegunRec(criterio, (List(x)++xs))
      case x::y::Nil if(criterio(x)==criterio(y)) => subGrupo
      case x::y::xs if(criterio(x)==criterio(y)) => mejorSegunRec(criterio, (xs:+x))
      case _ => subGrupo
    }
  }

  def remplazarMiembro(miembroViejo: Heroe, miembroNuevo: Heroe): Equipo =
    this.copy(grupo = grupo.filter(_ != miembroViejo)).agregarMiembro(miembroNuevo);

  def lider(): Option[Heroe] = mejorSegun(_.statPrincipal())

  def obtenerItem(item: Item): Equipo = {
    val heroeIndicado = mejorSegun(_.mejoraDeEquipamiento(item))

    heroeIndicado match {
      case Some(heroe) if(heroe.mejoraDeEquipamiento(item)) > 0 => remplazarMiembro(heroe, heroe.equiparItem(item))
      case _ => ganarDinero(item.valorEnOro)
    }

  }
}
