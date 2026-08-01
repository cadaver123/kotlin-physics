package components.grids

import common.Helper.Companion.clamp

import Environment
import common.IntLinkedList
import components.ComponentType
import components.ComponentsManager
import components.PositionComponent
import components.shapes.CircleComponent
import entities.Entity
import kotlin.collections.forEach
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min


class LooseTightDoubleGrid {
    companion object {
        const val TIGHT_CELL_WIDTH: Double = 100.0
        const val TIGHT_CELL_HEIGHT: Double = 100.0
        const val TIGHT_COLUMNS_COUNT: Int = Environment.WIDTH / TIGHT_CELL_WIDTH.toInt()
        const val TIGHT_ROWS_COUNT: Int = Environment.HEIGHT / TIGHT_CELL_HEIGHT.toInt()
        const val TIGHT_CELLS_COUNT: Int = TIGHT_ROWS_COUNT * TIGHT_COLUMNS_COUNT
        const val TIGHT_INV_CELL_WIDTH: Double = 1.0 / TIGHT_CELL_WIDTH
        const val TIGHT_INV_CELL_HEIGHT: Double = 1.0 / TIGHT_CELL_HEIGHT
        const val LOOSE_CELL_WIDTH: Double = 10.0;
        const val LOOSE_CELL_HEIGHT: Double = 10.0;
        const val LOOSE_INV_CELL_WIDTH: Double = 1.0 / LOOSE_CELL_WIDTH
        const val LOOSE_INV_CELL_HEIGHT: Double = 1.0 / LOOSE_CELL_HEIGHT
        val LOOSE_COLUMNS_COUNT = ceil(Environment.WIDTH * LOOSE_INV_CELL_WIDTH).toInt()
        val LOOSE_ROWS_COUNT = ceil(Environment.HEIGHT * LOOSE_INV_CELL_HEIGHT).toInt()

        fun getTightRangeY(y: Int): Int = clamp((y * TIGHT_INV_CELL_HEIGHT).toInt(), TIGHT_ROWS_COUNT - 1)

        fun getTightRangeX(x: Int): Int = clamp(
            (x * TIGHT_INV_CELL_WIDTH).toInt(), TIGHT_COLUMNS_COUNT - 1
        )
    }


    private var looseGridLinkedListHead = IntArray(LOOSE_COLUMNS_COUNT * LOOSE_ROWS_COUNT) { -1 }
    private var tightGridLinkedListHead = IntArray(TIGHT_COLUMNS_COUNT * TIGHT_ROWS_COUNT) { -1 }
    private val looseGridEntityIds: IntLinkedList = IntLinkedList()
    private val tightGridLooseCellsIdx: IntLinkedList = IntLinkedList()
    var xMin = DoubleArray(LOOSE_COLUMNS_COUNT * LOOSE_ROWS_COUNT) { Double.POSITIVE_INFINITY }
    var yMin = DoubleArray(LOOSE_COLUMNS_COUNT * LOOSE_ROWS_COUNT) { Double.POSITIVE_INFINITY }
    var xMax = DoubleArray(LOOSE_COLUMNS_COUNT * LOOSE_ROWS_COUNT) { Double.NEGATIVE_INFINITY }
    var yMax = DoubleArray(LOOSE_COLUMNS_COUNT * LOOSE_ROWS_COUNT) { Double.NEGATIVE_INFINITY }
    private val entityToLooseCell = IntArray(10000) { -1 };
    private val entityToLinkedListNode = IntArray(10000) { -1 };
    val occupiedCells = HashSet<Int>(LOOSE_COLUMNS_COUNT * LOOSE_ROWS_COUNT)
    private val queryVisitedStamp = LongArray(LOOSE_COLUMNS_COUNT * LOOSE_ROWS_COUNT) { Long.MAX_VALUE }
    private var queryId = Long.MIN_VALUE


    private var positions: PositionComponent
    private var shapes: CircleComponent

    constructor(entities: List<Entity>) {
        positions = ComponentsManager.getComponent(ComponentType.POSITION) as PositionComponent
        shapes = ComponentsManager.getComponent(ComponentType.SHAPE_CIRCLE) as CircleComponent
        entities.forEach { entity -> add(entity.id) }
    }

