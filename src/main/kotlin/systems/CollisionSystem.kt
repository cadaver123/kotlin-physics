package systems

import Environment
import common.Vector
import components.Collider
import components.CollisionType
import components.GravitySource
import components.Position
import components.Velocity
import components.shapes.Circle
import entities.Entity
import processing.AABB
import processing.Quadtree
import systems.interfaces.SimulationSystem
import systems.service.CirclesCollisionDetector
import java.lang.Math.pow
import java.lang.Math.sqrt

private class EntityMergeVO(e: Entity) : EntityVO(e) {
    val gravity: GravitySource?
    private val circle: Circle

    var r: Double
        get() = circle.radius
        set(value) {
            circle.radius = value
        }

    init {
        circle = e.getComponent(Circle::class)!!
        gravity = e.getComponent(GravitySource::class)
    }
}


private class EntityElasticVO(e: Entity) : EntityVO(e) {
    private val positionComponent: Position

    val position: Vector
        get() = positionComponent.vec


    init {
        positionComponent = e.getComponent(Position::class)!!
    }
}

private open class EntityVO(e: Entity) {
    protected val velocity: Velocity
    protected val collider: Collider

    var v: Vector
        get() = velocity.vec
        set(value) {
            velocity.vec = value
        }

    var m: Double
        get() = collider.mass
        set(value) {
            collider.mass = value
        }

    init {
        velocity = e.getComponent(Velocity::class)!!
        collider = e.getComponent(Collider::class)!!
    }
}

class CollisionSystem : SimulationSystem {
    override fun updateState(delta: Double) {
        val entities = Environment.entities.filter { it.hasComponent(Collider::class) }
        val objectsToRemove = mutableSetOf<Entity>()
        val collidingObjectsMap = HashMap<Entity, HashSet<Entity>>()

        val tree = Quadtree(AABB(Environment.CENTER_POINT, Environment.ENV_SIZE.x, Environment.ENV_SIZE.y))
        entities.forEach {
            if (it.hasComponent(Circle::class)) {
                tree.insert(it)
            }
        }

        entities.forEach {
            val potentialCollidingEntities = tree.queryByCircle(it)
            for(potentialCollidingEntity in potentialCollidingEntities) {
                if (it !== potentialCollidingEntity && collidingObjectsMap[it]?.contains(potentialCollidingEntity) != true) {
                    if (CirclesCollisionDetector.isColliding(it, potentialCollidingEntity)) {
                        when {
                            isElasticCollision(it, potentialCollidingEntity) -> doElasticCollision(it, potentialCollidingEntity)
                            isMergeCollision(it, potentialCollidingEntity) == true -> doMergeCollision(it, potentialCollidingEntity, objectsToRemove)
                        }
                        if (collidingObjectsMap[potentialCollidingEntity] == null) {
                            collidingObjectsMap[potentialCollidingEntity] = HashSet.newHashSet(3)
                        }
                        collidingObjectsMap[potentialCollidingEntity]!!.add(it)
                    }
                }
            }
        }

        Environment.entities = Environment.entities.filter { !objectsToRemove.contains(it) }
    }

    private fun doMergeCollision(e1: Entity, e2: Entity, objectsToRemove: MutableSet<Entity>) {
        val e1VO = EntityMergeVO(e1)
        val e2VO = EntityMergeVO(e2)

        if (e1VO.r > e2VO.r) {
            merge(e1VO, e2VO)
            objectsToRemove.add(e2)
        } else {
            merge(e2VO, e1VO)
            objectsToRemove.add(e1)
        }
    }

    private fun merge(target: EntityMergeVO, objToBeMerged: EntityMergeVO) {
        val sumOfMasses = target.m + objToBeMerged.m
        val newV = (target.v * target.m + objToBeMerged.v * objToBeMerged.m) / sumOfMasses
        val newR = sqrt(pow(target.r, 2.0) + pow(objToBeMerged.r, 2.0))

        target.v = newV
        target.r = newR
        target.m = sumOfMasses

        if (target.gravity != null && objToBeMerged.gravity != null) {
            target.gravity.strength += objToBeMerged.gravity.strength
        }
    }

    private fun doElasticCollision(e1: Entity, e2: Entity) {
        changeVelocitiesAfterElasticCollision(e1, e2)
        moveObjects(e1, e2)
    }

    private fun moveObjects(e1: Entity, e2: Entity) {
        val dd = CirclesCollisionDetector.collisionVector(e1, e2)
        e1.getComponent(Position::class)!!.add(dd / 2.0)
        e2.getComponent(Position::class)!!.add(dd / -2.0)
    }

    private fun changeVelocitiesAfterElasticCollision(o1: Entity, o2: Entity) {
        val e1 = EntityElasticVO(o1)
        val e2 = EntityElasticVO(o2)

        e1.v = calculateVelocityAfterElasticCollision(e1, e2)
        e2.v = calculateVelocityAfterElasticCollision(e2, e1)
    }

    private fun calculateVelocityAfterElasticCollision(e1: EntityElasticVO, e2: EntityElasticVO) =
        e1.v - (e1.position - e2.position) * ((e1.v - e2.v) dotProduct (e1.position - e2.position)) * 2.0 * e2.m / ((e1.m + e2.m) * pow(
            e1.position.distance(e2.position),
            2.0
        ))

    private fun isElasticCollision(e1: Entity, e2: Entity): Boolean {
        if (e1.hasComponent(Velocity::class) && e2.hasComponent(Velocity::class)) {
            val collider = e1.getComponent(Collider::class)
            val otherCollider = e2.getComponent(Collider::class)

            return collider?.type == CollisionType.ELASTIC || otherCollider?.type == CollisionType.ELASTIC
        }

        return false
    }

    private fun isMergeCollision(e1: Entity, e2: Entity): Boolean {
        if (e1.hasComponent(Velocity::class) && e2.hasComponent(Velocity::class)) {
            val collider = e1.getComponent(Collider::class)
            val otherCollider = e2.getComponent(Collider::class)

            return collider?.type == CollisionType.MERGE && otherCollider?.type == CollisionType.MERGE
        }

        return false
    }
}

