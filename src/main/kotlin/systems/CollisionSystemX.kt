package systems

import Environment
import common.Vector
import components.ColliderComponent
import components.CollisionType
import components.ComponentType
import components.ComponentsManager
import components.generic.Component1D
import components.generic.Component2D
import entities.Entity
import kotlin.math.sqrt
import systems.interfaces.SimulationSystem


class CollisionSystemX : SimulationSystem {
    private var gridQueryBuffer = IntArray(10000)
    private var circles: Component1D
    private var positions: Component2D
    private var collisions: ColliderComponent
    private var velocities: Component2D


    init {
        circles = ComponentsManager.getComponent(ComponentType.SHAPE_CIRCLE) as Component1D
        positions = ComponentsManager.getComponent(ComponentType.POSITION) as Component2D
        collisions = ComponentsManager.getComponent(ComponentType.COLLISION) as ColliderComponent
        velocities = ComponentsManager.getComponent(ComponentType.VELOCITY) as Component2D
    }

    override fun updateState(delta: Double) {
        val objectsToRemove = mutableSetOf<Entity>()


/*
        for (idx in 0 until queryResultCount) {
            val entityId = this.queryBuffer[idx]
            val x = positions.x[entityId].toInt()
            val y = positions.y[entityId].toInt()*/
        for (entityPos in 0 until Environment.entities.size)  {
            val entity = Environment.entities[entityPos]
            if (objectsToRemove.contains(entity) || checkRequiredComponents(entity)) {
                continue
            }
            val id1 = entity.id
            var p1x = positions.x[positions.entitiesMap[id1]!!]
            var p1y = positions.y[positions.entitiesMap[id1]!!]
            var r1 = circles.values[circles.entitiesMap[id1]!!]
            val resultSize: Int = Environment.grid.query(p1x - r1, p1x + r1, p1y - r1, p1y + r1, this.gridQueryBuffer)
            for(idx in 0 until resultSize) {
                if (entityPos < Environment.entitiesMap[gridQueryBuffer[idx]]!!) {
                    val otherEntity = Environment.entities[Environment.entitiesMap[gridQueryBuffer[idx]]!!]
                    if (objectsToRemove.contains(otherEntity) || checkRequiredComponents(otherEntity)) {
                        continue
                    }

                    val id2 = otherEntity.id
                    val p2x = positions.x[positions.entitiesMap[id2]!!]
                    val p2y = positions.y[positions.entitiesMap[id2]!!]
                    val r2 = circles.values[circles.entitiesMap[id2]!!]
                    if (distanceSquared( p1x, p1y,  p2x, p2y) <= (r1 + r2) * (r1 + r2)) {
                        val v1x = velocities.x[velocities.entitiesMap[id1]!!]
                        val v1y = velocities.y[velocities.entitiesMap[id1]!!]
                        val m1 = collisions.mass[collisions.entitiesMap[id1]!!]
                        val collisionType1 = collisions.type[collisions.entitiesMap[id1]!!]

                        val v2x = velocities.x[velocities.entitiesMap[id2]!!]
                        val v2y = velocities.y[velocities.entitiesMap[id2]!!]
                        val m2 = collisions.mass[collisions.entitiesMap[id2]!!]
                        val collisionType2 = collisions.type[collisions.entitiesMap[id2]!!]
                        when {
                            collisionType1 == CollisionType.ELASTIC || collisionType2 == CollisionType.ELASTIC -> doElasticCollision(id1, v1x, v1y, p1x, p1y, m1, r1, id2, v2x, v2y, p2x, p2y, m2, r2)
                            collisionType1 == CollisionType.MERGE && collisionType2 == CollisionType.MERGE -> {
                                doMergeCollision(id1, v1x, v1y, p1x, p1y, m1, r1, id2, v2x, v2y, p2x, p2y, m2, r2)
                                p1x = positions.x[positions.entitiesMap[id1]!!]
                                p1y = positions.y[positions.entitiesMap[id1]!!]
                                r1 = circles.values[circles.entitiesMap[id1]!!]
                                objectsToRemove.add(otherEntity)
                            }
                        }
                    }
                }
            }
        }

        Environment.removeEntities(objectsToRemove)
    }

    private fun checkRequiredComponents(otherEntity: Entity): Boolean = !otherEntity.hasComponent(ComponentType.SHAPE_CIRCLE) || !otherEntity.hasComponent(ComponentType.COLLISION) || !otherEntity.hasComponent(ComponentType.VELOCITY) || !otherEntity.hasComponent(ComponentType.POSITION)


