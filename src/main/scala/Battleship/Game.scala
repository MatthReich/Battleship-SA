package Battleship

import Battleship.aview.tui.Tui
import Battleship.config.GameModules
import Battleship.controller.InterfaceController
import Battleship.controller.controllerbaseimpl.{ExitGame, NewGame}
import com.google.inject.Guice

import scala.swing.Reactor

object Game extends Reactor {
  val injector = Guice.createInjector(new GameModules)
  var controller = injector.getInstance(classOf[InterfaceController])
  listenTo(controller)

  reactions += {
    case event: NewGame =>
      controller = injector.getInstance(classOf[InterfaceController])
      new Tui(controller)
    case event: ExitGame => System.exit(0)
  }

  def main(args: Array[String]): Unit = {
    print("Battleship in gut wird gestartet...")
    new Tui(controller)
  }
}