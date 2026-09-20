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
import java.nio.FloatBuffer
import kotlin.properties.Delegates
import org.bytedeco.javacpp.FloatPointer

object RaylibRenderer {
    var VAO_ID: Int by Delegates.notNull()
    var VBO_ID_QUAD: Int by Delegates.notNull()
    var VBO_ID_POSITIONS: Int by Delegates.notNull()
    var VBO_ID_RADIUS: Int by Delegates.notNull()
    var PROGRAM_ID: Int by Delegates.notNull()
    var POSITION_BUFFER: FloatPointer  = FloatPointer(MAX_CIRCLES * 2L)
    var POSITION_BUFFER_VIEW: FloatBuffer  = POSITION_BUFFER.asBuffer()
    var RADIUS_BUFFER: FloatPointer  = FloatPointer(MAX_CIRCLES * 1L)
    var RADIUS_BUFFER_VIEW: FloatBuffer  = RADIUS_BUFFER.asBuffer()


    private const val MAX_CIRCLES = 10_000

    private val VERTEX_SHADER_SRC = loadShader("/shaders/circles.vert")

    private val FRAGMENT_SHADER_SRC = loadShader("/shaders/circles.frag")

    fun init() {
        VAO_ID = Raylib.rlLoadVertexArray()
        Raylib.rlEnableVertexArray(VAO_ID)
        initializeVboQuad()
        initializeVboCenters()
        initializeVboRadius()
        val vsId = Raylib.rlCompileShader(VERTEX_SHADER_SRC, Raylib.RL_VERTEX_SHADER)
        val fsId = Raylib.rlCompileShader(FRAGMENT_SHADER_SRC, Raylib.RL_FRAGMENT_SHADER)
        FRAGMENT_SHADER_SRC.lines().forEachIndexed { i, l -> println("${i + 1}: [$l]") }
        PROGRAM_ID = Raylib.rlLoadShaderProgram(vsId, fsId)
        val uResolutionLoc = Raylib.rlGetLocationUniform(PROGRAM_ID, "uResolution")
        val uMinXLoc = Raylib.rlGetLocationUniform(PROGRAM_ID, "uMinX")
        val uMaxXLoc = Raylib.rlGetLocationUniform(PROGRAM_ID, "uMaxX")
        val uMinYLoc = Raylib.rlGetLocationUniform(PROGRAM_ID, "uMinY")
        val uMaxYLoc = Raylib.rlGetLocationUniform(PROGRAM_ID, "uMaxY")
        Raylib.rlEnableShader(PROGRAM_ID)
        Raylib.rlSetUniform(
            uResolutionLoc,
            FloatPointer(Raylib.rlGetFramebufferWidth().toFloat(), Raylib.rlGetFramebufferHeight().toFloat()),
            Raylib.RL_SHADER_UNIFORM_VEC2,
            1
        )

        Raylib.rlSetUniform(uMinXLoc, FloatPointer(.0f), Raylib.RL_SHADER_UNIFORM_FLOAT, 1)
        Raylib.rlSetUniform(uMaxXLoc, FloatPointer(Environment.WIDTH.toFloat()), Raylib.RL_SHADER_UNIFORM_FLOAT, 1)
        Raylib.rlSetUniform(uMinYLoc, FloatPointer(.0f), Raylib.RL_SHADER_UNIFORM_FLOAT, 1)
        Raylib.rlSetUniform(uMaxYLoc, FloatPointer(Environment.HEIGHT.toFloat()), Raylib.RL_SHADER_UNIFORM_FLOAT, 1)

        println("VAO_ID $VAO_ID VBO_ID $VBO_ID_QUAD vsId $vsId - fsId $fsId - programId $PROGRAM_ID")
        if (vsId == 0 || fsId == 0 || PROGRAM_ID == 0) {
            throw IllegalStateException("There is some problem with shaders - vsId $vsId - fsId $fsId - programId $PROGRAM_ID")
        }
    }

    private fun initializeVboQuad() {
        VBO_ID_QUAD = Raylib.rlLoadVertexBuffer(
            FloatPointer(
                -1.0f, -1.0f,
                1.0f, 1.0f,
                -1.0f, 1.0f,
                -1.0f, -1.0f,
                1.0f, -1.0f,
                1.0f, 1.0f
            ), 12 * 4, false
        )
        Raylib.rlSetVertexAttribute(0, 2, Raylib.RL_FLOAT, false, 2 * Float.SIZE_BYTES, 0)
        Raylib.rlEnableVertexAttribute(0)
    }

    private fun initializeVboCenters() {

        VBO_ID_POSITIONS = Raylib.rlLoadVertexBuffer(
            POSITION_BUFFER,
            10_000 * 2 * Float.SIZE_BYTES,
            true
        )
        Raylib.rlEnableVertexBuffer(VBO_ID_POSITIONS)
        Raylib.rlSetVertexAttribute(1, 2, Raylib.RL_FLOAT, false, 2 * Float.SIZE_BYTES, 0)
        Raylib.rlSetVertexAttributeDivisor(1, 1)
        Raylib.rlEnableVertexAttribute(1)
    }

    private fun initializeVboRadius() {


        VBO_ID_RADIUS = Raylib.rlLoadVertexBuffer(
            RADIUS_BUFFER,
            10_000 * 1 * Float.SIZE_BYTES,
            false
        )
        Raylib.rlEnableVertexBuffer(VBO_ID_RADIUS)
        Raylib.rlSetVertexAttribute(2, 1, Raylib.RL_FLOAT, false, 1 * Float.SIZE_BYTES, 0)
        Raylib.rlSetVertexAttributeDivisor(2, 1)
        Raylib.rlEnableVertexAttribute(2)   
    }


    private var queryBuffer = IntArray(10000)


    fun renderScene() {
        val positions = ComponentsManager.getComponent(ComponentType.POSITION) as Component2D
        val circles = ComponentsManager.getComponent(ComponentType.SHAPE_CIRCLE) as Component1D

        Raylib.rlEnableVertexArray(VAO_ID)
        Raylib.rlEnableShader(PROGRAM_ID)
        for (i in 0 until positions.freeIdx) {
            val x = positions.x[i].toFloat()
            val y = positions.y[i].toFloat()
            POSITION_BUFFER_VIEW.put(2 * i, x)
            POSITION_BUFFER_VIEW.put(2 * i + 1, y)
        }
        for (i in 0 until circles.freeIdx) {
            RADIUS_BUFFER_VIEW.put(i, circles.values[i].toFloat())
        }
        Raylib.rlUpdateVertexBuffer(VBO_ID_POSITIONS, POSITION_BUFFER, positions.freeIdx * 2 * 4, 0)
        Raylib.rlUpdateVertexBuffer(VBO_ID_RADIUS, RADIUS_BUFFER, circles.freeIdx * 4, 0)
        Raylib.rlDrawVertexArrayInstanced(0, 6, positions.freeIdx)
        Raylib.rlDisableShader()
        Raylib.rlDisableVertexArray()
        Raylib.rlDrawRenderBatchActive()
        /*    drawObjects()
            drawGrid()
            drawSquare()*/
    }


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
                            DrawCircle(x.toInt(), y.toInt(), r.toDouble(), raylibColor)
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

    private fun loadShader(path: String): String =
        javaClass.getResource(path)?.readText()
            ?: error("Couldn't find shader: $path")
}

