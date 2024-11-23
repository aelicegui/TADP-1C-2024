import domain.personas._
import domain.personas.elementosDeHeroes._
import domain.misionesYTareas.{Mision, _}
import org.scalatest.matchers.should.Matchers._
import org.scalatest.freespec.AnyFreeSpec

import scala.math.Ordered.orderingToOrdered


class ProjectSpec extends AnyFreeSpec {

  "Un heroe deberia poder" - { //Testeo de Heroe

    "cambiar sus stats" - {

      "actualizandolas" in {
        var heroe = Heroe(Stats(50, 20, 30, 10), Desempleado, List())
        heroe = heroe.modificarStats(vida = _ => 60,
          fuerza = _ => 30,
          velocidad = _ => 20,
          inteligencia = _ => 5)

        heroe.vida shouldBe 60
        heroe.fuerza shouldBe 30
        heroe.velocidad shouldBe 20
        heroe.inteligencia shouldBe 5
      }

      "al tener un Item equipado (el CascoVikingo suma 10 a la vida)" in {
        val heroe = Heroe(Stats(70, 50, 30, 10), Desempleado, List(CascoVikingo))

        heroe.vida shouldBe 80
        heroe.fuerza shouldBe 50
        heroe.velocidad shouldBe 30
        heroe.inteligencia shouldBe 10
      }

      "al tener un trabajo (el Guerrero tiene vida+10, fuerza+15 e inteligencia-10)" in {
        val heroe = Heroe(Stats(20, 40, 30, 15), Guerrero, List())

        heroe.vida shouldBe 30
        heroe.fuerza shouldBe 55
        heroe.velocidad shouldBe 30
        heroe.inteligencia shouldBe 5
      }

      "y que las mismas nunca sean 1 (la armadura le da vida-30)" in {
        val heroe = Heroe(Stats(20, 40, 30, 15), Desempleado, List(ArmaduraEleganteSport))

        heroe.vida shouldBe 1
        heroe.fuerza shouldBe 40
        heroe.velocidad shouldBe 60
        heroe.inteligencia shouldBe 15
      }
    }

    "equiparse un item" - {

      "teniendo lugar libre" in {
        var heroe = Heroe(Stats(50, 20, 30, 10), Desempleado, List(PalitoMagico))
        heroe = heroe.equiparItem(ArmaduraEleganteSport)
        heroe.inventario shouldBe List(PalitoMagico, ArmaduraEleganteSport)
      }

      "reemplazando otro" in {
        var heroe = Heroe(Stats(50, 30, 30, 10), Desempleado, List(CascoVikingo))
        heroe = heroe.equiparItem(VinchaDeBufaloDeAgua)

        heroe.inventario.contains(VinchaDeBufaloDeAgua) shouldBe true
        heroe.inventario.contains(CascoVikingo) shouldBe false
      }

      "reemplazando 2 de una mano por uno de ambas" in {
        var heroe = Heroe(Stats(50, 30, 30, 10), Desempleado, List(EscudoAntiRobo,PalitoMagico))
        heroe = heroe.equiparItem(ArcoViejo)

        heroe.inventario shouldBe List(ArcoViejo)
      }

      "reemplazando 1 de una mano por uno de ambas" in {
        var heroe = Heroe(Stats(50, 30, 30, 10), Desempleado, List(EscudoAntiRobo))
        heroe = heroe.equiparItem(ArcoViejo)

        heroe.inventario shouldBe List(ArcoViejo)
      }

      "reemplazando uno de ambas manos por uno de una" in {
        var heroe = Heroe(Stats(50, 31, 30, 10), Desempleado, List(ArcoViejo))
        heroe = heroe.equiparItem(EscudoAntiRobo)

        heroe.inventario shouldBe List(EscudoAntiRobo)
      }

      "equipar un item de una sola mano cuando ya hay uno" in {
        var heroe = Heroe(Stats(50, 31, 30, 10), Desempleado, List(PalitoMagico))
        heroe = heroe.equiparItem(EscudoAntiRobo)

        heroe.inventario shouldBe List(PalitoMagico, EscudoAntiRobo)
      }

      "agregar un item de una mano cuando ya hay 2 remplazando 1" in {
        var heroe = Heroe(Stats(50, 31, 30, 10), Desempleado, List(PalitoMagico,PalitoMagico))
        heroe = heroe.equiparItem(EscudoAntiRobo)

        heroe.inventario shouldBe List(PalitoMagico, EscudoAntiRobo)
      }

      "a menos que no cumpla la condicion (el casco requiere fuerza > 30)" in {
        var heroe = Heroe(Stats(50, 10, 30, 10), Desempleado, List())
        heroe = heroe.equiparItem(CascoVikingo)

        heroe.inventario.contains(CascoVikingo) shouldBe false
      }
    }

    "cambiar de trabajo" in {
      var heroe = Heroe(Stats(50, 10, 30, 10), Desempleado, List())
      heroe = heroe.cambiarTrabajo(Guerrero)

      heroe.getTrabajo shouldBe Guerrero
    }
  }

