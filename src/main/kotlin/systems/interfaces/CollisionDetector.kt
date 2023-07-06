package systems.interfaces

import entities.Entity

interface CollisionDetector {
    fun isColliding(e1: Entity, e2: Entity): Boolean
}