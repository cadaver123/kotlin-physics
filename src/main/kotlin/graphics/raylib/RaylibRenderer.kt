package graphics.raylib

import com.raylib.Raylib
import com.raylib.Raylib.DrawCircle
import components.Position
import components.shapes.Circle
import entities.Entity

class RaylibRenderer {
    companion object {
        fun drawObject(entity: Entity) {
            drawCircle(entity)
        }

        fun drawCircle(entity: Entity) {
            if (entity.hasComponent(Circle::class)) {
                val (r, color) = entity.getComponent(Circle::class)!!
                if (r > 1.0) {
                    val (x, y) = entity.getComponent(Position::class)!!.vec
                    val raylibColor = Raylib.Color().r(color.r).g(color.g).b(color.b).a(255.toByte())
                    DrawCircle(x.toInt(), y.toInt(), r.toFloat(), raylibColor)
                }
            }
        }
    }
}