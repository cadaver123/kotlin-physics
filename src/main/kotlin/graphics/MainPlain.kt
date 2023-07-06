package graphics

import Environment
import java.awt.Graphics
import javax.swing.JPanel


internal class MainPlain : JPanel() {
    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        renderObjects(g)
    }

    private fun renderObjects(g: Graphics) {
        Environment.entities.forEach { RendererService.drawObject(it, g) };
    }
}
