package processing;

import common.Vector
import entities.Entity

class Quadtree(val boundaries: AABB) {
    private val CAPACITY = 8;

    var divided = false
    var northwest: Quadtree? = null
    var northeast: Quadtree? = null
    var southeast: Quadtree? = null
    var southwest: Quadtree? = null

    val entities = mutableListOf<Entity>()

    private fun subdivide() {
        val (x, y) = boundaries.center
        val w = boundaries.halfWidth / 2
        val h = boundaries.halfHeight / 2

        val nw = AABB(Vector(x - w, y - h), w, h)
        val ne = AABB(Vector(x + w, y - h), w, h)
        val se = AABB(Vector(x + w, y + h), w, h)
        val sw = AABB(Vector(x - w, y + h), w, h)

        northwest = Quadtree(nw)
        northeast = Quadtree(ne)
        southeast = Quadtree(se)
        southwest = Quadtree(sw)

        divided = true
    }

    fun insert(e: Entity): Boolean {
        if (!boundaries.intersectsCircle(e)) {
            return false
        }
        if (entities.size < CAPACITY) {
            entities.add(e)
            return true
        }

        if (!divided) {
            subdivide()
            divided = true
        }

        var isInserted = northwest!!.insert(e);
        isInserted = northeast!!.insert(e) || isInserted;
        isInserted = southeast!!.insert(e) || isInserted;
        isInserted = southwest!!.insert(e) || isInserted;

        return isInserted;
    }


    fun queryByCircle(circle: Entity, result: HashSet<Entity> = HashSet()): HashSet<Entity> {
        if (boundaries.intersectsCircle(circle, 1.0)) {
            result.addAll(entities)
            if (divided) {
                northwest!!.queryByCircle(circle, result);
                northeast!!.queryByCircle(circle, result);
                southeast!!.queryByCircle(circle, result);
                southwest!!.queryByCircle(circle, result);
            }
        }

        return result;
    }
}
