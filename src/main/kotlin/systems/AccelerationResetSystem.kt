package systems

import Environment
import components.ComponentType
import components.ComponentsManager
import components.generic.Component2D
import systems.interfaces.SimulationSystem

class AccelerationResetSystem : SimulationSystem {
    val accelerations = ComponentsManager.getComponent(ComponentType.ACCELERATION) as Component2D

    override fun updateState(dt: Double) {
        accelerations.entitiesMap.forEach { _, idx ->
            accelerations.x[idx] = 0.0
            accelerations.y[idx] = 0.0
        }
    }
}