package Battleship.controller.utils

trait Command {

  def doStep(): Unit

  def undoStep(): Unit

}
