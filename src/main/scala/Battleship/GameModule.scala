package Battleship

import Battleship.controller.InterfaceController
import Battleship.controller.controllerComponent.Controller
import Battleship.model.fileIoComponent.InterfaceFileIo
import Battleship.model.fileIoComponent.fileIoJsonImplementation.FileIo
import Battleship.model.gridComponent.InterfaceGrid
import Battleship.model.gridComponent.gridImplementation.Grid
import Battleship.model.playerComponent.InterfacePlayer
import Battleship.model.playerComponent.playerImplementation.Player
import Battleship.model.shipComponent.InterfaceShip
import Battleship.model.shipComponent.shipImplemenation.Ship
import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule


class GameModule extends AbstractModule with ScalaModule {

  override def configure(): Unit = {
    bind[InterfacePlayer].to[Player]
    bind[InterfaceGrid].to[Grid]
    bind[InterfaceShip].to[Ship]
    bind[InterfaceFileIo].to[FileIo]
    bind[InterfaceController].to[Controller]
  }

}
