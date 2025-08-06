package processing;

import common.Vector
import components.Position
import components.shapes.Circle
import entities.Entity


data class AABB(val center: Vector, val halfWidth: Double, val halfHeight: Double) {
    val top: Double
        get() {
            return center.y - halfHeight;
        }
    val bottom: Double
        get() {
            return center.y + halfHeight;
        }
    val left: Double
        get() {
            return center.x - halfWidth
        }
    val right: Double
        get() {
            return center.x + halfWidth;
        }

    fun intersectsCircle(circle: Entity, epsilon: Double = 0.0): Boolean {
        if (!circle.hasComponent(Circle::class) || !circle.hasComponent(Position::class)) {
            throw IllegalArgumentException("Should be circle")
        }
        var (radius) = circle.getComponent(Circle::class)!!
        radius += epsilon
        val (cx, cy) = circle.getComponent(Position::class)!!.vec

        val closestX: Double = (if (cx < left) left else (if (cx > right) right else cx))
        val closestY: Double = (if (cy < top) top else (if (cy > bottom) bottom else cy))
        val dx: Double = closestX - cx
        val dy: Double = closestY - cy

        return (dx * dx + dy * dy) <= radius * radius
    }
}
