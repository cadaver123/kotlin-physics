package systems

import Environment
import common.IntLinkedList
import common.Helper.Companion.getTightRangeX
import common.Helper.Companion.getTightRangeY
import components.ComponentType
import components.ComponentsManager
import components.PositionComponent
import components.grids.LooseGrid
import components.grids.LooseTightGrid.Companion.TIGHT_CELL_WIDTH
import systems.interfaces.SimulationSystem
import kotlin.concurrent.withLock
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min


class CollisionSystemX : SimulationSystem {
    companion object {
        const val LOOSE_CELL_WIDTH: Double = TIGHT_CELL_WIDTH /10.0;
        const val LOOSE_CELL_HEIGHT: Double = TIGHT_CELL_HEIGHT/10.0;

        const val LOOSE_INV_CELL_WIDTH: Double = 1.0 / LOOSE_CELL_WIDTH
        const val LOOSE_INV_CELL_HEIGHT: Double = 1.0 / LOOSE_CELL_HEIGHT

        val LOOSE_COLUMNS_COUNT = ceil(Environment.WIDTH * LOOSE_INV_CELL_WIDTH).toInt()
        val LOOSE_ROWS_COUNT = ceil(Environment.HEIGHT * LOOSE_INV_CELL_HEIGHT).toInt()
    }

    var initiated: Boolean = false
    val cleanArray: IntArray = IntArray(TIGHT_CELLS_COUNT) { -1 }
    val tightGridHeadsIds: IntArray = IntArray(TIGHT_CELLS_COUNT) { -1 }
    lateinit var looseGrid: LooseGrid
    val tightGridEntities: IntLinkedList = IntLinkedList()
    val gridEntityIds: IntLinkedList = IntLinkedList()

    fun clamp(i: Int, minVal: Int, maxVal: Int): Int {
        return max(minVal, min(maxVal, i))
    }

    override fun updateState(delta: Double) {
        if (!initiated) {
            init();
            initiated = true;
        }

        val positions = ComponentsManager.getComponent(ComponentType.POSITION) as PositionComponent
        gridEntityIds.clear()
        occupiedLoosePositions.clear()
        System.arraycopy(cleanArray, 0, tightGridHeadsIds, 0, TIGHT_CELLS_COUNT)
        positions.reentryDataLock.readLock().withLock {
            for (entityId in positions.entitiesMap.keys()) {
                val looseGridIndex = looseGrid.addEntity(entityId)
            }
        }

        for (el in occupiedLoosePositions) {
            val xRangeStart = getTightRangeX(looseGrid.xMin[el].toInt())
            val yRangeStart = getTightRangeY(looseGrid.yMin[el].toInt())
            val xRangeEnd = getTightRangeX(looseGrid.xMin[el].toInt())
            val yRangeEnd = getTightRangeY(looseGrid.yMin[el].toInt())
            val tightGridHeadsId = looseGrid.head[el]

            for (x in xRangeStart..xRangeEnd) {
                for (y in yRangeStart..yRangeEnd) {
                  //  tightGridHeadsIds[y * TIGHT_COLUMNS_COUNT + x] =
                }
            }

        }


    }

    private fun init() {
        this.looseGrid = LooseGrid()
    }

}

