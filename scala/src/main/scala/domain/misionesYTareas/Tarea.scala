package domain.misionesYTareas

import domain.personas.{Equipo, Heroe}
import domain.personas.elementosDeHeroes.{Guerrero, Item, Ladron, Mago, Trabajo}

case class Tarea(
                  tarea: (Heroe => Heroe),
                  facilidad: (Equipo => Heroe => Option[Double])
                ){
  def heroeIndicado(equipo: Equipo): Option[Heroe] = equipo.mejorSegunOption(facilidad(equipo))

  def hacerTarea(equipo: Equipo): Result = {
    val posibleHeroe:Option[Heroe] = heroeIndicado(equipo)

    posibleHeroe match{
      case None => Failure(equipo, this)
      case Some(heroe) => Success(equipo.remplazarMiembro(heroe, impacto(heroe)))
    }

  }

  private def impacto(heroe: Heroe): Heroe = tarea(heroe)
}

object  peleaContraMonstruo extends Tarea ( tarea = { heroe: Heroe =>  if (heroe.fuerza < 20) {
                                                                          heroe.modificarStats(vida = _ - 10 )
                                                                       }
                                                                       else {
                                                                         heroe
                                                                       }
                                                    },
                                             facilidad = { equipo: Equipo => heroe: Heroe =>
                                                             equipo.lider().map(_.getTrabajo) match {
                                                               case Some(Guerrero) => Some(20)
                                                               case _ => Some(10)
                                                             }
                                                         }
                                         )
object  peleaContraNadie extends Tarea ( tarea = { heroe: Heroe =>  heroe },
                                         facilidad = { equipo: Equipo => heroe: Heroe => Some(100.0) }
                                       )

object forzarPuerta extends Tarea ( tarea = { heroe: Heroe => heroe.getTrabajo match {
                                                    case Mago => heroe
                                                    case Ladron => heroe
                                                    case _ => heroe.modificarStats( vida = _ - 5,
                                                                                    fuerza = _ + 1
                                                                                  )
                                                    }
                                                 },
                                           facilidad = { equipo: Equipo => heroe: Heroe => Some(heroe.inteligencia +
                                             (10 * equipo.grupo.map(_.getTrabajo).count(_ == Ladron)))
                                           }
                                        )

class robarTalisman(talisman: Item) extends Tarea ( tarea = { heroe: Heroe => heroe.equiparItem(talisman)},
                                                         facilidad = { equipo: Equipo => heroe: Heroe =>
                                                           equipo.lider().map(_.getTrabajo) match {
                                                              case Some(Ladron) => Some(heroe.velocidad)
                                                              case _ => None
                                                           }
                                                         }
                                                        )