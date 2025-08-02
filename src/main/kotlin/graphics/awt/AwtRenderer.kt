package graphics.awt

import components.Position
import components.shapes.Circle
import entities.Entity
import java.awt.Color
import java.awt.Graphics

class AwtRenderer private constructor() {
    companion object {
        fun drawObject(entity: Entity, g: Graphics) {
            drawCircle(entity, g)
        }

        fun drawCircle(entity: Entity, g: Graphics) {
            if (entity.hasComponent(Circle::class)) {
                val (r, color) = entity.getComponent(Circle::class)!!
                if (r > 2.0) {
                    val (x, y) = entity.getComponent(Position::class)!!.vec
                    val originalColor = g.getColor()

                    g.setColor(Color(color.r.toInt(), color.g.toInt(), color.b.toInt()))
                    g.fillOval((x - r).toInt(), (y - r).toInt(), (r * 2).toInt(), (r * 2).toInt())
                    g.setColor(originalColor)
                }
            }
        }
    }
}