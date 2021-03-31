package Battleship.utils

class UndoManager {
  private var undoStack: List[Command]= Nil
  private var redoStack: List[Command]= Nil
  def doStep(command: Command): Unit = {
    undoStack = command::undoStack
    command.doStep()
  }
  def undoStep():Unit  = {
    undoStack match {
      case  Nil =>
      case head::stack =>
        head.undoStep()
        undoStack=stack
        redoStack= head::redoStack
    }
  }
}
