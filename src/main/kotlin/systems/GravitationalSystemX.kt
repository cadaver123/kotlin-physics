package systems

import Environment
import common.IntLinkedList
import common.distance
import components.ColliderComponent
import components.ComponentType
import components.ComponentsManager
import components.generic.Component1D
import components.generic.Component2D
import kotlin.math.max
import kotlin.math.pow
import systems.interfaces.SimulationSystem


class GravitationalSystemX() : SimulationSystem {
    private val MAX_QTREE_LVL = 7
    private val THETA = 1.3
    private val EPSILON_2 = 2
    val TREE_MAX_SIZE = ((4.0.pow(MAX_QTREE_LVL+1) - 1)/3.0).toInt()

    private val plainSize = max(Environment.WIDTH, Environment.HEIGHT)
    private val halfSizes = DoubleArray(MAX_QTREE_LVL + 2) { i -> plainSize * 1.0 / 2.0.pow(1.0 * i + 1.0); }

    private var aabbCenterX = DoubleArray(TREE_MAX_SIZE)
    private var aabbCenterY = DoubleArray(TREE_MAX_SIZE)
    private var aabbMassSum = DoubleArray(TREE_MAX_SIZE)
    private var aabbPosMassX = DoubleArray(TREE_MAX_SIZE)
    private var aabbPosMassY = DoubleArray(TREE_MAX_SIZE)
    private var leafsLinkedLists = IntLinkedList()
    private var aabbLvl = IntArray(10000) { -1 }
    private var aabbChildIndex = IntArray(TREE_MAX_SIZE) { -1 }
    private var aabbLeafHeads = IntArray(TREE_MAX_SIZE) { -1 }
    private var circles: Component1D
    private var masses: Component1D
    private var positions: Component2D
    private var collisions: ColliderComponent
    private var velocities: Component2D
    private var accelerations: Component2D
    private var firstFreeIdx = 0

    init {
        circles = ComponentsManager.getComponent(ComponentType.SHAPE_CIRCLE) as Component1D
        masses = ComponentsManager.getComponent(ComponentType.MASS) as Component1D
        positions = ComponentsManager.getComponent(ComponentType.POSITION) as Component2D
        collisions = ComponentsManager.getComponent(ComponentType.COLLISION) as ColliderComponent
        velocities = ComponentsManager.getComponent(ComponentType.VELOCITY) as Component2D
        accelerations = ComponentsManager.getComponent(ComponentType.ACCELERATION) as Component2D
        aabbCenterX[0] = plainSize / 2.0
        aabbCenterY[0] = plainSize / 2.0
    }


    override fun updateState(dt: Double) {
        leafsLinkedLists.clear()
        clearNodeData(0)
        firstFreeIdx = 1

        for (entity in Environment.entities) {
            val id = entity.id
            val x = positions.x[positions.entitiesMap[id]!!]
            val y = positions.y[positions.entitiesMap[id]!!]
            addToQuadTree(x, y, id, 0, 0)
        }
        for (entity in Environment.entities) {
            val id = entity.id
            val x = positions.x[positions.entitiesMap[id]!!]
            val y = positions.y[positions.entitiesMap[id]!!]
            calculateAcceleration(x, y, 0, 0, id)
        }
    }

    fun calculateAcceleration(x: Double, y: Double, idx: Int, lvl: Int, entityId: Int) {
        val comX = aabbPosMassX[idx] / aabbMassSum[idx]
        val comY = aabbPosMassY[idx] / aabbMassSum[idx]
        val d = distance(x, y, comX, comY)
        val comVectorX = (comX - x)
        val comVectorY = (comY - y)
        val accDenominator = Math.pow(d * d + EPSILON_2, 1.5)
        if (d > 2 * halfSizes[lvl] / THETA + distance(aabbCenterX[idx], aabbCenterY[idx], comX, comY)) {
            accelerations.x[accelerations.entitiesMap[entityId]!!] += Environment.GRAVITATIONAL_CONSTANT * aabbMassSum[idx] * comVectorX / accDenominator
            accelerations.y[accelerations.entitiesMap[entityId]!!] += Environment.GRAVITATIONAL_CONSTANT * aabbMassSum[idx] * comVectorY / accDenominator
            return
        }

        if (aabbChildIndex[idx] == -1) {
            var currentEl = aabbLeafHeads[idx]
            if (currentEl == -1)
                return
            do {
                val nextEntity = leafsLinkedLists.values[currentEl]
                val mass = masses.values[masses.entitiesMap[nextEntity]!!]
                val entityPositionId = positions.entitiesMap[nextEntity]!!
                val entityPosX = positions.x[entityPositionId]
                val entityPosY = positions.y[entityPositionId]
                val distanceToEntity = distance(x, y, entityPosX, positions.y[entityPositionId])
                val accDenominatorForEntity = Math.pow(distanceToEntity * distanceToEntity + EPSILON_2, 1.5)

                accelerations.x[accelerations.entitiesMap[entityId]!!] += Environment.GRAVITATIONAL_CONSTANT * mass * (entityPosX - x) / accDenominatorForEntity
                accelerations.y[accelerations.entitiesMap[entityId]!!] += Environment.GRAVITATIONAL_CONSTANT * mass * (entityPosY - y) / accDenominatorForEntity
                currentEl = leafsLinkedLists.nextElIds[currentEl]

            } while (currentEl != -1)
            return
        }

        calculateAcceleration(x, y, aabbChildIndex[idx], lvl + 1, entityId)
        calculateAcceleration(x, y, aabbChildIndex[idx] + 1, lvl + 1, entityId)
        calculateAcceleration(x, y, aabbChildIndex[idx] + 2, lvl + 1, entityId)
        calculateAcceleration(x, y, aabbChildIndex[idx] + 3, lvl + 1, entityId)
    }

