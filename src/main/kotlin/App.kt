
import common.Vector
import components.shapes.Circle
import components.shapes.Shape
import entities.Entity
import graphics.raylib.Window
import systems.GravitationalSystemX
import systems.PositionSystemX
import systems.interfaces.SimulationSystem
import systems.service.CirclesCollisionDetector
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

            Environment.init(entities, systems)

/*
            EventQueue.invokeLater {
                val ex = Window()
                ex.isVisible = true

            }
*/

            Window.start()
        }

        private fun prepareSystems(systems: MutableList<SimulationSystem>) {
            systems.addAll(
                listOf(
                    //GravitationalSystem(),
                    //CollisionSystem(),
                    //PositionSystem(),
                    //DestructionSystem(),
                    PositionSystemX(),
                    GravitationalSystemX(),
                    //CollisionSystemX()
                )
            )
        }

        fun prepareEntities(entities: MutableList<Entity>) {
//            Environment.entities.add(Entity(Circle(10.0, Color.CYAN), Position(CENTER_POINT - Vector(100.0, 5.0)), Velocity(Vector(200.0, .0)), Weight(5.0)))
//            Environment.entities.add(Entity(Circle(10.0, Color.RED), Position(CENTER_POINT - Vector(20.0, 5.0)), Velocity(Vector(60.0, .0)), Weight(5.0)))
//            Environment.entities.add(Entity(Circle(10.0, Color.BLUE), Position(CENTER_POINT + Vector(20.0, .0)), Velocity(Vector(10.0, .0)), Weight(1.0)))


            addStar(entities, Environment.CENTER_POINT)
            //addStar(entities, CENTER_POINT + Vector(100.0, .0))

            for (i in 1..9999) {
                tryAddRandomBodies(entities)
            }

        }

        private fun addStar(entities: MutableList<Entity>, positionVec: Vector) {
            val entity = Entity()
            entity.addPosition(Environment.CENTER_POINT.x, Environment.CENTER_POINT.y)
            entity.addCircle(5.0)
            entity.addColor(255.toByte(), 0, 0)
            entity.addGravityForce(10000.0)

        }

        private fun tryAddRandomBodies(entities: MutableList<Entity>) {
            outer@ for (i in 1..10) {
                val circleEntity = getRandomBodyEntity()

                for (otherEntity in entities) {
                    if (otherEntity.hasComponent(Circle::class)) {

                        if (CirclesCollisionDetector.isColliding(circleEntity, otherEntity)) {
                            outer@ continue
                        }
                    }
                }

                entities.add(circleEntity)
                break
            }
        }

        fun getRandomBodyEntity(): Entity {
            val center = Environment.CENTER_POINT
            val size = Random.nextDouble(1.0, 2.0)
            val positionVec = Vector(
                Random.nextDouble(center.x - 400, center.x + 400),
                Random.nextDouble(center.y - 400, center.y + 400)
            )
            val distanceFromCenter = center.distance(positionVec)
            val velocity =
                (Environment.CENTER_POINT - positionVec).getPerpendicularCounterClockwise() * sqrt (Environment.GRAVITATIONAL_CONSTANT*100.0/distanceFromCenter)

            val entity = Entity()
            entity.addPosition(positionVec.x, positionVec.y)
            entity.addVelocity(velocity.x, velocity.y)
            entity.addCircle(size)
            entity.addColor(Random.nextInt(0, 255).toByte(), Random.nextInt(0, 255).toByte(), Random.nextInt(0, 255).toByte())
            return entity
        }

        fun getRandomColor() = Shape.Color(Random.nextInt(0, 255).toByte(), Random.nextInt(0, 255).toByte(), Random.nextInt(0, 255).toByte())

    }

}
