package components.generic

import components.interfaces.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock

open class Component2D : Component {
    var lastIndex = 0
    val x = DoubleArray(10000)
    val y = DoubleArray(10000)
    val entitiesMap = ConcurrentHashMap<Int, Int>(10000)
    val reentryDataLock = ReentrantReadWriteLock()

    fun attachComponentToEntity(entityId: Int, initialX: Double, initialY: Double) {
        reentryDataLock.writeLock().withLock {
            x[lastIndex] = initialX
            y[lastIndex] = initialY
            entitiesMap[entityId] = lastIndex++
        }
    }

    fun removeComponentFromEntity(entityId: Int) {
        reentryDataLock.writeLock().withLock {
            val index = entitiesMap.remove(entityId)!!
            x[index] = x[lastIndex - 1]
            y[index] = y[lastIndex - 1]
            lastIndex--
        }
    }

    fun getX(entityId: Int): Double {
        return x[entitiesMap[entityId]!!]
    }

    fun getY(entityId: Int): Double {
        return y[entitiesMap[entityId]!!]
    }
}

