package Battleship.model.playerComponent

trait PlayerInterface:
    def name: String
    def updateName(newName: String): PlayerInterface