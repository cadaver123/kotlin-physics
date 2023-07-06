package graphics

import components.Position
import components.shapes.Circle
import entities.Entity
import java.awt.Graphics

class RendererService private constructor() {
    companion object {
        fun drawObject(entity: Entity, g: Graphics) {
            drawCircle(entity, g)
        }

        fun drawCircle(entity: Entity, g: Graphics) {
            if (entity.hasComponent(Circle::class)) {
                val (r, color) = entity.getComponent(Circle::class)!!
                val (x, y) = entity.getComponent(Position::class)!!.vector
                val orginalColor = g.getColor()

                g.setColor(color)
                g.fillOval((x - r).toInt(), (y - r).toInt(), (r * 2).toInt(), (r * 2).toInt());

                g.setColor(orginalColor);
            }
        }
    }
}