    fun addToQuadTree(x: Double, y: Double, entityId: Int, idx: Int, lvl: Int): Boolean {
        if (!isInBorders(x, y, idx, lvl)) {
            return false
        }

        val mass = masses.values[masses.entitiesMap[entityId]!!]
        if (aabbChildIndex[idx] == -1 && (lvl >= MAX_QTREE_LVL || aabbLeafHeads[idx] == -1)) {
            aabbMassSum[idx] += mass
            aabbPosMassX[idx] += positions.x[positions.entitiesMap[entityId]!!] * mass
            aabbPosMassY[idx] += positions.y[positions.entitiesMap[entityId]!!] * mass

            aabbLeafHeads[idx] = leafsLinkedLists.add(entityId, aabbLeafHeads[idx])
            return true;
        }

        if (aabbChildIndex[idx] == -1) {
            aabbChildIndex[idx] = firstFreeIdx

            val centerX = aabbCenterX[idx]
            val centerY = aabbCenterY[idx]
            val newHalfSize = halfSizes[lvl + 1]

            //NW
            aabbCenterX[firstFreeIdx] = centerX - newHalfSize
            aabbCenterY[firstFreeIdx] = centerY + newHalfSize
            clearNodeData(firstFreeIdx)
            firstFreeIdx += 1
            //NE
            aabbCenterX[firstFreeIdx] = centerX + newHalfSize
            aabbCenterY[firstFreeIdx] = centerY + newHalfSize
            clearNodeData(firstFreeIdx)
            firstFreeIdx += 1
            //SE
            aabbCenterX[firstFreeIdx] = centerX + newHalfSize
            aabbCenterY[firstFreeIdx] = centerY - newHalfSize
            clearNodeData(firstFreeIdx)
            firstFreeIdx += 1
            //SW
            aabbCenterX[firstFreeIdx] = centerX - newHalfSize
            aabbCenterY[firstFreeIdx] = centerY - newHalfSize
            clearNodeData(firstFreeIdx)
            firstFreeIdx += 1

            val entityToBeMoved = leafsLinkedLists.values[aabbLeafHeads[idx]]
            val entityToBeMovedPositionIdx = positions.entitiesMap[entityToBeMoved]!!
            val entityToBeMovedX = positions.x[entityToBeMovedPositionIdx]
            val entityToBeMovedY = positions.y[entityToBeMovedPositionIdx]
            leafsLinkedLists.erase(aabbLeafHeads[idx])
            aabbLeafHeads[idx] = -1

            var entityToBeMovedIsInserted =
                addToQuadTree(entityToBeMovedX, entityToBeMovedY, entityToBeMoved, aabbChildIndex[idx], lvl + 1);
            entityToBeMovedIsInserted = entityToBeMovedIsInserted || addToQuadTree(
                entityToBeMovedX,
                entityToBeMovedY,
                entityToBeMoved,
                aabbChildIndex[idx] + 1,
                lvl + 1
            );
            entityToBeMovedIsInserted = entityToBeMovedIsInserted || addToQuadTree(
                entityToBeMovedX,
                entityToBeMovedY,
                entityToBeMoved,
                aabbChildIndex[idx] + 2,
                lvl + 1
            );
            entityToBeMovedIsInserted || addToQuadTree(
                entityToBeMovedX,
                entityToBeMovedY,
                entityToBeMoved,
                aabbChildIndex[idx] + 3,
                lvl + 1
            );
        }

        aabbMassSum[idx] += mass
        aabbPosMassX[idx] += positions.x[positions.entitiesMap[entityId]!!] * mass
        aabbPosMassY[idx] += positions.y[positions.entitiesMap[entityId]!!] * mass


        var isInserted = addToQuadTree(x, y, entityId, aabbChildIndex[idx], lvl + 1);
        isInserted = isInserted || addToQuadTree(x, y, entityId, aabbChildIndex[idx] + 1, lvl + 1)
        isInserted = isInserted || addToQuadTree(x, y, entityId, aabbChildIndex[idx] + 2, lvl + 1)
        isInserted = isInserted || addToQuadTree(x, y, entityId, aabbChildIndex[idx] + 3, lvl + 1)
        return isInserted;
    }

    private fun clearNodeData(idx: Int) {
        aabbChildIndex[idx] = -1
        aabbLeafHeads[idx] = -1
        aabbMassSum[idx] = 0.0
        aabbPosMassX[idx] = 0.0
        aabbPosMassY[idx] = 0.0
    }

    fun isInBorders(x: Double, y: Double, idx: Int, lvl: Int): Boolean {
        val centerX = aabbCenterX[idx]
        val centerY = aabbCenterY[idx]
        val halfSize = halfSizes[lvl]
        return centerX - halfSize <= x && x < centerX + halfSize && centerY - halfSize <= y && y < centerY + halfSize
    }


}