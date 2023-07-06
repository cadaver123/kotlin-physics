import common.Vector
import entities.Entity
import systems.interfaces.SimulationSystem
import kotlin.concurrent.thread

class Environment private constructor() {
    companion object {
        val ENV_SIZE = Vector(1000.0,1000.0);
        val MAX_VELOCITY = 1000.0;
        val CENTER_POINT = Vector(Environment.ENV_SIZE.x / 2.0, Environment.ENV_SIZE.y / 2.0)
        val FINITE_PLANE = false
        val GRAVITANIONAL_CONSTANT = 100.0


        var entities: List<Entity> = mutableListOf();
        var systems: List<SimulationSystem> = mutableListOf();
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
                var lastTime: Long = System.currentTimeMillis();
                while (true) {
                    system.updateState((System.currentTimeMillis() - lastTime) / 1000.0)
                    lastTime = System.currentTimeMillis()
                    Thread.sleep(3)
                }
            }
        }
    }
}
