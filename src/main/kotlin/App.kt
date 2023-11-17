import Environment.Companion.CENTER_POINT
import Environment.Companion.GRAVITANIONAL_CONSTANT
import common.Vector
import components.*
import components.shapes.Circle
import entities.Entity
import systems.DestructionSystem
import systems.CollisionSystem
import systems.GravitationalSystem
import systems.PositionSystem
import systems.interfaces.SimulationSystem
import systems.service.CirclesCollisionDetector
import java.awt.Color
import java.awt.EventQueue
import kotlin.math.sqrt
import kotlin.random.Random


class App {
    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val entities = mutableListOf<Entity>()
            val systems = mutableListOf<SimulationSystem>()

            prepareSystems(systems)
            prepareEntities(entities)

            Environment.entities = entities
            Environment.systems = systems

            EventQueue.invokeLater {
                val ex = Window()
                ex.isVisible = true
                Environment.Runner.run()
            }

        }

        private fun prepareSystems(systems: MutableList<SimulationSystem>) {
            systems.addAll(
                listOf(
                    GravitationalSystem(),
                    CollisionSystem(),
                    PositionSystem(),
                    DestructionSystem(),
                )
            )
        }

        fun prepareEntities(entities: MutableList<Entity>) {
//            Environment.entities.add(Entity(Circle(10.0, Color.CYAN), Position(CENTER_POINT - Vector(100.0, 5.0)), Velocity(Vector(200.0, .0)), Weight(5.0)))
//            Environment.entities.add(Entity(Circle(10.0, Color.RED), Position(CENTER_POINT - Vector(20.0, 5.0)), Velocity(Vector(60.0, .0)), Weight(5.0)))
//            Environment.entities.add(Entity(Circle(10.0, Color.BLUE), Position(CENTER_POINT + Vector(20.0, .0)), Velocity(Vector(10.0, .0)), Weight(1.0)))


            addStar(entities, CENTER_POINT)
            //addStar(entities, CENTER_POINT + Vector(100.0, .0))

            for (i in 1..100) {
                tryAddRandomBodies(entities)
            }

        }

        private fun addStar(entities: MutableList<Entity>, positionVec: Vector) {
            entities.add(
                Entity(
                    Circle(5.0, Color.RED),
                    Position(positionVec),
                    GravitySource(100000.0),
                    //Velocity(Vector(.0, .0)),
                    //Destructor(),
                )
            );
        }

        private fun tryAddRandomBodies(entities: MutableList<Entity>) {
            outer@ for (i in 1..10) {
                val circleEntity = getRandomBodyEntity()

                for (otherEntity in entities) {
                    if (otherEntity.hasComponent(Circle::class)) {

                        if (CirclesCollisionDetector.isColliding(circleEntity, otherEntity)) {
                            outer@ continue;
                        }
                    }
                }

                entities.add(circleEntity)
                break
            }
        }

        fun getRandomBodyEntity(): Entity {
            val center = CENTER_POINT
            val size = Random.nextDouble(5.0, 20.0)
            val positionVec = Vector(
                Random.nextDouble(center.x - 400, center.x + 400),
                Random.nextDouble(center.y - 400, center.y + 400)
            )
            val distanceFromCenter = center.distance(positionVec)
            val velocity =
                (CENTER_POINT - positionVec).getPerpendicularCounterClockwise() * sqrt (5.5*GRAVITANIONAL_CONSTANT*10000.0/distanceFromCenter)

            return Entity(
                Circle(size, getRandomColor()),
                Position(positionVec),
                Velocity(velocity),
                Collider(size, CollisionType.ELASTIC),
                GravitySource(size)
            )
        }

        fun getRandomColor() = Color(Random.nextInt(0, 255), Random.nextInt(0, 255), Random.nextInt(0, 255))

    }

}
