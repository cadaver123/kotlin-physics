package graphics.raylib

import Environment
import com.raylib.Colors
import com.raylib.Raylib.*
import components.ColorComponent
import components.ComponentType
import components.ComponentsManager
import components.generic.Component1D
import components.generic.Component2D
import components.grids.LooseTightGrid
import components.grids.LooseTightGrid.Companion.TIGHT_CELL_WIDTH
import systems.CollisionSystemX

class RaylibRenderer {
    companion object {
        fun drawObjects() {
            drawCircle()
        }

        fun drawGrid() {
            for (i in 0..LooseTightGrid.TIGHT_COLUMNS_COUNT) {
                DrawLine(
                    i * TIGHT_CELL_WIDTH.toInt(),
                    0,
                    i * TIGHT_CELL_WIDTH.toInt(),
                    Environment.HEIGHT,
                    Colors.BLACK
                );
            }
            for (i in 0..LooseTightGrid.TIGHT_ROWS_COUNT) {
                DrawLine(
                    0,
                    i * LooseTightGrid.TIGHT_CELL_HEIGHT.toInt(),
                    Environment.WIDTH,
                    i * LooseTightGrid.TIGHT_CELL_HEIGHT.toInt(),
                    Colors.BLACK
                );
            }
        }

        fun drawSquare() {
            val mousePosition = GetMousePosition()
            val xMin = mousePosition.x().toInt() - 100
            val xMax = mousePosition.x().toInt() + 100
            val yMin = mousePosition.y().toInt() - 50
            val yMax = mousePosition.y().toInt() + 50
            DrawRectangleLines(xMin, yMin, xMax - xMin, yMax - yMin, Colors.RED)
            val xStart = LooseTightGrid.getTightRangeX(xMin)
            val yStart = LooseTightGrid.getTightRangeY(yMin)

            val xEnd = LooseTightGrid.getTightRangeX(xMax)
            val yEnd = LooseTightGrid.getTightRangeY(yMax)
            val collisionSystemX = Environment.systems.get(2) as CollisionSystemX

            val positions = ComponentsManager.getComponent(ComponentType.POSITION) as Component2D
            val circles = ComponentsManager.getComponent(ComponentType.SHAPE_CIRCLE) as Component1D

            for (xWindow in xStart..xEnd)
                for (yWindow in yStart..yEnd) {
                    var pos = yWindow * CollisionSystemX.TIGHT_COLUMNS_COUNT + xWindow

                    var idx = collisionSystemX.tightGridHeadsIds[pos]

                    while (1 == 1) {
                        if (idx == -1) break
                        val entityId = collisionSystemX.gridEntityIds.values[idx]
                        if (entityId == -1) break
                        val x = positions.x[entityId].toInt()
                        val y = positions.y[entityId].toInt()
                        if (x in xMin..xMax && y in yMin..yMax) {
                            DrawCircle(
                                x,
                                y,
                                circles.values[circles.entitiesMap[entityId]!!].toFloat(),
                                Colors.RED
                            )
                        }


                        idx = collisionSystemX.gridEntityIds.nextElIds[idx]
                    }
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

            for (entityId in circles.entitiesMap.keys()) {
                val positionIndex = positions.entitiesMap[entityId]
                val colorId = colors.entitiesMap[entityId]
                if (positionIndex != null) {
                    DrawCircle(
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
}

