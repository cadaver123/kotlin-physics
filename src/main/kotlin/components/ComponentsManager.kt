package components

import components.interfaces.Component
import components.shapes.CircleComponent
import kotlin.reflect.full.createInstance

class ComponentsManager {
    companion object {
        private val components: Array<Component> = arrayOf(
            PositionComponent(10000),
            VelocityComponent(10000),
            ColorComponent(),
            CircleComponent(10000),
            GravitySourceComponent(10000)

        )

        fun getComponent(type: ComponentType): Component {
            return components[type.ordinal]
        }
    }
}