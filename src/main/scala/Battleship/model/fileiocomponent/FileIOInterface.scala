package Battleship.model.fileiocomponent

trait FileIOInterface {

  def load: (InterfaceGrid, InterfaceGrid, InterfacePerson, InterfacePerson, Array[Int], Array[Int], InterfaceShip, Array[Int], Boolean, Boolean, String, GameState, PlayerState)

  def save(grid1: InterfaceGrid, grid2: InterfaceGrid, player: InterfacePerson, player2: InterfacePerson, shipSetting: Array[Int], shipSetting2: Array[Int], ship: InterfaceShip, shipCoordsSetting: Array[Int], shipSet: Boolean, shipDelete: Boolean, lastGuess: String, gameState: GameState, playerState: PlayerState): Unit

}
