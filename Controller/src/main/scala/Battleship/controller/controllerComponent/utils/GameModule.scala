package Battleship.controller.controllerComponent.utils


import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule


class GameModule extends AbstractModule with ScalaModule {

  override def configure(): Unit = {
    // bind[InterfaceFileIo].to[FileIo]
  }
}
