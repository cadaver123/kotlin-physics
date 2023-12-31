package components;

import components.interfaces.Component

enum class CollisionType {
    ELASTIC,
    MERGE
}

data class Collider(var mass: Double, val type: CollisionType): Component