    fun add(entityId: Int) {
        val looseCell = looseCellOf(positions.getX(entityId), positions.getY(entityId))
        updateCellSizeAndAssignToTightGrid(entityId, looseCell)
        val oldHead = looseGridLinkedListHead[looseCell]
        val linkedListNode = looseGridEntityIds.add(entityId, oldHead)
        looseGridLinkedListHead[looseCell] = linkedListNode
        entityToLooseCell[entityId] = looseCell
        entityToLinkedListNode[entityId] = linkedListNode
        occupiedCells.add(looseCell)
    }

    private fun updateCellSizeAndAssignToTightGrid(entityId: Int, looseCell: Int) {
        val r = shapes.values[entityId]
        val x = positions.x[entityId]
        val y = positions.y[entityId]
        val newXMin = min(xMin[looseCell], x - r)
        val newYMin = min(yMin[looseCell], y - r)
        val newXMax = max(xMax[looseCell], x + r)
        val newYMax = max(yMax[looseCell], y + r)
        if (xMin[looseCell] == Double.POSITIVE_INFINITY) {
            for (x in tightGridXPos(newXMin)..tightGridXPos(newXMax)) {
                for (y in tightGridYPos(newYMin)..tightGridYPos(newYMax)) {
                    addToTightGrid(looseCell, tightGridIdx(x, y))
                }
            }
        } else {
            for (x in tightGridXPos(newXMin)..tightGridXPos(newXMax)) {
                for (y in tightGridYPos(newYMin) until tightGridYPos(yMin[looseCell])) {
                    addToTightGrid(looseCell, tightGridIdx(x, y))
                }

                for (y in tightGridYPos(yMax[looseCell]) + 1..tightGridYPos(newYMax)) {
                    addToTightGrid(looseCell, tightGridIdx(x, y))
                }
            }

            for (y in tightGridYPos(yMin[looseCell])..tightGridYPos(yMax[looseCell])) {
                for (x in tightGridXPos(newXMin) until tightGridXPos(xMin[looseCell])) {
                    addToTightGrid(looseCell, tightGridIdx(x, y))
                }
                for (x in tightGridXPos(xMax[looseCell]) + 1..tightGridXPos(newXMax)) {
                    addToTightGrid(looseCell, tightGridIdx(x, y))
                }
            }

        }
        xMin[looseCell] = newXMin
        yMin[looseCell] = newYMin
        xMax[looseCell] = newXMax
        yMax[looseCell] = newYMax
    }


    fun move(entityId: Int) {
        val newCell = looseCellOf(positions.getX(entityId), positions.getY(entityId))
        val oldLooseCell = entityToLooseCell[entityId]
        if (newCell == oldLooseCell) {
            val r = shapes.values[entityId]
            val x = positions.x[entityId]
            val y = positions.y[entityId]

            if (xMin[oldLooseCell] > x - r ||
                yMin[oldLooseCell] > y - r ||
                xMax[oldLooseCell] < x + r ||
                yMax[oldLooseCell] < y + r
            ) {
                updateCellSizeAndAssignToTightGrid(entityId, oldLooseCell)
            }
            return
        }
        remove(entityId)
        add(entityId)
    }

    fun remove(entityId: Int) {
        val cell = entityToLooseCell[entityId]
        val headNode = looseGridLinkedListHead[cell]

        val node = entityToLinkedListNode[entityId]
        assert(node != -1)
        if (headNode == node) {
            val nextNode = looseGridEntityIds.nextElIds[node]
            looseGridLinkedListHead[cell] = nextNode
            if (nextNode == -1) {
                occupiedCells.remove(cell)
            }
        } else {
            val prevNode = looseGridEntityIds.prevElIds[node]
            val nextNode = looseGridEntityIds.nextElIds[node]
            looseGridEntityIds.nextElIds[prevNode] = nextNode
            if (nextNode != -1) {
                looseGridEntityIds.prevElIds[nextNode] = prevNode
            }
        }
        looseGridEntityIds.erase(node)
    }

