package systems

import Environment
import components.interfaces.Component
import entities.Entity
import systems.interfaces.SimulationSystem
import kotlin.reflect.KClass

abstract class AbstractSystem(
    val clazz: KClass<out Component>,
    val doStateChange: (Entity, Double) -> Unit,
) : SimulationSystem {
    override fun updateState(delta: Double) {
        Environment.entities
            .filter { it.hasComponent(this.clazz) }
            .forEach { doStateChange(it, delta) }
    }

}