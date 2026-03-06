package components

import com.raylib.Raylib
import components.interfaces.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock

class ColorComponent : Component {
    var lastIndex = 0
    val colors = Raylib.Color(10000);
    val entitiesMap = ConcurrentHashMap<Int, Int>(10000)
    val reentryDataLock = ReentrantReadWriteLock()

    fun attachWithLock(entityId: Int, initialR: Byte, initialG: Byte, initialB: Byte) {
        reentryDataLock.writeLock().withLock {
            colors.position(entityId.toLong()).r(initialR).g(initialG).b(initialB).a(255.toByte())
            entitiesMap[entityId] = lastIndex++
        }
    }

    fun detachWithLock(entityId: Int) {
        reentryDataLock.writeLock().withLock {
            val index = entitiesMap.remove(entityId)!!
            val lastColorInArray = colors.position((lastIndex - 1).toLong())
            colors.position(index.toLong()).r(lastColorInArray.r()).g(lastColorInArray.g()).b(lastColorInArray.b()).a(255.toByte())
            lastIndex--
        }
    }
}