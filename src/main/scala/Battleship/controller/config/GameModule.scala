package Battleship.controller.config

import Battleship.model.fileIoComponent.InterfaceFileIo
import Battleship.model.fileIoComponent.fileIoJsonImplementation.FileIo
import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule


class GameModule extends AbstractModule with ScalaModule {

  override def configure(): Unit = {
    bind[InterfaceFileIo].to[FileIo]
  }
}
