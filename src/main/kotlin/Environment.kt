import common.Vector
import components.grids.LooseTightDoubleGrid
import entities.Entity
import systems.interfaces.SimulationSystem

object Environment {
    const val WIDTH = 1000
    const val HEIGHT = 1000
    val ENV_SIZE = Vector(WIDTH.toDouble(), HEIGHT.toDouble())
    const val MAX_VELOCITY = 1000.0
    val CENTER_POINT = Vector((ENV_SIZE.x / 2.0).toDouble(), (ENV_SIZE.y / 2.0).toDouble())
    const val FINITE_PLANE = true
    const val GRAVITATIONAL_CONSTANT = 100.0
    const val TARGET_FPS = 60

    lateinit var entities: MutableList<Entity>
        private set
    val entitiesMap = HashMap<Int, Int>(10000)
    lateinit var systems: List<SimulationSystem>
        private set
    lateinit var grid: LooseTightDoubleGrid
        private set

    fun removeEntities(entitiesToRemove: Collection<Entity>) {
        for(entity in entitiesToRemove) {
            entity.removeComponents()
            grid.remove(entity.id)
            if (entities.size == 1) {
                entities.clear()
                entitiesMap.clear()
                continue
            }
            val hole = entitiesMap.remove(entity.id)!!
            if (hole == entities.size - 1) {
               entities.removeAt(entities.size - 1)
                continue
            }
            entities[hole] = entities.removeAt(entities.size - 1)
            entitiesMap[entities[hole].id] = hole


        }
    }

    fun removeEntity(entity: Entity) {
        entity.removeComponents()
        entities.remove(entity)
    }

    fun init(entities: MutableList<Entity>, systems: List<SimulationSystem>) {
        this.entities = entities
        for (i in 0 until entities.size) {
            entities[i]
            entitiesMap[entities[i].id] = i
        }
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