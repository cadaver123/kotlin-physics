package graphics.raylib

import Environment
import com.raylib.Colors
import com.raylib.Colors.RED
import com.raylib.Raylib

class Window {
    companion object {
        fun start() {
            with(Environment.ENV_SIZE) {
                Raylib.InitWindow(x.toInt(), y.toInt(), "Demo")
            }

            Raylib.SetTargetFPS(Environment.TARGET_FPS)
            var lastTime = System.nanoTime()
            var framesSinceOptimize = 0
            RaylibRenderer.init()
            while (!Raylib.WindowShouldClose()) {
                val currentTime = System.nanoTime()
                val dt = (currentTime - lastTime) / 1_000_000_000.0 // Convert nanoseconds to seconds
                lastTime = currentTime
                Environment.Runner.run(dt)
                val computeTimeMs  = (System.nanoTime() - lastTime)/1000_000
                val rendererStartTime = System.nanoTime()
                Raylib.BeginDrawing()
                Raylib.ClearBackground(Colors.WHITE)
                Raylib.DrawText("compute: $computeTimeMs", Raylib.GetScreenWidth() - 150, Raylib.GetScreenHeight() - 60, 20, RED);
                Raylib.DrawText("fps: ${Raylib.GetFPS()}", Raylib.GetScreenWidth() - 150, Raylib.GetScreenHeight() - 40, 20, RED);
                Raylib.DrawText("render: ${(System.nanoTime() - rendererStartTime)/1000_000}", Raylib.GetScreenWidth() - 150, Raylib.GetScreenHeight() - 20, 20, RED);

                RaylibRenderer.renderScene()
                Raylib.EndDrawing()
                framesSinceOptimize++
                if (framesSinceOptimize >= 20) {
                    Environment.grid.optimize()
                    framesSinceOptimize = 0
                }
            }

            Raylib.CloseWindow()
            System.exit(0)
        }
    }
}
