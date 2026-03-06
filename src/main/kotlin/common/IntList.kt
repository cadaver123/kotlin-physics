package common

class IntList {
    var cap: Int = 128
    var values: IntArray = IntArray(cap) { -1 }
    var size: Int = 0
    var freeElementId = -1

    fun add(value: Int, nextElId: Int): Int {
        if (freeElementId == -1) {
            return pushBack(value, nextElId)
        }

        val id = freeElementId
        freeElementId = values[id]
        values[id] = value
        return id
    }

    fun pushBack(value: Int, nextElId: Int): Int {
        if(size >= cap) {
            val newCap = (1.25 * cap).toInt()
            val newValueArray = IntArray(newCap) { -1 }
            System.arraycopy(values, 0, newValueArray, 0, cap);
            values = newValueArray
            cap = newCap
        }

        values[size] = value
        return size++
    }

    fun clear() {
        size = 0
        freeElementId = -1
    }

    fun erase(idx: Int) {
        values[idx] = freeElementId;
        freeElementId = idx;
    }
}