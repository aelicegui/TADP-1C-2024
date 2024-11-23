package domain.personas.elementosDeHeroes

import domain.personas.Heroe
import domain.personas.elementosDeHeroes.PartesDelCuerpo.{AmbasManos, Cabeza, Ninguna, PartesDelCuerpo, Torso, UnaMano}

abstract class Item (
                  val condicion: (Heroe => Boolean),
                  val parteDelCuerpo: PartesDelCuerpo,
                  val valorEnOro: Float
               ){
  def impacto(heroe: Heroe): Heroe // si el impacto se basa en los stats pedirselos al stat
}

object ArcoViejo extends Item ( condicion = { heroe: Heroe => true },
                                parteDelCuerpo = AmbasManos,
                                valorEnOro = 5
                              ) {
  override def impacto(heroe: Heroe): Heroe = heroe.modificarStats(fuerza = _ + 2)
}

object ArmaduraEleganteSport extends Item ( condicion = { heroe: Heroe => true },
                                            parteDelCuerpo = Torso,
                                            valorEnOro = 10
                                          ) {
  override def impacto(heroe: Heroe): Heroe = heroe.modificarStats( vida = _ - 30,
                                                                    velocidad = _ + 30
                                                                   )
}

object CascoVikingo extends Item ( condicion = { heroe: Heroe => heroe.fuerza > 30 },
                                   parteDelCuerpo = Cabeza,
                                   valorEnOro = 20
                                  ) {
  override def impacto(heroe: Heroe): Heroe = heroe.modificarStats(vida = _ + 10)
}

object EscudoAntiRobo extends Item ( condicion = { heroe: Heroe => (( heroe.getTrabajo != Ladron )
                                                                       && heroe.fuerza > 30 ) },
                                     parteDelCuerpo = UnaMano,
                                     valorEnOro = 50
                                    ) {
  override def impacto(heroe: Heroe): Heroe = heroe.modificarStats(vida = _ + 20)
}
object EspadaDeLaVida extends Item ( condicion = { heroe: Heroe => true},
                                     parteDelCuerpo = Ninguna,
                                     valorEnOro = 35
                                   ) { // Hace que la fuerza del héroe sea igual a su hp
  override def impacto(heroe: Heroe): Heroe = heroe.modificarStats(fuerza = _ => heroe.stats.getVida)
}

object PalitoMagico extends Item ( condicion = { heroe: Heroe => (( heroe.getTrabajo == Mago )
                                                                    || ( heroe.getTrabajo == Ladron && heroe.inteligencia > 30 )) },
                                   parteDelCuerpo = UnaMano,
                                   valorEnOro = 20
                                 ) {
  override def impacto(heroe: Heroe): Heroe = heroe.modificarStats(inteligencia = _ + 20)
}

object TalismanDeDedicacion extends Item ( condicion = { heroe: Heroe => true },
                                           parteDelCuerpo = Ninguna,
                                           valorEnOro = 100
                                         ) { // Todos los stats se incrementan 10% del valor del stat principal del trabajo.
  override def impacto(heroe: Heroe): Heroe = heroe.aumentarStatsEn(heroe.statPrincipal() * 0.1)
}

object TalismanDelMinimalismo extends Item ( condicion = { heroe: Heroe => true },
                                                   parteDelCuerpo = Ninguna,
                                                   valorEnOro = 50
                                                 ) { // -10 hp por cada otro ítem equipado.
  override def impacto(heroe: Heroe): Heroe = heroe.modificarStats(vida = _ + 50 - (10 * (heroe.inventario.size - 1)))
}

object TalismanMaldito extends Item ( condicion = { heroe: Heroe => true},
                                           parteDelCuerpo = Ninguna,
                                           valorEnOro = 500
                                         ) {
  override def impacto(heroe: Heroe): Heroe = heroe.modificarStats( vida = _ => 1,
                                                                    fuerza = _ => 1,
                                                                    velocidad = _ => 1,
                                                                    inteligencia = _ => 1
                                                                   )
}

object VinchaDeBufaloDeAgua extends Item ( condicion = { heroe: Heroe => heroe.getTrabajo == Desempleado},
                                           parteDelCuerpo = Cabeza,
                                           valorEnOro = 45
                                         ) {
  override def impacto(heroe: Heroe): Heroe = {
    if (heroe.stats.getFuerza > heroe.stats.getInteligencia)
      heroe.modificarStats(inteligencia = _ + 30)
    else
      heroe.aumentarStatsEn(10).modificarStats(inteligencia = _ - 10)
  }
}