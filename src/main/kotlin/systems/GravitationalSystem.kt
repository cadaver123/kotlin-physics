package systems

import Environment
import Environment.Companion.GRAVITANIONAL_CONSTANT
import components.GravitySource
import components.Position
import components.Velocity
import entities.Entity
import kotlin.math.pow

class GravitationalSystem() : AbstractSystem(
    GravitySource::class,
    fun(source: Entity, deltaT: Double) {
        val (strength) = source.getComponent(GravitySource::class)!!
        val (sourcePosition) = source.getComponent(Position::class)!!

        Environment.entities
            .forEach {
                if (source != it) {
                    val velocity = it.getComponent(Velocity::class)
                    val position = it.getComponent(Position::class)

                    if (velocity != null && position != null) {
                        val gravityAcc = strength / (sourcePosition.distance(position.vec)).pow(2.0)
                        val gravityAccVec =
                            (sourcePosition - position.vec).norm() * GRAVITANIONAL_CONSTANT * gravityAcc

                        velocity.add(gravityAccVec * deltaT)
                    }
                }
            }


    }
)