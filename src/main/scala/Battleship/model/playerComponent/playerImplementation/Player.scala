package Battleship.model.playerComponent.playerImplementation

import Battleship.model.playerComponent.PlayerInterface

case class Player(val name: String = "") extends PlayerInterface:
    override def updateName(newName: String): PlayerInterface = this.copy(name = newName)
