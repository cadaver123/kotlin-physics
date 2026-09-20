package entities;

import components.ColliderComponent
import components.CollisionType
import components.ColorComponent
import components.ComponentType
import components.ComponentsManager
import components.GravitySourceComponent
import components.PositionComponent
import components.VelocityComponent
import components.generic.Component2D
import components.generic.Flags
import components.interfaces.Component
import components.shapes.CircleComponent
import java.util.*
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
class Entity(vararg var components: Component, var flags: Flags) {
    companion object {
        var lastId = 0
        val componentsSet: Array<BitSet> = Array(10000) { BitSet(64) }
    }

    val id = lastId++

    fun addPosition(initialX: Double, initialY: Double) {
        (ComponentsManager.getComponent(ComponentType.POSITION) as Component2D).attachComponentToEntity(
            id,
            initialX,
            initialY
        )
        componentsSet[id].set(ComponentType.POSITION.ordinal)
    }

    fun addVelocity(initialX: Double, initialY: Double) {
        (ComponentsManager.getComponent(ComponentType.VELOCITY) as VelocityComponent).attachComponentToEntity(
            id,
            initialX,
            initialY
        )
        componentsSet[id].set(ComponentType.VELOCITY.ordinal)
    }

    fun addCircle(radius: Double) {
        (ComponentsManager.getComponent(ComponentType.SHAPE_CIRCLE) as CircleComponent).attach(id, radius)
        componentsSet[id].set(ComponentType.SHAPE_CIRCLE.ordinal)
    }

    fun addColor(initialR: Byte, initialG: Byte, initialB: Byte) {
        (ComponentsManager.getComponent(ComponentType.COLOR) as ColorComponent).attach(id, initialR, initialG, initialB)
        componentsSet[id].set(ComponentType.COLOR.ordinal)
    }

    fun addGravityForce(strength: Double) {
        (ComponentsManager.getComponent(ComponentType.GRAVITY_SOURCE) as GravitySourceComponent).attach(id, strength)
        componentsSet[id].set(ComponentType.GRAVITY_SOURCE.ordinal)
    }

    fun addCollision(mass: Double, type: CollisionType) {
        (ComponentsManager.getComponent(ComponentType.COLLISION) as ColliderComponent).attach(id, mass, type)
        componentsSet[id].set(ComponentType.COLLISION.ordinal)
    }


    fun <T : Component> getComponent(clazz: KClass<T>): T? =
        components.find { clazz.java.isAssignableFrom(it::class.java) } as T?

    fun <T : Component> hasComponent(clazz: KClass<T>): Boolean =
        components.any { clazz.java.isAssignableFrom(it::class.java) }

    fun hasComponent(type: ComponentType): Boolean =
        componentsSet[id].get(type.ordinal)

    fun removeComponents() {
        ComponentType.values().forEach {
            if(componentsSet[id].get(it.ordinal)) {
                ComponentsManager.getComponent(it).detach(id)
                componentsSet[id].set(it.ordinal, false)
            }
        }
    }

}