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
        List("h", "get all options + state info"),
        List("save", "save the game"),
        List("load", "load the game"),
        List("state info", "get info about the actual game situation")))

    val goodbye_message: String = "The game is written in Scala 3. Wish you all the best and hope to see you back soon!"

    val self: Int       = 0
    val enemy: Int      = 1
    val showAllShips    = true
    val showNotAllShips = false
    val toStringHelper  = ToStringHelper(controller.player_01.grid.size)

    listenTo(controller)

    reactions += {
        case _: GameStart            => println("Yeah you play the best game in the world... probably :)")
        case _: PlayerChanged        => controller.gameState match
                case GameStates.PLAYERSETTING => printWithPlayerAnnotation("set your name")
                case GameStates.SHIPSETTING   =>
                    printWithPlayerAnnotation("set your Ship <x y x y>")
                    printGrid(self)
                    printRemainingShips()
                case GameStates.IDLE          =>
                    printWithPlayerAnnotation("guess the enemy ship <x y>")
                    printGrid(enemy)
                    printGrid(self)
                case _                        => println("internal error")
        case _: GridUpdated          => controller.gameState match
                case GameStates.SHIPSETTING =>
                    printWithPlayerAnnotation("set your Ship <x y x y>")
                    printGrid(self)
                    printRemainingShips()
                case _                      => println("internal error")
        case _: RedoTurn             => controller.gameState match
                case GameStates.SHIPSETTING =>
                    printWithPlayerAnnotation("please try again")
                    println("Set your Ship <x y x y>")
                    printGrid(self)
                case GameStates.IDLE        =>
                    printWithPlayerAnnotation("please try again")
                    println("Guess the enemy ship <x y>")
                    printGrid(enemy)
                    printGrid(self)
                case _                      => println("internal error")
        case _: TurnAgain            => controller.gameState match
                case GameStates.IDLE =>
                    printWithPlayerAnnotation("That was a hit!")
                    println("Guess the enemy ship <x y>")
                    printGrid(enemy)
                    printGrid(self)
                case _               => println("internal error")
        case _: GameWon              =>
            printWithPlayerAnnotation("has won!!!")
            println(tui_options)
        case exception: FailureEvent => println(exception.getMessage())
        case _                       => println("internal error")
    }

    def tui_process: Unit =
        println("Welcome to Battleship")
        println(tui_options)

        while true do
            val input = scala.io.StdIn.readLine()
            input match
                case "s"          =>
                    println("Started game")
                    controller.publish(new GameStart)
                    controller.publish(new PlayerChanged)
                case "n"          => println("Starting new game")
                case "q"          =>
                    println(goodbye_message)
                    println("Exit game")
                    System.exit(0)
                case "h" => 
                    println(tui_options)
                    println(controller.gameState.getInfo)
                    println(controller.playerState.getInfo)
                case "save"       => println("Saving game")
                case "load"       => println("Loading game")
                case "state info" =>
                    println(controller.gameState.getInfo)
                    println(controller.playerState.getInfo)
                case input        => controller.doTurn(input)

    private def printWithPlayerAnnotation(msg: String) = controller.playerState match
        case PlayerStates.PLAYER_ONE => println(Console.MAGENTA + controller.player_01.name + Console.RESET + " " + msg)
        case PlayerStates.PLAYER_TWO => println(Console.CYAN + controller.player_02.name + Console.RESET + " " + msg)

    private def printGrid(who: Int)                    = who match {
        case this.self => controller.playerState match
                case PlayerStates.PLAYER_ONE =>
                    println(toStringHelper.gridToString(showAllShips, controller.player_01.grid.grid))
                case PlayerStates.PLAYER_TWO =>
                    println(toStringHelper.gridToString(showAllShips, controller.player_02.grid.grid))
        case this.enemy => controller.playerState match
                case PlayerStates.PLAYER_ONE =>
                    println(toStringHelper.gridToString(showNotAllShips, controller.player_02.grid.grid))
                case PlayerStates.PLAYER_TWO =>
                    println(toStringHelper.gridToString(showNotAllShips, controller.player_01.grid.grid))
    }

    private def printRemainingShips() =
        println("Remaining ships:")
        controller.playerState match
            case PlayerStates.PLAYER_ONE =>
                println(toStringHelper.shipSetListToString(controller.player_01.shipSetList))
            case PlayerStates.PLAYER_TWO =>
                println(toStringHelper.shipSetListToString(controller.player_02.shipSetList))