    fun looseCellOf(x: Double, y: Double): Int {
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

    fun tightGridXPos(x: Double): Int {
        return clamp(
            (x * TIGHT_INV_CELL_WIDTH).toInt(),
            0,
            TIGHT_COLUMNS_COUNT - 1
        )
    }

    fun tightGridYPos(y: Double): Int {
        return clamp(
            (y * TIGHT_INV_CELL_HEIGHT).toInt(),
            0,
            TIGHT_ROWS_COUNT - 1
        )
    }

    fun tightCellOf(x: Double, y: Double): Int {
        val xGridPos = clamp(
            (x * TIGHT_INV_CELL_WIDTH).toInt(),
            0,
            TIGHT_COLUMNS_COUNT - 1
        )
        val yGridPos = clamp(
            (y * TIGHT_INV_CELL_HEIGHT).toInt(),
            0,
            TIGHT_ROWS_COUNT - 1
        )

        return yGridPos * TIGHT_COLUMNS_COUNT + xGridPos

    }

    fun optimize() {
        tightGridLooseCellsIdx.clear()
        for (i in tightGridLinkedListHead.indices) {
            tightGridLinkedListHead[i] = -1
        }
        for (i in 0 until LOOSE_COLUMNS_COUNT * LOOSE_ROWS_COUNT) { // reset AABB of all cells (there can be cells which was removed from occupiedCells but has AABB)
            resetAABB(i)
        }
        for (cell in occupiedCells) {
            var current = looseGridLinkedListHead[cell]
            while (current != -1) {
                val entityId = looseGridEntityIds.values[current]
                updateCellSizeAndAssignToTightGrid(entityId, cell)
                current = looseGridEntityIds.nextElIds[current]
            }
        }
    }

    fun query(xMin: Double, xMax: Double, yMin: Double, yMax: Double, outBufferWithEntityIds: IntArray): Int {
        var counter = 0
        for (x in tightGridXPos(xMin)..tightGridXPos(xMax)) {
            for (y in tightGridYPos(yMin)..tightGridYPos(yMax)) {
                var tightGridLinkedListIdx = tightGridLinkedListHead[tightGridIdx(x, y)]
                while (tightGridLinkedListIdx != -1) {
                    val currentLooseCellIdx = tightGridLooseCellsIdx.values[tightGridLinkedListIdx]
                    if (queryVisitedStamp[currentLooseCellIdx] == queryId) {
                        tightGridLinkedListIdx = tightGridLooseCellsIdx.nextElIds[tightGridLinkedListIdx]
                        continue
                    }
                    if (!(this.xMax[currentLooseCellIdx] > xMin
                                && this.xMin[currentLooseCellIdx] < xMax
                                && this.yMax[currentLooseCellIdx] > yMin
                                && this.yMin[currentLooseCellIdx] < yMax
                                )
                    ) {
                        tightGridLinkedListIdx = tightGridLooseCellsIdx.nextElIds[tightGridLinkedListIdx]

                        continue
                    }
                    queryVisitedStamp[currentLooseCellIdx] = queryId
                    var currentEntityIdx = looseGridLinkedListHead[currentLooseCellIdx]
                    while (currentEntityIdx != -1) {
                        val entityId = looseGridEntityIds.values[currentEntityIdx]
                        val r = shapes.values[entityId]
                        val x = positions.x[entityId]
                        val y = positions.y[entityId]
                        if ((xMin < x + r && x - r < xMax) && (yMin < y + r && y - r < yMax)) {
                            outBufferWithEntityIds[counter] = entityId
                            counter++
                        }
                        currentEntityIdx = looseGridEntityIds.nextElIds[currentEntityIdx]
                    }
                    tightGridLinkedListIdx = tightGridLooseCellsIdx.nextElIds[tightGridLinkedListIdx]
                }
            }

        }
        queryId++;
        return counter
    }

    private fun tightGridIdx(x: Int, y: Int): Int = x + y * TIGHT_COLUMNS_COUNT

    private fun addToTightGrid(looseCell: Int, tightGridIdx: Int) {
        val oldHead = tightGridLinkedListHead[tightGridIdx]
        val linkedListNode = tightGridLooseCellsIdx.add(looseCell, oldHead)
        tightGridLinkedListHead[tightGridIdx] = linkedListNode
    }

    private fun resetAABB(looseCellIdx: Int) {
        xMin[looseCellIdx] = Double.POSITIVE_INFINITY
        yMin[looseCellIdx] = Double.POSITIVE_INFINITY
        xMax[looseCellIdx] = Double.NEGATIVE_INFINITY
        yMax[looseCellIdx] = Double.NEGATIVE_INFINITY
    }
}

