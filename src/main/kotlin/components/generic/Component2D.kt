package components.generic

import components.interfaces.Component
import java.util.concurrent.ConcurrentHashMap

open class Component2D(n: Int) : Component {
    var lastIndex = 0
    val x = DoubleArray(n)
    val y = DoubleArray(n)
    val entitiesMap = ConcurrentHashMap<Int, Int>(10000)

    fun attachComponentToEntity(entityId: Int, initialX: Double, initialY: Double) {
        x[lastIndex] = initialX
        y[lastIndex] = initialY
        entitiesMap[entityId] = lastIndex++
    }

    fun removeComponentFromEntity(entityId: Int) {
        val index = entitiesMap.remove(entityId)!!
        x[index] = x[lastIndex - 1]
        y[index] = y[lastIndex - 1]
        lastIndex--
    }

    fun getX(entityId: Int): Double {
        return x[entitiesMap[entityId]!!]
    }

    fun getY(entityId: Int): Double {
        return y[entitiesMap[entityId]!!]
    }
}

