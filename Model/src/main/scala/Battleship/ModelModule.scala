package Battleship

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule

class ModelModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[DaoInterface].toInstance(DaoSlick())
  }
}
