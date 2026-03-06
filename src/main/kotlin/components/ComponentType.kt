package components;

import components.interfaces.Component
import components.shapes.CircleComponent
import kotlin.reflect.KClass

enum class ComponentType(val clazz: KClass<out Component>?) {
    POSITION(PositionComponent::class),
    VELOCITY(VelocityComponent::class),
    COLOR(ColorComponent::class),
    SHAPE_CIRCLE(CircleComponent::class),
    GRAVITY_SOURCE(GravitySourceComponent::class)
}