    private fun doMergeCollision(id1: Int, v1x : Double, v1y : Double, p1x : Double, p1y : Double, m1 : Double, r1: Double, id2 : Int, v2x: Double, v2y: Double, p2x: Double, p2y: Double, m2: Double, r2: Double) {
        val sumOfMasses = m1 + m2
        val newVx = (v1x * m1 + v2x * m2) / sumOfMasses
        val newVy = (v1y * m1 + v2y * m2) / sumOfMasses
        val newR = sqrt(r1*r1 + r2*r2)
        val newPScaleFactor = m2 / (m1 + m2)
        val newPx = p1x + newPScaleFactor * (p2x - p1x)
        val newPy = p1y + newPScaleFactor * (p2y - p1y)

        velocities.x[velocities.entitiesMap[id1]!!] = newVx
        velocities.y[velocities.entitiesMap[id1]!!] = newVy

        circles.values[circles.entitiesMap[id1]!!] = newR
        collisions.mass[collisions.entitiesMap[id1]!!] = sumOfMasses

        //Move objects
        positions.x[positions.entitiesMap[id1]!!] = newPx
        positions.y[positions.entitiesMap[id1]!!] =  newPy

        Environment.grid.updateGrid(id1)

    }



/*    private fun merge(target: EntityMergeVO, objToBeMerged: EntityMergeVO) {
        val sumOfMasses = target.m + objToBeMerged.m
        val newVx = (target.v.x * target.m + objToBeMerged.v.x * objToBeMerged.m) / sumOfMasses
        val newVy = (target.v.y * target.m + objToBeMerged.v.y * objToBeMerged.m) / sumOfMasses
        val newR = sqrt(pow(target.r, 2.0) + pow(objToBeMerged.r, 2.0))

        target.v.x = newVx
        target.v.y = newVy
        target.r = newR
        target.m = sumOfMasses

        if (target.gravity != null && objToBeMerged.gravity != null) {
            target.gravity.strength += objToBeMerged.gravity.strength
        }
    }*/

    private fun doElasticCollision(id1: Int, v1x : Double, v1y : Double, p1x : Double, p1y : Double, m1 : Double, r1: Double, id2 : Int, v2x: Double, v2y: Double, p2x: Double, p2y: Double, m2: Double, r2: Double) {
        val distSquared = distanceSquared(p1x, p1y, p2x, p2y)
        val dotProduct = (v1x - v2x)*(p1x - p2x) + (v1y - v2y)*(p1y  - p2y)
        val massSum = m1 + m2
        val newV1x = newVelocity(v1x, p1x, p2x, dotProduct, m2, massSum, distSquared)
        val newV1y = newVelocity(v1y, p1y, p2y, dotProduct, m2, massSum, distSquared)

        val newV2x = newVelocity(v2x, p2x, p1x, dotProduct, m1, massSum, distSquared)
        val newV2y = newVelocity(v2y, p2y, p1y, dotProduct, m1, massSum, distSquared)

        velocities.x[velocities.entitiesMap[id1]!!] = newV1x
        velocities.y[velocities.entitiesMap[id1]!!] = newV1y

        velocities.x[velocities.entitiesMap[id2]!!] = newV2x
        velocities.y[velocities.entitiesMap[id2]!!] = newV2y

        //Move objects
        val collisionVectorX = collisionVector(p1x, p2x, r1, r2, distSquared)
        val collisionVectorY = collisionVector(p1y, p2y, r1, r2, distSquared)
        positions.x[positions.entitiesMap[id1]!!] += collisionVectorX / 2.0
        positions.y[positions.entitiesMap[id1]!!] += collisionVectorY / 2.0
        positions.x[positions.entitiesMap[id2]!!] += collisionVectorX / -2.0
        positions.y[positions.entitiesMap[id2]!!] += collisionVectorY / -2.0
    }

    private fun collisionVector(p1: Double, p2: Double, r1: Double, r2: Double, distSquared: Double): Double =
        (p1 - p2) * ((r1 + r2 - sqrt(distSquared)) / (r1 + r2))

    private fun newVelocity(v: Double, p1: Double, p2: Double, dotProduct: Double, m2: Double, massSum: Double, distSquared: Double): Double =
        v - (p1 - p2) * dotProduct * 2.0 * m2 / (massSum * distSquared)


    private fun isColliding(r1: Double, x1: Double, y1: Double, r2: Double, x2: Double, y2: Double): Boolean {
        return Vector.distance(x1, y1, x2, y2) <= r1 + r2
    }

    fun distanceSquared(x1: Double, y1: Double, x2: Double, y2: Double): Double {
        return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)
    }
}

