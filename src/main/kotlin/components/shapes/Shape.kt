package components.shapes

import components.interfaces.Component

open class Shape(var color: Color): Component {
    class Color(val r: Byte, val g: Byte, val b: Byte)
}