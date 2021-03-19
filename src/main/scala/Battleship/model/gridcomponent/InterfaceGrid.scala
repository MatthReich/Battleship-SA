package Battleship.model.gridcomponent

trait InterfaceGrid {

  def setField(x: Int, y: Int, value: Int): Unit

  def getField(x: Int, y: Int): Int

  def getValue(x: Int, y: Int): Int

  def setSize(int: Int): Unit

  def getSize: Int

  def winStatement(): Boolean

}
