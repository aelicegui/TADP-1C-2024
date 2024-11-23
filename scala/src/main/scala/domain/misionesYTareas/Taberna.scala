package domain.misionesYTareas

import domain.personas.Equipo
import domain.auxiliares.dropFirst


object Taberna {
  type Tablon = List[Mision]

  def elegirMision(tablon: Tablon, equipo: Equipo, criterio: (Equipo,Equipo)=>Boolean): Option[Mision] = {
    tablon.filter(m => m.realizarMision(equipo).isDefined).sortWith(
      (m1,m2)=>criterio(m1.realizarMision(equipo).get,m2.realizarMision(equipo).get)).headOption
  }

  def entrenar(tablon: Tablon, equipo: Equipo, criterio: (Equipo,Equipo)=>Boolean):Result ={
    elegirMision(tablon, equipo, criterio) match {
      case Some(mision) => entrenar(dropFirst[Mision](tablon, {_ == mision}), mision.realizarMision(equipo).get, criterio)
      case None if(tablon.nonEmpty) => tablon.head.realizarMision(equipo)
      case None => Success(equipo)
    }
  }
}

