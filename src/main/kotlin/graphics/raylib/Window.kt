package graphics.raylib

import Environment
import com.raylib.Colors.RAYWHITE
import com.raylib.Raylib.BeginDrawing
import com.raylib.Raylib.ClearBackground
import com.raylib.Raylib.CloseWindow
import com.raylib.Raylib.EndDrawing
import com.raylib.Raylib.InitWindow
import com.raylib.Raylib.SetTargetFPS
import com.raylib.Raylib.WindowShouldClose

class Window {
    companion object {
        fun start() {
            with(Environment.ENV_SIZE) {
                InitWindow(x.toInt(), y.toInt(), "Demo")
            }

            SetTargetFPS(60)

            while (!WindowShouldClose()) {
                BeginDrawing()
                ClearBackground(RAYWHITE)
                Environment.entities.forEach {
                    RaylibRenderer.drawObject(it)

                    //printQuadtree(g)
            }
                EndDrawing()
            }

            CloseWindow()
        }
    }
}
