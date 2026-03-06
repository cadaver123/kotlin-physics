package entities;

import components.*
import components.interfaces.Component
import components.shapes.CircleComponent
import java.util.BitSet
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
class Entity(vararg val components: Component) {
    companion object {
        val lastId = AtomicInteger(0)
        val componentsSet: Array<BitSet> = Array(10000) { BitSet(64) }
    }

    val id = lastId.getAndIncrement()



    fun addPosition(initialX: Double, initialY: Double) {
        (ComponentsManager.getComponent(ComponentType.POSITION) as PositionComponent).attachComponentToEntity(id, initialX, initialY)
        componentsSet[id].set(ComponentType.POSITION.ordinal)
    }

    fun addVelocity(initialX: Double, initialY: Double) {
        (ComponentsManager.getComponent(ComponentType.VELOCITY) as VelocityComponent).attachComponentToEntity(id, initialX, initialY)
        componentsSet[id].set(ComponentType.VELOCITY.ordinal)
    }

    fun addCircle(radius: Double) {
        (ComponentsManager.getComponent(ComponentType.SHAPE_CIRCLE) as CircleComponent).attachWithLock(id, radius)
        componentsSet[id].set(ComponentType.SHAPE_CIRCLE.ordinal)
    }

    fun addColor(initialR: Byte, initialG: Byte, initialB: Byte) {
        (ComponentsManager.getComponent(ComponentType.COLOR) as ColorComponent).attachWithLock(id, initialR, initialG, initialB)
        componentsSet[id].set(ComponentType.COLOR.ordinal)
    }

    fun addGravityForce(strength: Double) {
        (ComponentsManager.getComponent(ComponentType.GRAVITY_SOURCE) as GravitySourceComponent).attachWithLock(id, strength)
        componentsSet[id].set(ComponentType.GRAVITY_SOURCE.ordinal)
    }


    fun <T : Component> getComponent(clazz: KClass<T>): T? =
        components.find { clazz.java.isAssignableFrom(it::class.java) } as T?

    fun <T : Component> hasComponent(clazz: KClass<T>): Boolean =
        components.any { clazz.java.isAssignableFrom(it::class.java) }

}