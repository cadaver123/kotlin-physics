package components.generic

import components.interfaces.Component

open class Component1D(n: Int) : Component {
    var lastIndex = 0
    val values = DoubleArray(n)
    val entitiesMap = HashMap<Int, Int>(n)

    fun attach(entityId: Int, initialValue: Double) {
        values[lastIndex] = initialValue
        entitiesMap[entityId] = lastIndex++
    }

    fun detach(entityId: Int) {
        val index = entitiesMap.remove(entityId)!!
        values[index] = values[lastIndex - 1]
        lastIndex--
    }
}
