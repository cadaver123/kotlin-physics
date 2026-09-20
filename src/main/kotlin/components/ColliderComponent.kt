package components;

import components.interfaces.Component

enum class CollisionType {
    ELASTIC,
    MERGE
}

class ColliderComponent : Component {

    var freeIdx = 0
    val entitiesMap = HashMap<Int, Int>(10000)
    var mass = DoubleArray(10000)
    val type = Array<CollisionType>(10000) { CollisionType.ELASTIC }
    val idxToEntity = HashMap<Int, Int>(10000)

    fun attach(entityId: Int, mass: Double, type: CollisionType) {
        this.mass[freeIdx] = mass
        this.type[freeIdx] = type
        idxToEntity[freeIdx] = entityId
        entitiesMap[entityId] = freeIdx++
    }

    override fun detach(entityId: Int) {
        val hole = entitiesMap.remove(entityId)!!
        val lastElementIdx = freeIdx - 1
        if(hole != lastElementIdx) {
            mass[hole] = mass[lastElementIdx]
            type[hole] = type[lastElementIdx]
            val movedEntityId = idxToEntity[lastElementIdx]!!
            idxToEntity[hole] = movedEntityId
            entitiesMap[movedEntityId] = hole
        }
        idxToEntity.remove(lastElementIdx)
        freeIdx--
    }
}


