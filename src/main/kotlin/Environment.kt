
import common.Vector
import entities.Entity
import systems.interfaces.SimulationSystem
import kotlin.concurrent.thread

class Environment private constructor() {
    companion object {
        val ENV_SIZE = Vector(1000.0,1000.0)
        val MAX_VELOCITY = 1000.0
        val CENTER_POINT = Vector(ENV_SIZE.x / 2.0, ENV_SIZE.y / 2.0)
        val FINITE_PLANE = true
        val GRAVITANIONAL_CONSTANT = 100.0


        var entities: List<Entity> = mutableListOf()
        var systems: List<SimulationSystem> = mutableListOf()
    }

    class Runner private constructor() {
        companion object {
            val MS_PER_FRAME = 16;
            fun run() {
                systems.forEach {
                    thread {
                        runSystem(it)
                    }
                }
            }

            fun runSystem(system: SimulationSystem) {
                val MS_PER_FRAME = 16L // Example: ~60 FPS
                var lastTime = System.currentTimeMillis()
                while (true) {
                    val currentTime = System.currentTimeMillis()
                    val deltaTime = currentTime - lastTime
                    system.updateState(deltaTime.toDouble())
                    lastTime = currentTime

                    val waitTime = MS_PER_FRAME - (System.currentTimeMillis() - lastTime)
                    if (waitTime > 0) {
                        Thread.sleep(waitTime)
                    } else {
                        System.out.println("dt = $deltaTime")
                    }
                }
            }
        }
    }
}
