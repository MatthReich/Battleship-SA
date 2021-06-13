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

    controller.updatePlayer(
        "player_01",
        injector.getInstance(classOf[PlayerInterface]).updateGrid(injector.getInstance(classOf[GridInterface]).init()))
    controller.updatePlayer(
        "player_02",
        injector.getInstance(classOf[PlayerInterface]).updateGrid(injector.getInstance(classOf[GridInterface]).init()))

    tui.tui_process
