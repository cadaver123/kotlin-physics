
import common.Vector
import entities.Entity
import systems.interfaces.SimulationSystem
import kotlin.concurrent.thread

class Environment private constructor() {
    companion object {
        const val WIDTH = 1000
        const val HEIGHT = 1000
        val ENV_SIZE = Vector(WIDTH.toDouble(),HEIGHT.toDouble())
        const val MAX_VELOCITY = 1000.0
        val CENTER_POINT = Vector(ENV_SIZE.x / 2.0, ENV_SIZE.y / 2.0)
        const val FINITE_PLANE = true
        const val GRAVITANIONAL_CONSTANT = 100.0
        const val TARGET_FPS = 60

        var entities: List<Entity> = mutableListOf()
        var systems: List<SimulationSystem> = mutableListOf()
        var fps: Long = 0
    }

    class Runner private constructor() {
        companion object {
            fun run() {
                systems.forEach {
                    thread {
                        runSystem(it)
                    }
                }
            }

            fun runSystem(system: SimulationSystem) {
                var lastTime = System.nanoTime()
                var lastPrintedFpsTime = 0L
                val targetDeltaTime = 1.0 / TARGET_FPS // Seconds per frame

                while (true) {
                    val currentTime = System.nanoTime()
                    val deltaTime = (currentTime - lastTime) / 1_000_000_000.0 // Convert nanoseconds to seconds
                    lastTime = currentTime

                    val cappedDeltaTime = minOf(deltaTime, targetDeltaTime * 2.0)

                    system.updateState(cappedDeltaTime)

                    val frameTime = System.nanoTime() - currentTime
                    val sleepTime = (targetDeltaTime * 1_000_000_000 - frameTime) / 1_000_000 // Nanoseconds to milliseconds
                    if (sleepTime > 0) Thread.sleep(sleepTime.toLong())
                    if (currentTime - lastPrintedFpsTime > 500_000_000) {
                        lastPrintedFpsTime = currentTime
                        fps = if (frameTime > 0L) 1_000_000_000/(System.nanoTime() - currentTime) else 0
                    }
                }
            }
        }
    }
}
