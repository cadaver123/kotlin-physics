import common.Vector
import components.grids.LooseTightDoubleGrid
import entities.Entity
import systems.interfaces.SimulationSystem

object Environment {
    const val WIDTH = 1000
    const val HEIGHT = 1000
    val ENV_SIZE = Vector(WIDTH.toDouble(), HEIGHT.toDouble())
    const val MAX_VELOCITY = 1000.0
    val CENTER_POINT = Vector(ENV_SIZE.x / 2.0, ENV_SIZE.y / 2.0)
    const val FINITE_PLANE = true
    const val GRAVITATIONAL_CONSTANT = 100.0
    const val TARGET_FPS = 60

    lateinit var entities: List<Entity>
        private set
    lateinit var systems: List<SimulationSystem>
        private set
    lateinit var grid: LooseTightDoubleGrid
        private set

    fun removeEntities(objectsToRemove: Collection<Entity>) {
        entities = entities.filterNot { it in objectsToRemove }
    }

    fun init(entities: List<Entity>, systems: List<SimulationSystem>) {
        this.entities = entities
        this.systems = systems
        this.grid = LooseTightDoubleGrid(entities)
    }
    object Runner {
        fun run(dt: Double) {
            systems.forEach {
                it.updateState(dt)

            }
        }
    }
}