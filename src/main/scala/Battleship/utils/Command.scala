package Battleship.utils

trait Command:
    def doStep(): Unit
    
    def undoStep(): Unit
