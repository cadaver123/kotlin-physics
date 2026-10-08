package common

fun distanceSquared(x1: Double, y1: Double, x2: Double, y2: Double): Double {
    return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)
}
fun distance(x1: Double, y1: Double, x2: Double, y2: Double): Double {
    return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2))
}