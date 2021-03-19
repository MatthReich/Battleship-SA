package Battleship.config

class GameModules extends AbstractModule with ScalaModule {

  override def configure(): Unit = {
    bind[StrategyCollide].to[StrategyCollideNormal]
    bind[InterfaceGrid].to[Grid]
    bind[InterfacePerson].to[Player]
    bind[InterfaceShip].to[Ship]
    bind[InterfaceController].to[Controller]
    bind[FileIOInterface].to[fileIoXmlImpl.FileIO]
  }
}