  "Un equipo deberia poder" - {

    "dar el mejor heroe segun una condicion (en este caso suma total de las Stats)" in {
      def sumaDeStats(heroe: Heroe): Double = heroe.vida + heroe.fuerza + heroe.velocidad + heroe.inteligencia

      val heroe1 = Heroe(Stats(60, 20, 40, 20), Desempleado, List())
      val heroe2 = Heroe(Stats(50, 10, 30, 10), Desempleado, List())

      val equipo = Equipo(List(heroe1, heroe2), "MarioBrothers", 450)

      equipo.mejorSegun(sumaDeStats) shouldBe Some(heroe1)
    }

    "obtener un Item y" - {

      "que se lo equipe uno de los heroes por que le aumenta la stat principal" in {
        val heroe1 = Heroe(Stats(60, 40, 40, 20), Desempleado, List())
        val heroe2 = Heroe(Stats(50, 100, 30, 10), Guerrero, List())

        val equipo = Equipo(List(heroe1, heroe2), "MarioBrothers", 450)

        equipo.obtenerItem(ArcoViejo).grupo.flatMap { heroe => heroe.inventario } shouldBe List(ArcoViejo)
      }

      "venderlo para el pozoComun (el casco vale 20) la stat principal aumenta igual para ambos" in {
        val heroe1 = Heroe(Stats(60, 40, 40, 20), Guerrero, List())
        val heroe2 = Heroe(Stats(50, 100, 30, 10), Guerrero, List())

        val equipo = Equipo(List(heroe1, heroe2), "MarioBrothers", 450)

        equipo.obtenerItem(CascoVikingo).grupo.flatMap { heroe => heroe.inventario } shouldBe List()
        equipo.obtenerItem(CascoVikingo).pozoComun shouldBe 470
      }

      "venderlo para el pozoComun (el talisman vale 500) le disminuye la stat principal a todos" in {
        val heroe1 = Heroe(Stats(60, 40, 40, 20), Mago, List())
        val heroe2 = Heroe(Stats(50, 100, 30, 10), Guerrero, List())

        val equipo = Equipo(List(heroe1, heroe2), "MarioBrothers", 450)

        equipo.obtenerItem(TalismanMaldito).grupo.flatMap { heroe => heroe.inventario } shouldBe List()
        equipo.obtenerItem(TalismanMaldito).pozoComun shouldBe 950
      }

      "venderlo para el pozoComun (el casco vale 20) no le aumenta la stat principal" in {
        val heroe1 = Heroe(Stats(60, 20, 40, 20), Desempleado, List())
        val heroe2 = Heroe(Stats(50, 10, 30, 10), Desempleado, List())

        val equipo = Equipo(List(heroe1, heroe2), "MarioBrothers", 450)

        equipo.obtenerItem(CascoVikingo).pozoComun shouldBe 470
      }
    }

    "obtener un nuevo miembro" in {
      val heroe1 = Heroe(Stats(60, 20, 40, 20), Desempleado, List())
      val heroe2 = Heroe(Stats(50, 10, 30, 10), Desempleado, List())

      val equipo = Equipo(List(heroe1), "Mario", 450)

      equipo.agregarMiembro(heroe2).grupo.size shouldBe 2
    }

    "reemplazar a un miembro" in {
      val heroe1 = Heroe(Stats(60, 20, 40, 20), Desempleado, List())
      val heroe2 = Heroe(Stats(50, 10, 30, 10), Desempleado, List())

      val equipo = Equipo(List(heroe1), "Mario", 450)

      equipo.remplazarMiembro(heroe1, heroe2).grupo.contains(heroe1) shouldBe false
    }

    "tener un lider" in {
      val heroe1 = Heroe(Stats(60, 20, 40, 20), Guerrero, List())
      val heroe2 = Heroe(Stats(50, 10, 30, 10), Guerrero, List())

      val equipo = Equipo(List(heroe1, heroe2), "MarioBrothers", 450)

      equipo.lider shouldBe Some(heroe1)
    }
  }

