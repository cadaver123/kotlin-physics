package components.grids

import common.Helper.Companion.clamp

class LooseTightGrid {
    companion object {
        const val TIGHT_CELL_WIDTH: Double = 100.0
        const val TIGHT_CELL_HEIGHT: Double = 100.0
        const val TIGHT_COLUMNS_COUNT: Int = Environment.WIDTH / TIGHT_CELL_WIDTH.toInt()
        const val TIGHT_ROWS_COUNT: Int = Environment.HEIGHT / TIGHT_CELL_HEIGHT.toInt()
        const val TIGHT_CELLS_COUNT: Int = TIGHT_ROWS_COUNT * TIGHT_COLUMNS_COUNT
        const val TIGHT_INV_CELL_WIDTH: Double = 1.0 / TIGHT_CELL_WIDTH
        const val TIGHT_INV_CELL_HEIGHT: Double = 1.0 / TIGHT_CELL_HEIGHT

        var looseGrid: LooseGrid = LooseGrid()

        fun getTightRangeY(y: Int): Int = clamp((y * TIGHT_INV_CELL_HEIGHT).toInt(), TIGHT_ROWS_COUNT - 1)

        fun getTightRangeX(x: Int): Int = clamp(
            (x * TIGHT_INV_CELL_WIDTH).toInt(), TIGHT_COLUMNS_COUNT - 1
        )
    }
}

