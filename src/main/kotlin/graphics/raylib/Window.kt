package graphics.raylib

import Environment
import Environment.Companion.TARGET_FPS
import com.raylib.Colors.RAYWHITE
import com.raylib.Colors.RED
import com.raylib.Raylib.*

class Window {
    companion object {
        fun start() {
            with(Environment.ENV_SIZE) {
                InitWindow(x.toInt(), y.toInt(), "Demo")
            }

            SetTargetFPS(TARGET_FPS)

            while (!WindowShouldClose()) {
                BeginDrawing()
                ClearBackground(RAYWHITE)
                RaylibRenderer.drawObjects()
                RaylibRenderer.drawGrid()
                RaylibRenderer.drawSquare()

                DrawText(GetFPS().toString(), GetScreenWidth() - 30, GetScreenHeight() - 40, 20, RED);
                DrawText(Environment.fps.toString(), GetScreenWidth() - 30, GetScreenHeight() - 20, 20, RED);
                EndDrawing()
            }

            CloseWindow()
            System.exit(0)
        }
    }
}