  "Una mision" - {

    "la deberia poder realizar este equipo" in {
      val heroe1 = Heroe(Stats(60,21,40,20), Guerrero, List())
      val heroe2 = Heroe(Stats(50,10,30,10), Guerrero, List())
      val heroeOP = Heroe(Stats(100,100,100,100), Guerrero, List())

      val equipo = Equipo(List(heroe1, heroe2), "MarioBrothers", 450)

      val mision: Mision = Mision(List(peleaContraMonstruo, forzarPuerta), { equipo:Equipo => equipo.agregarMiembro(heroeOP) } )

      mision.realizarMision(equipo).get.grupo.size shouldBe 3
    }

    "no deberia de poder realizarla el proximo equipo" in {
      val heroe1 = Heroe(Stats(60, 20, 40, 20), Guerrero, List())
      val heroe2 = Heroe(Stats(50, 10, 30, 10), Guerrero, List())

      val equipo = Equipo(List(heroe1, heroe2), "MarioBrothers", 450)

      val robarTalisman1 = new robarTalisman(TalismanDeDedicacion)

      val mision = Mision(List(peleaContraMonstruo, forzarPuerta, robarTalisman1, forzarPuerta), { e:Equipo => e })
      val mision2 = Mision(List(peleaContraMonstruo, forzarPuerta), { e:Equipo => e })

      mision.realizarMision(equipo) shouldBe Failure(mision2.realizarMision(equipo).get, robarTalisman1)
    }
  }

