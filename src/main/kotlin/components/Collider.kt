package components;

import components.interfaces.Component

enum class CollisionType {
    ELASTIC,
    MERGE
}

data class Collider(var weight: Double, val type: CollisionType): Component