package Battleship.model.personcomponent.persons

import Battleship.model.personcomponent.InterfacePerson

case class Creator() extends InterfacePerson {
  var name: String = ""

  override def toString: String = name

  override def addName(name: String): Unit = this.name = name
}
