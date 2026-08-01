package common

class IntLinkedList {
    var cap: Int = 128
    var values: IntArray = IntArray(cap) { -1 }
    var nextElIds: IntArray = IntArray(cap) { -1 }
    var prevElIds: IntArray = IntArray(cap) { -1 }
    var size: Int = 0
    var freeElementId = -1

    fun add(value: Int, nextElId: Int): Int {
        if (freeElementId == -1) {
            return pushBack(value, nextElId)
        }

        val node = freeElementId
        freeElementId = values[node]
        values[node] = value
        nextElIds[node] = nextElId
        prevElIds[node] = -1
        if(nextElId != -1) {
            prevElIds[nextElId] = node
        }

        return node
    }

    fun pushBack(value: Int, nextElId: Int): Int {
        if(size >= cap) {
            val newCap = (1.25 * cap).toInt()
            val newValueArray = IntArray(newCap) { -1 }
            val newNextEleArray = IntArray(newCap) { -1 }
            val newPrevElIds = IntArray(newCap) { -1 }
            System.arraycopy(values, 0, newValueArray, 0, cap);
            System.arraycopy(nextElIds, 0, newNextEleArray, 0, cap);
            System.arraycopy(prevElIds, 0, newPrevElIds, 0, cap);
            values = newValueArray
            nextElIds = newNextEleArray
            prevElIds = newPrevElIds
            cap = newCap
        }

        values[size] = value
        nextElIds[size] = nextElId
        val id = size++
        if(nextElId != -1) {
            prevElIds[nextElId] = id
        }
        return id
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
