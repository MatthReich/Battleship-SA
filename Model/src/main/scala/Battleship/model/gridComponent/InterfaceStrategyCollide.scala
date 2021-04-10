package Battleship.model.gridComponent

trait InterfaceStrategyCollide {

  def collide(fields: Vector[Map[String, Int]], grid: Vector[Map[String, Int]]): Either[Vector[Int], Vector[Int]]

}
