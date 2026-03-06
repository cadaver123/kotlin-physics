package systems

import Environment
import components.generic.Component2D
import components.ComponentType
import components.ComponentsManager
import systems.interfaces.SimulationSystem
import kotlin.concurrent.withLock

class PositionSystemX : SimulationSystem {
    override fun updateState(dt: Double) {
        val velocities = ComponentsManager.getComponent(ComponentType.VELOCITY) as Component2D
        val positions = ComponentsManager.getComponent(ComponentType.POSITION) as Component2D
        positions.reentryDataLock.readLock().withLock {
            velocities.entitiesMap.forEach { entityId, indexV ->
                val positionIndex = positions.entitiesMap[entityId]
                if (positionIndex != null) {
                    velocities.reentryDataLock.writeLock().withLock {
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
                    }
                }
            }
        }
    }
}