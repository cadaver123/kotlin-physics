package components

import components.interfaces.Component
import kotlin.reflect.full.createInstance

class ComponentsManager {
    companion object {
        private val components: Array<Component> = Array(ComponentType.entries.size, init = {
            ComponentType.entries[it].clazz!!.createInstance()
        })

        fun getComponent(type: ComponentType): Component {
            return components[type.ordinal]
        }
    }
}