import common.Vector
import components.CollisionType
import components.ComponentType
import components.ComponentsManager
import components.generic.Component1D
import components.generic.Component2D
import components.generic.flagsOf
import entities.Entity
import graphics.raylib.Window
import kotlin.math.sqrt
import kotlin.random.Random
import systems.CollisionSystem
import systems.GravitationalSystemX
import systems.PositionSystem
import systems.interfaces.SimulationSystem


class App {
    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val entities = mutableListOf<Entity>()
            val systems = mutableListOf<SimulationSystem>()

            prepareEntities(entities)
            prepareSystems(systems)

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
                    PositionSystem(),
                    CollisionSystem(),
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
            val entity = Entity(flags = flagsOf())
            entity.addPosition(Environment.CENTER_POINT.x, Environment.CENTER_POINT.y)
            entity.addVelocity(0.0, 0.0)
            entity.addCircle(5.0)
            entity.addColor(255.toByte(), 0, 0)
            entity.addGravityForce(10000.0)
            entity.addCollision(CollisionType.ELASTIC)
            entity.addMass(10000.0)
            entities.add(entity)

        }

        private fun tryAddRandomBodies(entities: MutableList<Entity>) {
            val positions = ComponentsManager.getComponent(ComponentType.POSITION) as Component2D
            val circles = ComponentsManager.getComponent(ComponentType.SHAPE_CIRCLE) as Component1D
            outer@ for (i in 1..10) {
                val center = Environment.CENTER_POINT
                val r = Random.nextDouble(1.0, 2.0)
                val x = Random.nextDouble(center.x - 400.0, center.x + 400.0)
                val y = Random.nextDouble(center.y - 400.0, center.y + 400.0)
                
                for (otherEntity in entities) {
                    val otherX = positions.x[positions.entitiesMap[otherEntity.id]!!]
                    val otherY = positions.y[positions.entitiesMap[otherEntity.id]!!]
                    val otherR = circles.values[circles.entitiesMap[otherEntity.id]!!]
                    if ((x - otherX) * (x - otherX) + (y - otherY) * (y - otherY) <= (r + otherR) * (r + otherR)) {
                        outer@ continue
                    }
                }
                val distanceFromCenter = sqrt((Environment.CENTER_POINT.x - x) * (Environment.CENTER_POINT.x - x) + (Environment.CENTER_POINT.y - y) * (Environment.CENTER_POINT.y - y))
                val velocityFactor = sqrt(100.0 / distanceFromCenter)
                val velocityX = y/distanceFromCenter * velocityFactor
                val velocityY = -x /distanceFromCenter * velocityFactor

                val entity = Entity(flags = flagsOf())
                entity.addPosition(x, y)
                entity.addVelocity(velocityX, velocityY)
                entity.addCircle(r)
                entity.addColor(
                    Random.nextInt(0, 255).toByte(),
                    Random.nextInt(0, 255).toByte(),
                    Random.nextInt(0, 255).toByte()
                )
                entity.addCollision(CollisionType.ELASTIC)
                entity.addMass(1.0)
                entities.add(entity)
                break
            }
        }


    }

}
