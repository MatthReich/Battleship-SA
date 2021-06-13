package Battleship.model.gridComponent

import Battleship.controller.controllerComponent.states.GameStates
import scala.util.Try

trait GridInterface:
    def size: Int

    def strategyCollide: StrategyCollideInterface

    def grid: Vector[Map[String, Int]]

    def setFields(gameState: GameStates, fields: Vector[Map[String, Int]]): Try[GridInterface]

    def init(): Unit
