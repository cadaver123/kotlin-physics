package graphics.raylib

import Environment
import com.raylib.Colors
import com.raylib.Raylib
import com.raylib.Raylib.DrawRectangleLines
import components.ColorComponent
import components.ComponentType
import components.ComponentsManager
import components.generic.Component1D
import components.generic.Component2D
import components.grids.LooseTightDoubleGrid
import org.bytedeco.javacpp.FloatPointer
import org.bytedeco.javacpp.Pointer

object RaylibRenderer {
    val vaoId: Int
    val vboId: Int

    init {
        vaoId = Raylib.rlLoadVertexArray()
        vboId = Raylib.rlLoadVertexBuffer(FloatPointer(-1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f), 12*4, false)
        Raylib.rlSetVertexAttribute(0, 2, Raylib.RL_FLOAT, false, 8, 0)
        Raylib.rlEnableVertexAttribute(0)
    }


    private var queryBuffer = IntArray(10000)

    fun drawObjects() {
        drawCircle()
    }

    fun drawGrid() {
        for (i in 0..LooseTightDoubleGrid.TIGHT_COLUMNS_COUNT) {
            Raylib.DrawLine(
                i * LooseTightDoubleGrid.TIGHT_CELL_WIDTH.toInt(),
                0,
                i * LooseTightDoubleGrid.TIGHT_CELL_WIDTH.toInt(),
                Environment.HEIGHT,
                Colors.BLACK
            );
        }
        for (i in 0..LooseTightDoubleGrid.TIGHT_ROWS_COUNT) {
            Raylib.DrawLine(
                0,
                i * LooseTightDoubleGrid.TIGHT_CELL_HEIGHT.toInt(),
                Environment.WIDTH,
                i * LooseTightDoubleGrid.TIGHT_CELL_HEIGHT.toInt(),
                Colors.BLACK
            );
        }
        for (looselyCellIdx in Environment.grid.occupiedCells) {
            Raylib.DrawRectangleLines(
                Environment.grid.xMin[looselyCellIdx].toInt(),
                Environment.grid.yMin[looselyCellIdx].toInt(),
                Environment.grid.xMax[looselyCellIdx].toInt() - Environment.grid.xMin[looselyCellIdx].toInt(),
                Environment.grid.yMax[looselyCellIdx].toInt() - Environment.grid.yMin[looselyCellIdx].toInt(),
                Colors.GRAY
            )
        }
    }

    fun drawSquare() {
        val mousePosition = Raylib.GetMousePosition()
        val xMin = mousePosition.x().toInt() - 100
        val xMax = mousePosition.x().toInt() + 100
        val yMin = mousePosition.y().toInt() - 50
        val yMax = mousePosition.y().toInt() + 50
        DrawRectangleLines(xMin, yMin, xMax - xMin, yMax - yMin, Colors.RED)
        val xStart = LooseTightDoubleGrid.getTightRangeX(xMin)
        val yStart = LooseTightDoubleGrid.getTightRangeY(yMin)

        val xEnd = LooseTightDoubleGrid.getTightRangeX(xMax)
        val yEnd = LooseTightDoubleGrid.getTightRangeY(yMax)
        //  val collisionSystemX = Environment.systems.get(2) as CollisionSystemX

        val positions = ComponentsManager.getComponent(ComponentType.POSITION) as Component2D
        val circles = ComponentsManager.getComponent(ComponentType.SHAPE_CIRCLE) as Component1D
        val queryResultCount =
            Environment.grid.query(xMin.toDouble(), xMax.toDouble(), yMin.toDouble(), yMax.toDouble(), this.queryBuffer)
        for (idx in 0 until queryResultCount) {
            val entityId = this.queryBuffer[idx]
            val x = positions.x[entityId].toInt()
            val y = positions.y[entityId].toInt()
            Raylib.DrawCircle(
                x,
                y,
                circles.values[circles.entitiesMap[entityId]!!].toFloat(),
                Colors.RED
            )
        }

    }


    fun drawCircle() {
        /*            if (entity.hasComponent(Circle::class)) {
                        val (r, color) = entity.getComponent(Circle::class)!!
                        if (r > 1.0) {
                            val (x, y) = entity.getComponent(Position::class)!!.vec
                            val raylibColor = Raylib.Color().r(color.r).g(color.g).b(color.b).a(255.toByte())
                            DrawCircle(x.toInt(), y.toInt(), r.toFloat(), raylibColor)
                        }
                    }*/


        val circles = ComponentsManager.getComponent(ComponentType.SHAPE_CIRCLE) as Component1D
        val colors = ComponentsManager.getComponent(ComponentType.COLOR) as ColorComponent
        val positions = ComponentsManager.getComponent(ComponentType.POSITION) as Component2D

        for (entityId in circles.entitiesMap.keys) {
            val positionIndex = positions.entitiesMap[entityId]
            val colorId = colors.entitiesMap[entityId]
            if (positionIndex != null) {
                Raylib.DrawCircle(
                    positions.x[positionIndex].toInt(),
                    positions.y[positionIndex].toInt(),
                    circles.values[circles.entitiesMap[entityId]!!].toFloat(),
                    Colors.BLACK
                    //if (colorId == null) Colors.GREEN else colors.colors.position(colorId.toLong())
                )
            }
        }
    }
}

