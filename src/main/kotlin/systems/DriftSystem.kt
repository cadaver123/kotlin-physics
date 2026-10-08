package systems

import Environment
import components.ComponentType
import components.ComponentsManager
import components.generic.Component2D
import systems.interfaces.SimulationSystem

class DriftSystem : SimulationSystem {
    val velocities = ComponentsManager.getComponent(ComponentType.VELOCITY) as Component2D
    val accelerations = ComponentsManager.getComponent(ComponentType.ACCELERATION) as Component2D
    val positions = ComponentsManager.getComponent(ComponentType.POSITION) as Component2D

    override fun updateState(dt: Double) {
        velocities.entitiesMap.forEach { entityId, indexV ->
            val positionIndex = positions.entitiesMap[entityId]
            if (positionIndex != null) {
                val x = positions.x[positionIndex]
                val dx = velocities.x[indexV] * dt
                positions.x[positionIndex] = when {
                    Environment.FINITE_PLANE && x + dx < 0 -> Environment.ENV_SIZE.x - x + dx
                    Environment.FINITE_PLANE && x + dx > Environment.ENV_SIZE.x -> x + dx - Environment.ENV_SIZE.x
                    else -> x + dx
                }

                val y = positions.y[positionIndex]
                val dy = velocities.y[indexV] * dt
                positions.y[positionIndex] = when {
                    Environment.FINITE_PLANE && y + dy < 0 -> Environment.ENV_SIZE.y - y + dy
                    Environment.FINITE_PLANE && y + dy > Environment.ENV_SIZE.y -> y + dy - Environment.ENV_SIZE.y
                    else -> y + dy
                }
                if (dy != 0.0 || dx != 0.0) {
                    Environment.grid.updateGrid(entityId)
                }
            }
        }
    }
}