  "Una taberna" - {
    "puede elegir una mision cuando tiene un solo success" in {
      val heroe1 = Heroe(Stats(60, 20, 40, 20), Guerrero, List())
      val heroe2 = Heroe(Stats(50, 10, 30, 10), Guerrero, List())
      val equipo = Equipo(List(heroe1, heroe2), "MarioBrothers", 450)
      val robarTalisman1 = new robarTalisman(TalismanDeDedicacion)

      val mision = Mision(List(peleaContraMonstruo, forzarPuerta, robarTalisman1, forzarPuerta), { e:Equipo => e })
      val mision2 = Mision(List(peleaContraMonstruo, forzarPuerta), { e:Equipo => e })

      Taberna.elegirMision(List(mision, mision2), equipo, { _.pozoComun > _.pozoComun}) shouldBe Some(mision2)
    }

    "cuando no tiene ninguna mision success devuelve none" in {
      val heroe1 = Heroe(Stats(60, 20, 40, 20), Guerrero, List())
      val heroe2 = Heroe(Stats(50, 10, 30, 10), Guerrero, List())

      val equipo = Equipo(List(heroe1, heroe2), "MarioBrothers", 450)

      val robarTalisman1 = new robarTalisman(TalismanDeDedicacion)

      val mision = Mision(List(peleaContraMonstruo, forzarPuerta, robarTalisman1, forzarPuerta), { e:Equipo => e })

      Taberna.elegirMision(List(mision), equipo, { _.pozoComun > _.pozoComun}) shouldBe None
    }

    "puede elegir una mision que le haga ganar más oro" in {
      val heroe1 = Heroe(Stats(60, 20, 40, 20), Guerrero, List())
      val heroe2 = Heroe(Stats(50, 10, 30, 10), Guerrero, List())

      val equipo = Equipo(List(heroe1, heroe2), "MarioBrothers", 450)

      val robarTalisman1 = new robarTalisman(TalismanDeDedicacion)

      val mision = Mision(List(peleaContraMonstruo, forzarPuerta, robarTalisman1, forzarPuerta), { e:Equipo => e })
      val mision2 = Mision(List(peleaContraMonstruo, forzarPuerta), { e:Equipo => e })
      val mision3 = Mision(List(peleaContraMonstruo, forzarPuerta), { e:Equipo => e.ganarDinero(10) })
      val mision4 = Mision(List(peleaContraMonstruo, forzarPuerta), { e:Equipo => e.ganarDinero(20) })

      Taberna.elegirMision(List(mision, mision2, mision3, mision4), equipo, { _.pozoComun > _.pozoComun}) shouldBe Some(mision4)
    }

    "permite entrenar hasta fallar" in {
      val heroe1 = Heroe(Stats(60, 20, 40, 20), Guerrero, List())
      val heroe2 = Heroe(Stats(50, 10, 30, 10), Guerrero, List())

      val equipo = Equipo(List(heroe1, heroe2), "MarioBrothers", 450)

      val robarTalisman1 = new robarTalisman(TalismanDeDedicacion)

      val mision = Mision(List(peleaContraMonstruo, forzarPuerta, robarTalisman1, forzarPuerta), { e:Equipo => e })
      val mision2 = Mision(List(peleaContraMonstruo, forzarPuerta), { e:Equipo => e })
      val mision3 = Mision(List(peleaContraMonstruo, forzarPuerta), { e:Equipo => e.ganarDinero(10) })
      val mision4 = Mision(List(peleaContraMonstruo, forzarPuerta), { e:Equipo => e.ganarDinero(20) })

      val tablon= List(mision, mision2, mision3, mision4)

      Taberna.entrenar(tablon, equipo, { _.pozoComun > _.pozoComun}) shouldBe
        tablon.foldRight[Result](Success(equipo))((sigMision, equipoAnterior) => sigMision.realizarMision(equipoAnterior.get))
    }

    "permite entrenar hasta fallar la primera vez mision" in {
      val heroe1 = Heroe(Stats(60, 20, 40, 20), Guerrero, List())
      val heroe2 = Heroe(Stats(50, 10, 30, 10), Guerrero, List())

      val equipo = Equipo(List(heroe1, heroe2), "MarioBrothers", 450)

      val robarTalisman1 = new robarTalisman(TalismanDeDedicacion)
      val robarTalisman2 = new robarTalisman(TalismanDelMinimalismo)

      val mision = Mision(List(peleaContraMonstruo, forzarPuerta, robarTalisman1, forzarPuerta), { e:Equipo => e })
      val mision5 = Mision(List(robarTalisman2, forzarPuerta), { e:Equipo => e })
      val mision2 = Mision(List(peleaContraMonstruo, forzarPuerta), { e:Equipo => e })
      val mision3 = Mision(List(peleaContraMonstruo, forzarPuerta), { e:Equipo => e.ganarDinero(10) })
      val mision4 = Mision(List(peleaContraMonstruo, forzarPuerta), { e:Equipo => e.ganarDinero(20) })

      val tablon = List(mision, mision5, mision2, mision3, mision4)

      Taberna.entrenar(tablon, equipo, { _.pozoComun > _.pozoComun}) shouldBe
        mision.realizarMision(tablon.tail.tail.foldRight[Result](Success(equipo))((sigMision, equipoAnterior) =>
          sigMision.realizarMision(equipoAnterior.get)).get)
    }

    "permite entrenar hasta fallar la primera vez mision5" in {
      val heroe1 = Heroe(Stats(60, 20, 40, 20), Guerrero, List())
      val heroe2 = Heroe(Stats(50, 10, 30, 10), Guerrero, List())

      val equipo = Equipo(List(heroe1, heroe2), "MarioBrothers", 450)

      val robarTalisman1 = new robarTalisman(TalismanDeDedicacion)
      val robarTalisman2 = new robarTalisman(TalismanDelMinimalismo)

      val mision = Mision(List(peleaContraMonstruo, forzarPuerta, robarTalisman1, forzarPuerta), { e:Equipo => e })
      val mision5 = Mision(List(robarTalisman2, forzarPuerta), { e:Equipo => e })
      val mision2 = Mision(List(peleaContraMonstruo, forzarPuerta), { e:Equipo => e })
      val mision3 = Mision(List(peleaContraMonstruo, forzarPuerta), { e:Equipo => e.ganarDinero(10) })
      val mision4 = Mision(List(peleaContraMonstruo, forzarPuerta), { e:Equipo => e.ganarDinero(20) })

      val tablon = List(mision5, mision, mision2, mision3, mision4)

      Taberna.entrenar(tablon, equipo, { _.pozoComun > _.pozoComun}) shouldBe
        mision5.realizarMision(tablon.tail.tail.foldRight[Result](Success(equipo))((sigMision, equipoAnterior) =>
          sigMision.realizarMision(equipoAnterior.get)).get)
    }

    "permite entrenar hasta hacer todas las misiones" in {
      val heroe1 = Heroe(Stats(60, 20, 40, 20), Guerrero, List())
      val heroe2 = Heroe(Stats(50, 10, 30, 10), Guerrero, List())

      val equipo = Equipo(List(heroe1, heroe2), "MarioBrothers", 450)

      val mision2 = Mision(List(peleaContraMonstruo, forzarPuerta), { e: Equipo => e })
      val mision3 = Mision(List(peleaContraMonstruo, forzarPuerta), { e:Equipo => e.ganarDinero(10) })
      val mision4 = Mision(List(peleaContraMonstruo, forzarPuerta), { e:Equipo => e.ganarDinero(20) })
      val tablon = List(mision2, mision3, mision4)

      Taberna.entrenar(tablon, equipo, { _.pozoComun > _.pozoComun}) shouldBe
        tablon.foldRight[Result](Success(equipo))((sigMision, equipoAnterior) => sigMision.realizarMision(equipoAnterior.get))
    }

    "permite entrenar hasta hacer todas las misiones cuando tenemos dos comparaciones iguales" in {
      val heroe1 = Heroe(Stats(60, 20, 40, 20), Guerrero, List())
      val heroe2 = Heroe(Stats(50, 10, 30, 10), Guerrero, List())

      val equipo = Equipo(List(heroe1, heroe2), "MarioBrothers", 450)

      val mision2 = Mision(List(peleaContraNadie), { e:Equipo => e })
      val mision3 = Mision(List(peleaContraNadie), { e:Equipo => e.ganarDinero(10) })
      val mision4 = Mision(List(peleaContraNadie), { e:Equipo => e.ganarDinero(20) })
      val mision = Mision(List(peleaContraNadie), { e:Equipo => e.ganarDinero(20) })

      val tablon = List(mision2, mision, mision3, mision4)

      Taberna.entrenar(tablon, equipo, { _.pozoComun > _.pozoComun}) shouldBe
        tablon.foldRight[Result](Success(equipo))((sigMision, equipoAnterior) => sigMision.realizarMision(equipoAnterior.get))
    }

    "permite entrenar hasta hacer todas las misiones cuando tenemos misiones repetidas" in {
      val heroe1 = Heroe(Stats(60, 20, 40, 20), Guerrero, List())
      val heroe2 = Heroe(Stats(50, 10, 30, 10), Guerrero, List())

      val equipo = Equipo(List(heroe1, heroe2), "MarioBrothers", 450)

      val mision2 = Mision(List(peleaContraNadie), { e:Equipo => e })
      val mision3 = Mision(List(peleaContraNadie), { e:Equipo => e.ganarDinero(10) })
      val mision4 = Mision(List(peleaContraNadie), { e:Equipo => e.ganarDinero(20) })

      val tablon = List(mision2, mision4, mision3, mision2, mision4,mision4)

      Taberna.entrenar(tablon, equipo, { _.pozoComun > _.pozoComun}) shouldBe Success(equipo.ganarDinero(70))
    }
  }
}