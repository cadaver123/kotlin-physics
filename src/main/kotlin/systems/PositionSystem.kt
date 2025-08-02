package systems

import components.Position
import components.Velocity
import entities.Entity

class PositionSystem() : AbstractSystem(
    Velocity::class,
    { it: Entity, delta: Double ->
        run {
            val velocity = it.getComponent(Velocity::class)!!
            val position = it.getComponent(Position::class)!!
            position.add(velocity.vec * delta)
        }
    }
)