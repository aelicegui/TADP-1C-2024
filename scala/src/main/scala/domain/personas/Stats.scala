package domain.personas

case class Stats(
                   vida: Double,
                   fuerza: Double,
                   velocidad: Double,
                   inteligencia: Double
                 ) {
  def getVida: Double = vida
  def getFuerza: Double = fuerza
  def getVelocidad: Double = velocidad
  def getInteligencia: Double = inteligencia

  def modificar(vida: Double => Double = i => i,
                fuerza: Double => Double = i => i,
                velocidad: Double => Double = i => i,
                inteligencia: Double => Double = i => i
               ): Stats = this.copy( vida(this.vida),
                                     fuerza(this.fuerza),
                                     velocidad(this.velocidad),
                                     inteligencia(this.inteligencia)
                                   )

  def aumetarEn(cantidad:Double):Stats = {
    val aumentar: Double => Double = {_ + cantidad}
    this.modificar( vida = aumentar,
                    fuerza = aumentar,
                    velocidad = aumentar,
                    inteligencia = aumentar
                  )
  }

  def statsPositivas():Stats = this.modificar( vida = _.max(1),
                                               fuerza = _.max(1),
                                               velocidad = _.max(1),
                                               inteligencia = _.max(1)
                                             )
}