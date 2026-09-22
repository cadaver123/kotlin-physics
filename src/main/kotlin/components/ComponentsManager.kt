package components

import components.interfaces.Component
import components.shapes.CircleComponent
import kotlin.reflect.full.createInstance

class ComponentsManager {
    companion object {
        private var components: Array<Component> = Array<Component>(ComponentType.entries.size) { idx ->
            val type = ComponentType.entries[idx]
            when (type) {
                ComponentType.COLLISION -> ColliderComponent()
                ComponentType.POSITION -> PositionComponent(10000)
                ComponentType.VELOCITY -> VelocityComponent(10000)
                ComponentType.COLOR -> ColorComponent()
                ComponentType.SHAPE_CIRCLE -> CircleComponent(10000)
                ComponentType.MASS -> MassComponent(10000)
                ComponentType.GRAVITY_SOURCE -> GravitySourceComponent(10000)
            }
        }

        fun getComponent(type: ComponentType): Component {
            return components[type.ordinal]
        }
    }
}