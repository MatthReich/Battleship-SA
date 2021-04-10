package Battleship.controller.controllerComponent.commands

trait Command {

  def doStep(): Unit

  def undoStep(): Unit

}
