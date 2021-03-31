package Battleship.aview.gui.panel

import java.awt.image.BufferedImage
import scala.swing.{Graphics2D, Panel}

class ImagePanel extends Panel {
  private var bufferedImage: BufferedImage = null

  def imagePath(bufferedImageInsert: BufferedImage) {
    bufferedImage = bufferedImageInsert
  }

  override def paintComponent(g: Graphics2D) = {
    if (null != bufferedImage) g.drawImage(bufferedImage, 0, 0, null)
  }
}

object ImagePanel {
  def apply() = new ImagePanel()
}
