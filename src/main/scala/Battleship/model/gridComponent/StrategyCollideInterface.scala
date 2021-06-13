package Battleship.model.gridComponent

trait StrategyCollideInterface:
    def collides(fields: Vector[Map[String, Int]], grid: Vector[Map[String, Int]]): Either[Vector[Int], Vector[Int]]
