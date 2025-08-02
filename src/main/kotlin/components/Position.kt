package components

import Environment
import common.Vector
import components.interfaces.Component

data class Position(val vec: Vector) : Component {
    fun add(other: Vector): Position {
        with(vec) {
            x = when {
                Environment.FINITE_PLANE && x + other.x < 0 -> Environment.ENV_SIZE.x - x + other.x
                Environment.FINITE_PLANE &&  x + other.x > Environment.ENV_SIZE.x -> x + other.x - Environment.ENV_SIZE.x
                else -> x + other.x
            }
            y = when {
                Environment.FINITE_PLANE &&  y + other.y < 0 -> Environment.ENV_SIZE.y - y + other.y
                Environment.FINITE_PLANE &&  y + other.y > Environment.ENV_SIZE.y -> y + other.y - Environment.ENV_SIZE.y
                else -> y + other.y
            }
        }

        return this
    }
}