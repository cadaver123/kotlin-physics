package components

import Environment
import common.Vector
import components.interfaces.Component

data class Velocity(val vector: Vector) : Component {
    var vec: Vector = vector
        set(value) {
            if (value.length() <= Environment.MAX_VELOCITY) {
                field = value
            } else {
                field = value / value.length() * Environment.MAX_VELOCITY
            }
        }

    fun add(other: Vector): Velocity {
        vec = Vector(vec.x + other.x, vec.y + other.y)

        return this
    }

}