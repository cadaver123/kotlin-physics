package systems

import Environment
import common.Vector
import components.*
import components.shapes.Circle
import entities.Entity
import systems.interfaces.SimulationSystem
import systems.service.CirclesCollisionDetector
import java.lang.Math.pow
import java.lang.Math.sqrt

class CollisionSystem : SimulationSystem {

    override fun updateState(delta: Double) {
        val entities = Environment.entities.filter { it.hasComponent(Collider::class) }
        val objectsToRemove = mutableSetOf<Entity>()

        for (i in entities.indices) {
            for (j in i + 1 until entities.size) {
                val e1 = entities.get(i)
                val e2 = entities.get(j)

                if (CirclesCollisionDetector.isColliding(e1, e2)) {
                    when {
                        isElasticCollision(e1, e2) == true -> doElasticCollision(e1, e2)
                        isMergeCollision(e1, e2) == true -> doMergeCollision(e1, e2, objectsToRemove)
                    }
                }
            }
        }

        Environment.entities = Environment.entities.filter { !objectsToRemove.contains(it) }
    }

    private fun doMergeCollision(e1: Entity, e2: Entity, objectsToRemove: MutableSet<Entity>) {
        val (r1) = e1.getComponent(Circle::class)!!
        val (r2) = e2.getComponent(Circle::class)!!

        if (r1 > r2) {
            merge(e1, e2)
            objectsToRemove.add(e2)
        } else {
            merge(e2, e1)
            objectsToRemove.add(e1)
        }
    }

    private fun merge(target: Entity, objToBeMerged: Entity) {
        val targetV = target.getComponent(Velocity::class)!!
        val targetR = target.getComponent(Circle::class)!!
        val targetM = target.getComponent(Collider::class)!!
        val targetG = target.getComponent(GravitySource::class)
        val otherG = objToBeMerged.getComponent(GravitySource::class)

        val (v1) = targetV
        val (v2) = objToBeMerged.getComponent(Velocity::class)!!
        val (m1) = targetM
        val (m2) = objToBeMerged.getComponent(Collider::class)!!
        val (r1) = targetR
        val (r2) = objToBeMerged.getComponent(Circle::class)!!

        val newV = (v1 * m1 + v2 * m2) / (m1 + m2)
        val newR = sqrt(pow(r1, 2.0) + pow(r2, 2.0))

        targetV.vector = newV
        targetR.radius = newR
        targetM.weight = m1 + m2

        if (targetG != null && otherG != null) {
            targetG.strength += otherG.strength
        }
    }

    private fun doElasticCollision(e1: Entity, e2: Entity) {
        changeVelocitiesInElastic(e1, e2)
        moveObjects(e1, e2)
    }

    private fun moveObjects(e1: Entity, e2: Entity) {
        val dd = CirclesCollisionDetector.collisionVector(e1, e2)
        e1.getComponent(Position::class)!!.add(dd / 2.0)
        e2.getComponent(Position::class)!!.add(dd / -2.0)
    }

    private fun changeVelocitiesInElastic(e1: Entity, e2: Entity) {
        val velocity = e1.getComponent(Velocity::class)!!
        val otherVelocity = e2.getComponent(Velocity::class)!!
        val (v1) = velocity
        val (m1) = e1.getComponent(Collider::class)!!
        val (x1) = e1.getComponent(Position::class)!!
        val (v2) = otherVelocity
        val (m2) = e2.getComponent(Collider::class)!!
        val (x2) = e2.getComponent(Position::class)!!

        velocity.vector = newElasticCollistionVelocity(v1, m1, x1, v2, m2, x2)
        otherVelocity.vector = newElasticCollistionVelocity(v2, m2, x2, v1, m1, x1)
    }

    private fun newElasticCollistionVelocity(
        v1: Vector,
        m1: Double,
        x1: Vector,
        v2: Vector,
        m2: Double,
        x2: Vector,
    ): Vector {
        return v1 - (x1 - x2) * ((v1 - v2) dotProduct (x1 - x2)) * 2.0 * m2 / ((m1 + m2) * pow(x1.distance(x2), 2.0))
    }

    private fun isElasticCollision(e1: Entity, e2: Entity): Boolean {
        if (e1.hasComponent(Velocity::class) && e2.hasComponent(Velocity::class)) {
            val collider = e1.getComponent(Collider::class)
            val otherCollider = e2.getComponent(Collider::class)

            return collider?.type == CollisionType.ELASTIC || otherCollider?.type == CollisionType.ELASTIC
        }

        return false;
    }

    private fun isMergeCollision(e1: Entity, e2: Entity): Boolean {
        if (e1.hasComponent(Velocity::class) && e2.hasComponent(Velocity::class)) {
            val collider = e1.getComponent(Collider::class)
            val otherCollider = e2.getComponent(Collider::class)

            return collider?.type == CollisionType.MERGE && otherCollider?.type == CollisionType.MERGE
        }

        return false;
    }
}

