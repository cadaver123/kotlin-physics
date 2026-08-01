package common

import kotlin.math.max
import kotlin.math.min

class Helper {
    companion object {
        fun clamp(i: Int, maxVal: Int): Int = clamp(i, 0, maxVal)
        fun clamp(i: Int, minVal: Int, maxVal: Int): Int = max(minVal, min(maxVal, i))

    }
}
