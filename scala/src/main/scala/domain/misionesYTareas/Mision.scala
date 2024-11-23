package domain.misionesYTareas

import domain.personas.{Equipo, Heroe}

sealed trait Result {
  def equipo: Equipo

  def map(f: Equipo => Equipo): Result
  def flatMap(f: Equipo => Result): Result
  def isDefined:Boolean
  def get: Equipo
}

case class Success(equipo:Equipo) extends Result{

  def map(f: Equipo => Equipo): Result = Success(f(equipo))
  def flatMap(f: Equipo => Result): Result = f(equipo)

  override def isDefined = true

  override def get: Equipo = equipo
}

case class Failure(equipo: Equipo, tarea: Tarea) extends Result {

  def map(f: Equipo => Equipo): Result = this
  def flatMap(f: Equipo => Result):Result = this

  override def isDefined = false

  override def get: Equipo = equipo // throw new Exception("No se puede hacer get a un failure")
}

case class Mision (
               tareas: List[Tarea],
               recompensa: Equipo => Equipo
             ) {
  def realizarMision(equipo: Equipo): Result = {
    val equipoFinal: Result = mision(equipo)
    equipoFinal.map(equipo => recompensa(equipo))
  }

  private def mision(equipo: Equipo): Result = {
    tareas.foldLeft(Success(equipo): Result) { (equipoPrevio, tareaActual) =>
      equipoPrevio.flatMap(equipo => tareaActual.hacerTarea(equipo)) }
  }


}



