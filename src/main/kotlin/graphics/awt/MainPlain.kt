package graphics.awt

import Environment
import components.shapes.Circle
import processing.AABB
import processing.Quadtree
import java.awt.Graphics
import java.util.ArrayDeque
import javax.swing.JPanel


internal class MainPlain : JPanel() {
    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        renderObjects(g)
    }

    private fun renderObjects(g: Graphics) {

        Environment.entities.forEach {
            AwtRenderer.drawObject(it, g) }

        //printQuadtree(g)
    }

    private fun printQuadtree(g: Graphics) {
        var tree = Quadtree(AABB(Environment.CENTER_POINT, Environment.ENV_SIZE.x, Environment.ENV_SIZE.y))
        Environment.entities.forEach {
            if (it.hasComponent(Circle::class)) {
                tree.insert(it)
            }
        }
        val q = ArrayDeque<Quadtree>()
        q.add(tree)
        while (q.isNotEmpty()) {
            tree = q.pop()
            if (tree.divided) {
                q.add(tree.northwest!!)
                q.add(tree.northeast!!)
                q.add(tree.southeast!!)
                q.add(tree.southwest!!)
            } else {
                g.drawRect(
                    tree.boundaries.left.toInt(), tree.boundaries.top.toInt(),
                    (tree.boundaries.halfWidth * 2).toInt(), (tree.boundaries.halfHeight * 2).toInt()
                )
            }
        }
    }
}
