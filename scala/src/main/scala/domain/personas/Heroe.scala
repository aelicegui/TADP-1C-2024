package domain.personas

import domain.auxiliares.dropFirst
import domain.personas.elementosDeHeroes.PartesDelCuerpo.{AmbasManos, PartesDelCuerpo, UnaMano}
import domain.personas.elementosDeHeroes.{Item, PartesDelCuerpo, Trabajo}

case class Heroe(
                   stats: Stats,
                   trabajo: Trabajo,
                   inventario: List[Item]
                 ){

  // def stats(nuevosStats: Stats): Heroe = this.copy(stats = nuevosStats)
  def vida: Double = impactoEnStats.getVida
  def fuerza: Double = impactoEnStats.getFuerza
  def velocidad: Double = impactoEnStats.getVelocidad
  def inteligencia: Double = impactoEnStats.getInteligencia

  def getTrabajo: Trabajo = this.trabajo

  private def impactoEnStats:Stats = {
    inventario.foldLeft( trabajo.impacto(this) )( (heroeAnterior, item) => item.impacto(heroeAnterior) ).stats.statsPositivas()
  }

  def modificarStats( vida: Double => Double = i => i,
                      fuerza: Double => Double = i => i,
                      velocidad: Double => Double = i => i,
                      inteligencia: Double => Double = i => i
                    ): Heroe = this.copy( stats = this.stats.modificar( vida, fuerza, velocidad, inteligencia ))

  def aumentarStatsEn(cantidad: Double): Heroe = this.copy(stats= this.stats.aumetarEn(cantidad))


  def statPrincipal(): Double = trabajo.statPrincipal(this)

  def equiparItem(item: Item): Heroe = {
    item match {
      case _ if item.condicion(this) && espacioOcupado(item.parteDelCuerpo) => this.copy(inventario = reeplazarItem(item))
      case _ if item.condicion(this) => this.copy(inventario = inventario :+ item)
      case _ => this
    }
  }

  def cambiarTrabajo(nuevoTrabajo: Trabajo): Heroe = this.copy(trabajo = nuevoTrabajo)

  private def espacioOcupado(espacioDeCuerpo: PartesDelCuerpo): Boolean = {
    val itemsEnEspacio = inventario.filter(_.parteDelCuerpo == espacioDeCuerpo)

    espacioDeCuerpo match {
      case UnaMano => itemsEnEspacio.size == 2 || inventario.exists(_.parteDelCuerpo == AmbasManos)
      case AmbasManos => itemsEnEspacio.isEmpty || !inventario.exists(_.parteDelCuerpo == UnaMano)
      case _ => itemsEnEspacio.nonEmpty
    }
  }

  private def reeplazarItem(item: Item): List[Item] = {
    val inventarioFinal: List[Item] = item.parteDelCuerpo match {
      case UnaMano => dropFirst[Item](inventario,(i => i.parteDelCuerpo == AmbasManos || i.parteDelCuerpo == UnaMano))
      case _ => inventario.filterNot(itemEq => (
        item.parteDelCuerpo match {
          case AmbasManos => itemEq.parteDelCuerpo == UnaMano
          case _ => false
        }) || itemEq.parteDelCuerpo == item.parteDelCuerpo)
    }

    inventarioFinal :+ item
  }

  def mejoraDeEquipamiento(item: Item): Double = this.equiparItem(item).statPrincipal() - this.statPrincipal()

}
