package systems.service

import common.Vector
import components.Position
import components.shapes.Circle
import entities.Entity
import systems.interfaces.CollisionDetector

class CirclesCollisionDetector private constructor() {
    companion object : CollisionDetector {
        override fun isColliding(e1: Entity, e2: Entity): Boolean {
            if (!e1.hasComponent(Circle::class) ||
                !e1.hasComponent(Position::class) ||
                !e2.hasComponent(Circle::class) ||
                !e2.hasComponent(Position::class)
            ) {
                return false
            }

            val (r1) = e1.getComponent(Circle::class)!!
            val (p1) = e1.getComponent(Position::class)!!
            val (r2) = e2.getComponent(Circle::class)!!
            val (p2) = e2.getComponent(Position::class)!!

            return p1.distance(p2) <= r1 + r2
        }

        fun collisionVector(e1: Entity, e2: Entity): Vector {
            val (r1) = e1.getComponent(Circle::class)!!
            val (p1) = e1.getComponent(Position::class)!!
            val (r2) = e2.getComponent(Circle::class)!!
            val (p2) = e2.getComponent(Position::class)!!

            return if (isColliding(e1, e2))
                (p1 - p2) * ((r1 + r2 - p1.distance(p2)) / (r1 + r2))
            else
                Vector(.0, .0)
        }
    }
}