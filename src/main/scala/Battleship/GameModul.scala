package Battleship

import Battleship.controller.ControllerInterface
import com.google.inject.{AbstractModule, Singleton}
import Battleship.controller.controllerComponent.Controller
import Battleship.model.playerComponent.PlayerInterface
import Battleship.model.playerComponent.playerImplementation.Player
import Battleship.model.gridComponent.GridInterface
import Battleship.model.gridComponent.gridImplementation.Grid

@Singleton
class GameModul extends AbstractModule:

    override def configure() = 
        bind(classOf[ControllerInterface]).toInstance(Controller())
        bind(classOf[PlayerInterface]).toInstance(Player())
        bind(classOf[GridInterface]).toInstance(Grid())
