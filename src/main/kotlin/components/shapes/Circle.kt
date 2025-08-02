package components.shapes

class Circle(var radius: Double, color: Color): Shape(color) {
    operator fun component1() = radius
    operator fun component2() = color
}