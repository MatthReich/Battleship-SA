package Battleship

import Battleship.aview.tui.Tui
import Battleship.controller.controllerComponent.Controller
import Battleship.controller.ControllerInterface
import com.google.inject.{Guice, Injector}
import Battleship.model.playerComponent.PlayerInterface
import Battleship.model.gridComponent.GridInterface

val injector: Injector = Guice.createInjector(new GameModul)

@main def battleship =
    println("starting Battleship...")
    val controller: ControllerInterface = injector.getInstance(classOf[ControllerInterface])
    val tui                             = Tui(controller)
    val shipSetList                     = Map("2" -> 2, "3" -> 0, "4" -> 0, "5" -> 0)

    controller.updatePlayer(
        "player_01",
        injector.getInstance(classOf[PlayerInterface]).updateGrid(
            injector.getInstance(classOf[GridInterface]).init()).updateName("player_01").updateShipSetList(shipSetList))
    controller.updatePlayer(
        "player_02",
        injector.getInstance(classOf[PlayerInterface]).updateGrid(
            injector.getInstance(classOf[GridInterface]).init()).updateName("player_02").updateShipSetList(shipSetList))

    tui.tui_process
