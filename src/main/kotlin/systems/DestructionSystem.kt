package systems

import Environment
import components.Destructor
import components.Position
import components.Velocity
import components.shapes.Circle
import entities.Entity
import systems.service.CirclesCollisionDetector

class DestructionSystem : AbstractSystem(
    Destructor::class,
    { it: Entity, delta: Double ->
        run {
            for(other in Environment.entities) {
                if (it !== other && CirclesCollisionDetector.isColliding(it, other)) {
                    Environment.entities =  Environment.entities.filter { it !== other }
                }
            }
        }
    }
)