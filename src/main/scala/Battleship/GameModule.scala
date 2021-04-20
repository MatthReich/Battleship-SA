package Battleship

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule


class GameModule extends AbstractModule with ScalaModule {

  override def configure(): Unit = {
    // bind[InterfacePlayer].to[Player]
    // bind[InterfaceGrid].to[Grid]
    // bind[InterfaceShip].to[Ship]
    // bind[InterfaceFileIo].to[FileIo]
    // bind[InterfaceController].to[Controller]
  }

}
