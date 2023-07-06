package components

import Environment
import common.Vector
import components.interfaces.Component
import java.lang.Math.pow
import kotlin.math.sqrt

data class Velocity(var vector: Vector) : Component {
    fun add(other: Vector): Velocity {
        val temp = Vector(vector.x + other.x, vector.y + other.y);

        if (temp.length() <= Environment.MAX_VELOCITY) {
            vector.x = temp.x
            vector.y = temp.y
        }

        return this;
    };
}