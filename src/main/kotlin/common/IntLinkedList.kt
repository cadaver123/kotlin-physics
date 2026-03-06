package common

class IntLinkedList {
    var cap: Int = 128
    var values: IntArray = IntArray(cap) { -1 }
    var nextElIds: IntArray = IntArray(cap) { -1 }
    var size: Int = 0
    var freeElementId = -1

    fun add(value: Int, nextElId: Int): Int {
        if (freeElementId == -1) {
            return pushBack(value, nextElId)
        }

        val id = freeElementId
        freeElementId = values[id]
        values[id] = value
        nextElIds[id] = nextElId
        return id
    }

    fun pushBack(value: Int, nextElId: Int): Int {
        if(size >= cap) {
            val newCap = (1.25 * cap).toInt()
            val newValueArray = IntArray(newCap) { -1 }
            val newNextEleArray = IntArray(newCap) { -1 }
            System.arraycopy(values, 0, newValueArray, 0, cap);
            System.arraycopy(nextElIds, 0, newNextEleArray, 0, cap);
            values = newValueArray
            nextElIds = newNextEleArray
            cap = newCap
        }

        values[size] = value
        nextElIds[size] = nextElId
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
