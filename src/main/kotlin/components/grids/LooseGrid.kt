package components.grids

import common.IntLinkedList
import common.Helper.Companion.clamp
import components.ComponentType
import components.ComponentsManager
import components.PositionComponent
import components.shapes.CircleComponent
import kotlin.concurrent.withLock
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

class LooseGrid {
    companion object {
        const val LOOSE_CELL_WIDTH: Double = 10.0;
        const val LOOSE_CELL_HEIGHT: Double = 10.0;
        const val LOOSE_INV_CELL_WIDTH: Double = 1.0 / LOOSE_CELL_WIDTH
        const val LOOSE_INV_CELL_HEIGHT: Double = 1.0 / LOOSE_CELL_HEIGHT
        val LOOSE_COLUMNS_COUNT = ceil(Environment.WIDTH * LOOSE_INV_CELL_WIDTH).toInt()
        val LOOSE_ROWS_COUNT = ceil(Environment.HEIGHT * LOOSE_INV_CELL_HEIGHT).toInt()
    }

    var head = IntArray(10000) { -1 }
    var xMin = DoubleArray(10000) { Double.MAX_VALUE }
    var yMin = DoubleArray(10000) { Double.MAX_VALUE }
    var xMax = DoubleArray(10000)
    var yMax = DoubleArray(10000)
    val gridEntityIds: IntLinkedList = IntLinkedList()
    val occupiedLoosePositions = HashSet<Int>(10000)


    private var positions: PositionComponent
    private var shapes: CircleComponent

    constructor() {
        positions = ComponentsManager.getComponent(ComponentType.POSITION) as PositionComponent
        shapes = ComponentsManager.getComponent(ComponentType.SHAPE_CIRCLE) as CircleComponent
    }

    fun addEntity(entityId: Int): Int {
        val index = getIndexForPosition(positions.getX(entityId), positions.getY(entityId))
        positions.reentryDataLock.readLock().withLock {
            shapes.reentryDataLock.readLock().withLock {
                val r = shapes.values[entityId]
                val x = positions.x[entityId]
                val y = positions.y[entityId]
                xMin[index] = min(xMin[index], x - r)
                yMin[index] = min(yMin[index], y - r)
                xMax[index] = max(xMax[index], x + r)
                yMax[index] = max(yMax[index], y + r)
                val oldHead = this.head[index]
                val newHead = gridEntityIds.add(entityId, oldHead)
                this.head[index] = newHead
            }
        }

        occupiedLoosePositions.add(index)

        return index
    }

    fun getIndexForPosition(x: Double, y: Double): Int {
        val xGridPos = clamp(
            (x * LOOSE_INV_CELL_WIDTH).toInt(),
            0,
            LOOSE_COLUMNS_COUNT - 1
        )
        val yGridPos = clamp(
            (y * LOOSE_INV_CELL_HEIGHT).toInt(),
            0,
            LOOSE_ROWS_COUNT - 1
        )

        return yGridPos * LOOSE_COLUMNS_COUNT + xGridPos

    }


}