package systems

import components.*
import systems.interfaces.SimulationSystem

class GravitationalSystemX() : SimulationSystem {
    override fun updateState(dt: Double) {
/*
        val gravitySources = ComponentsManager.getComponent(ComponentType.GRAVITY_SOURCE) as GravitySourceComponent
        val velocities = ComponentsManager.getComponent(ComponentType.VELOCITY) as VelocityComponent
        val positions = ComponentsManager.getComponent(ComponentType.POSITION) as PositionComponent
*/

/*        gravitySources.reentryDataLock.readLock().withLock {
            for (gravitySourceEntityId in gravitySources.entitiesMap.keys()) {
                val gravityStrength = gravitySources.values[gravitySources.entitiesMap[gravitySourceEntityId]!!]
                positions.reentryDataLock.readLock().withLock {
                    velocities.reentryDataLock.writeLock().withLock {
                        for (entityId in velocities.entitiesMap.keys()) {
                            val velocityIndex = velocities.entitiesMap[entityId]!!
                            val positionIndex = positions.entitiesMap[entityId]
                            val gravitySourcePositionIndex = positions.entitiesMap[gravitySourceEntityId]!!
                            if (positionIndex != null) {
                                val x1 = positions.x[gravitySourcePositionIndex]
                                val y1 = positions.y[gravitySourcePositionIndex]
                                val x2 = positions.x[positionIndex]
                                val y2 = positions.y[positionIndex]
                                val distanceSquared = Vector.distanceSquared(x1, y1, x2, y2)
                                val gravityAcceleration = gravityStrength / distanceSquared
                                val distance = sqrt(distanceSquared)
                                velocities.x[velocityIndex] += (x1 - x2) / distance * Environment.GRAVITATIONAL_CONSTANT * gravityAcceleration * dt
                                velocities.y[velocityIndex] += (y1 - y2) / distance * Environment.GRAVITATIONAL_CONSTANT * gravityAcceleration * dt

                            }
                        }
                    }
                }
            }
        }*/
    }
}