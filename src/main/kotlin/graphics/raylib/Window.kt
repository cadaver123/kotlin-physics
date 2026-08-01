package graphics.raylib

import Environment
import com.raylib.Colors.RAYWHITE
import com.raylib.Colors.RED
import com.raylib.Raylib.*

class Window {
    companion object {
        fun start() {
            with(Environment.ENV_SIZE) {
                InitWindow(x.toInt(), y.toInt(), "Demo")
            }

            SetTargetFPS(Environment.TARGET_FPS)
            var lastTime = System.nanoTime()
            var framesSinceOptimize = 0
            while (!WindowShouldClose()) {
                val currentTime = System.nanoTime()
                val dt = (currentTime - lastTime) / 1_000_000_000.0 // Convert nanoseconds to seconds
                lastTime = currentTime
                Environment.Runner.run(dt)
                val computeTimeMs  = (System.nanoTime() - lastTime)/1000_000
                val rendererStartTime = System.nanoTime()
                BeginDrawing()
                DrawText("compute: $computeTimeMs", GetScreenWidth() - 150, GetScreenHeight() - 60, 20, RED);
                ClearBackground(RAYWHITE)
                RaylibRenderer.drawObjects()
                RaylibRenderer.drawGrid()
                RaylibRenderer.drawSquare()
                DrawText("fps: ${GetFPS()}", GetScreenWidth() - 150, GetScreenHeight() - 40, 20, RED);
                DrawText("render: ${(System.nanoTime() - rendererStartTime)/1000_000}", GetScreenWidth() - 150, GetScreenHeight() - 20, 20, RED);

                EndDrawing()
                framesSinceOptimize++
                if (framesSinceOptimize >= 20) {
                    Environment.grid.optimize()
                    framesSinceOptimize = 0
                }
            }

            CloseWindow()
            System.exit(0)
        }
    }
}
