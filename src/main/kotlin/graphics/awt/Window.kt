package graphics.awt

import Environment
import java.awt.Color
import java.awt.Dimension
import java.awt.FlowLayout
import java.util.Timer
import java.util.TimerTask
import javax.swing.BoxLayout
import javax.swing.JCheckBox
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JLayeredPane
import javax.swing.JPanel
import javax.swing.JSlider

class Window : JFrame() {
    private val INITIAL_DELAY = 100
    private val PERIOD_INTERVAL = 16

    private var timer = Timer()
    private val pane = WorkPane()

    init {
        // Window setup
        title = "Simple Physics with Kotlin"
        with(Environment.ENV_SIZE) {
            setSize(x.toInt(), y.toInt())
        }
        setLocationRelativeTo(null)
        defaultCloseOperation = EXIT_ON_CLOSE
        add(pane)
        initAnimation()
    }

    class WorkPane: JLayeredPane() {
        private val mainPlain = MainPlain()
        val menuPanel = JPanel()
        val pauseCheckBox = JCheckBox("Pause")
        val menuCheckBox = JCheckBox("Menu")
        val speedSlider = JSlider(0, 200, 100) // Slider from 0 to 2 (scaled to 0-200)


        init {
            layout = null

            mainPlain.apply {
                background = Color.WHITE
                with(Environment.ENV_SIZE) {
                    setBounds(0, 0, x.toInt(), y.toInt())
                }
            }

            add(mainPlain)
            setLayer(mainPlain, DEFAULT_LAYER)

            // Menu checkbox
            menuCheckBox.background = Color(0, 0, 0, 0)
            menuCheckBox.isOpaque = false
            menuCheckBox.addActionListener {
                if (menuCheckBox.isSelected) {
                    add(menuPanel)
                    setLayer(menuPanel, MODAL_LAYER)
                } else {
                    remove(menuPanel)
                }
                revalidate()
                repaint()
            }
            menuCheckBox.setBounds(10, 10, 80, 20)
            add(menuCheckBox)
            setLayer(menuCheckBox, MODAL_LAYER)
            val pausePanel = JPanel(FlowLayout(FlowLayout.LEFT, 0, 0)).apply {
                maximumSize = Dimension(Int.MAX_VALUE, pauseCheckBox.preferredSize.height)
                background = Color(0, 0, 0, 0) // Transparent background
                isOpaque = false
                add(pauseCheckBox)
            }
            pauseCheckBox.apply {
                background = Color(0, 0, 0, 0) // Transparent background
                isOpaque = false
            }

            // Menu panel setup
            menuPanel.apply {
                layout = BoxLayout(menuPanel, BoxLayout.Y_AXIS)
                preferredSize = Dimension(220, height)
                background = Color(0, 0, 0, 0)
                isOpaque = false


                add(pausePanel)
                add(JLabel("Speed"))
                add(speedSlider)
                setBounds(5, 30, 220, 400)
            }


            // Slider setup
            speedSlider.apply {
                majorTickSpacing = 50
                minorTickSpacing = 10
                paintTicks = true
                paintLabels = true
                background = Color(0, 0, 0, 0) // Transparent background
                isOpaque = false
                addChangeListener {
                    val speed = speedSlider.value / 100.0 // Scale to 0.0 to 2.0
                    // Update animation speed here (e.g., adjust PERIOD_INTERVAL)
                }
            }
        }
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
            if (!pane.pauseCheckBox.isSelected) {
                repaint()
            }
        }
    }
}