package common

import kotlin.math.pow
import kotlin.math.sqrt

data class Vector(var x: Double, var y: Double) {

    companion object {
        fun distance(x1: Double, y1: Double, x2: Double, y2: Double): Double {
            return sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2))
        }

        fun distanceSquared(x1: Double, y1: Double, x2: Double, y2: Double): Double {
            return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)
        }
    }

    operator fun plus(other: Vector): Vector {
        return Vector(x + other.x, y + other.y)
    }

    operator fun minus(other: Vector): Vector {
        return Vector(x - other.x, y - other.y)
    }

    operator fun times(other: Double): Vector {
        return Vector(x * other, y * other)
    }

    operator fun div(other: Double): Vector {
        return Vector(x / other, y / other)
    }

    fun getPerpendicularCounterClockwise(): Vector {
        return Vector(-y, x) / length();
    }

    fun norm(): Vector = Vector(x, y) / length()

    fun length(): Double = sqrt(x * x + y * y)

    infix fun dotProduct(other: Vector): Double = x * other.x + y * other.y

    fun distance(other: Vector): Double {
        return sqrt((x - other.x).pow(2.0) + (y - other.y).pow(2.0))
    }
}
