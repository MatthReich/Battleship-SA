package Battleship.aview.tui

import Battleship.controller.ControllerInterface
import Battleship.controller.controllerComponent.states.GameStates
import Battleship.controller.controllerComponent.states.PlayerStates
import scala.swing.Reactor
import Battleship.controller.controllerComponent.events._
import Battleship.model.playerComponent.PlayerInterface

class Tui(controller: ControllerInterface) extends Reactor:

    val tui_options: String = Tabulator.format(List(
        List("Options", "Description"),
        List("s", "start game"),
        List("n", "new game"),
        List("q", "quit game"),
        List("save", "save the game"),
        List("load", "load the game"),
        List("state info", "get info about the actual game situation")))

    val goodbye_message: String = "The game is written in Scala 3. Wish you all the best and hope to see you back soon!"

    val self: Int  = 0
    val enemy: Int = 1

    listenTo(controller)

    reactions += {
        case _: GameStart     => println("Yeah you play the best game in the world... probably :)")
        case _: PlayerChanged =>
            controller.gameState match
                case GameStates.PLAYERSETTING => printWithPlayerAnnotation("set yout name")
                case GameStates.SHIPSETTING   =>
                    printWithPlayerAnnotation("set your Ship <x y x y>")
                    printGrid(self)
                    printRemainingShips()
                case GameStates.IDLE          =>
                case GameStates.SOLVED        =>
                case GameStates.SAVED         =>
                case GameStates.LOADED        =>
    }

    def tui_process: Unit =
        println("Welcome to Battleship")
        println(tui_options)

        while true do
            val input = scala.io.StdIn.readLine()
            input match
                case "s"          => println("Started game")
                case "n"          => println("Starting new game")
                case "q"          =>
                    println(goodbye_message)
                    println("Exit game")
                    System.exit(0)
                case "save"       => println("Saving game")
                case "load"       => println("Loading game")
                case "state info" =>
                    println(controller.gameState.getInfo)
                    println(controller.playerState.getInfo)
                case _            =>
                    println("Wrong input: " + input)
                    println("try one of these:\n" + tui_options)

    private def printWithPlayerAnnotation(msg: String) = controller.playerState match
        case PlayerStates.PLAYER_ONE => println(Console.MAGENTA + controller.player_01.name + Console.RESET + " " + msg)
        case PlayerStates.PLAYER_TWO => println(Console.CYAN + controller.player_02.name + Console.RESET + " " + msg)

    private def printGrid(who: Int)                    = who match {
        case self  => println("self")
        case enemy => println("enemy")
        case _     => println("internel error")
    }

    private def printRemainingShips() = println("Remaining ships:\n")
