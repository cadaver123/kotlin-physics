package components.generic

import components.interfaces.Component

open class Component1D(n: Int) : Component {
    var freeIdx = 0
    val values = DoubleArray(n)
    val entitiesMap = HashMap<Int, Int>(n)
    val idxToEntity = HashMap<Int, Int>(n)

    fun attach(entityId: Int, initialValue: Double) {
        values[freeIdx] = initialValue
        entitiesMap[entityId] = freeIdx
        idxToEntity[freeIdx] = entityId
        freeIdx++
    }

    override fun detach(entityId: Int) {
        val hole = entitiesMap.remove(entityId)!!
        val lastElementIdx = freeIdx - 1
        if(hole != lastElementIdx) {
            values[hole] = values[lastElementIdx]
            val movedEntityId = idxToEntity[lastElementIdx]!!
            idxToEntity[hole] = movedEntityId
            entitiesMap[movedEntityId] = hole
        }
        idxToEntity.remove(lastElementIdx)
        freeIdx--
    }
}
