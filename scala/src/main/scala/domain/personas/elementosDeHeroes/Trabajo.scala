package domain.personas.elementosDeHeroes

import domain.auxiliares.BuscadorDeMetodos
import domain.personas.Heroe

import scala.reflect.runtime.universe._

case class Trabajo (
                     statPrincipal: String
                    ) {
  def statPrincipal(heroe: Heroe): Double = statPrincipal match {
    case "" => 0
    case _ => BuscadorDeMetodos.encontrarElValor(heroe, statPrincipal).get
  }

  def impacto(heroe: Heroe): Heroe = heroe
}

object Desempleado extends Trabajo( statPrincipal = "" )

object Mago extends Trabajo ( statPrincipal = "inteligencia" ) {
  override def impacto(heroe: Heroe): Heroe = heroe.modificarStats( vida = _ - 5 ,
                                                                    fuerza = _ - 20,
                                                                    inteligencia = _ + 10
                                                                  )
}

object Ladron extends Trabajo ( statPrincipal = "velocidad" ) {
  override def impacto(heroe: Heroe): Heroe = heroe.modificarStats( vida = _ - 5 ,
                                                                    velocidad = _ + 10
                                                                  )
}

object Guerrero extends Trabajo ( statPrincipal= "fuerza" ) {

  override def impacto(heroe: Heroe): Heroe = heroe.modificarStats( vida = _ + 10 ,
                                                                    fuerza = _ + 15,
                                                                    inteligencia = _ - 10
                                                                  )
}