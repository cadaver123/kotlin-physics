package entities;

import components.interfaces.Component
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
class Entity(vararg val components: Component) {
    fun <T : Component> getComponent(clazz: KClass<T>): T? =
        components.find { clazz.java.isAssignableFrom(it::class.java) } as T?

    fun <T : Component> hasComponent(clazz: KClass<T>): Boolean =
        components.any { clazz.java.isAssignableFrom(it::class.java) }


}