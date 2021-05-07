package Battleship

import Battleship.model.databaseComponent.DaoInterface
import Battleship.model.databaseComponent.mongodb.DaoMongo
import Battleship.model.databaseComponent.slick.DaoSlick
import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule

class ModelModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[DaoInterface].annotatedWithName("mongodb").toInstance(DaoMongo())
    bind[DaoInterface].annotatedWithName("mysql").toInstance(DaoSlick())
  }
}
