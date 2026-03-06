package components.generic

import components.interfaces.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock

open class Component1D : Component {
    var lastIndex = 0
    val values = DoubleArray(10000)
    val entitiesMap = ConcurrentHashMap<Int, Int>(10000)
    val reentryDataLock = ReentrantReadWriteLock()

    fun attachWithLock(entityId: Int, initialValue: Double) {
        reentryDataLock.writeLock().withLock {
            values[lastIndex] = initialValue
            entitiesMap[entityId] = lastIndex++
        }
    }

    fun detachWithLock(entityId: Int) {
        reentryDataLock.writeLock().withLock {
            val index = entitiesMap.remove(entityId)!!
            values[index] = values[lastIndex - 1]
            lastIndex--
        }
    }
}
