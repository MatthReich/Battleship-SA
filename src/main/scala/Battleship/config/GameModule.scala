package Battleship.config

import Battleship.controller.InterfaceController
import Battleship.controller.controllerComponent.Controller
import Battleship.model.gridComponent.gridImplementation.Grid
import Battleship.model.gridComponent.strategyCollide.StrategyCollideNormal
import Battleship.model.gridComponent.{InterfaceGrid, InterfaceStrategyCollide}
import Battleship.model.playerComponent.InterfacePlayer
import Battleship.model.playerComponent.playerImplementation.Player
import Battleship.model.shipComponent.InterfaceShip
import Battleship.model.shipComponent.shipImplemenation.Ship
import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule


class GameModule extends AbstractModule with ScalaModule {

  override def configure(): Unit = {
    bind[InterfaceGrid].to[Grid]
    bind[InterfaceShip].to[Ship]
    bind[InterfacePlayer].to[Player]
    bind[InterfaceStrategyCollide].to[StrategyCollideNormal]
    bind[InterfaceController].to[Controller]
  }
}
