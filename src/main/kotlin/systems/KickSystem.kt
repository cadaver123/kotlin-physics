package systems

import Environment
import components.ComponentType
import components.ComponentsManager
import components.generic.Component2D
import systems.interfaces.SimulationSystem

class KickSystem : SimulationSystem {
    override fun updateState(dt: Double) {
        val velocities = ComponentsManager.getComponent(ComponentType.VELOCITY) as Component2D
        val accelerations = ComponentsManager.getComponent(ComponentType.ACCELERATION) as Component2D
        velocities.entitiesMap.forEach { entityId, vIdx ->
            val aIdx = accelerations.entitiesMap[entityId]
            if (aIdx != null) {
                velocities.x[vIdx] += accelerations.x[aIdx] * dt/2.0
                velocities.y[vIdx] += accelerations.y[aIdx] * dt/2.0
            }
        }
    }
}