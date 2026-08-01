package components

import com.raylib.Raylib
import components.interfaces.Component
import java.util.concurrent.ConcurrentHashMap

class ColorComponent : Component {
    var lastIndex = 0
    val colors = Raylib.Color(10000);
    val entitiesMap = ConcurrentHashMap<Int, Int>(10000)

    fun attach(entityId: Int, initialR: Byte, initialG: Byte, initialB: Byte) {
        colors.position(entityId.toLong()).r(initialR).g(initialG).b(initialB).a(255.toByte())
        entitiesMap[entityId] = lastIndex++
    }

    fun detach(entityId: Int) {
        val index = entitiesMap.remove(entityId)!!
        val lastColorInArray = colors.position((lastIndex - 1).toLong())
        colors.position(index.toLong()).r(lastColorInArray.r()).g(lastColorInArray.g()).b(lastColorInArray.b())
            .a(255.toByte())
        lastIndex--
    }
}