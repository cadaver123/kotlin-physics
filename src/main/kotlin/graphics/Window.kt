import graphics.MainPlain
import java.awt.Color
import java.util.*
import javax.swing.JFrame


class Window : JFrame() {
    private val INITIAL_DELAY = 100
    private val PERIOD_INTERVAL = 16

    private var timer = Timer()

    init {
        initUI()
    }

    private fun initUI() {
        val mainPlain = MainPlain()

        mainPlain.setBackground(Color.WHITE);
        add(mainPlain)
        title = "Simple Physics with Kotlin"
        with(Environment.ENV_SIZE) {
            setSize(x.toInt(), y.toInt())
        }
        setLocationRelativeTo(null)
        setDefaultCloseOperation(EXIT_ON_CLOSE)

        initAnimation()
    }

    private fun initAnimation() {
        timer = Timer()
        timer.scheduleAtFixedRate(
            ScheduleTask(),
            INITIAL_DELAY.toLong(), PERIOD_INTERVAL.toLong()
        )
    }

    inner class ScheduleTask : TimerTask() {
        override fun run() {
            repaint();
        }
    }
}
