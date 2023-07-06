package components.shapes

import java.awt.Color

class Circle(var radius: Double, color: Color): Shape(color) {
    operator fun component1() = radius
    operator fun component2() = color
}