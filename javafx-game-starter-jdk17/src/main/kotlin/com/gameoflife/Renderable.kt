package com.gameoflife

import javafx.scene.canvas.GraphicsContext

interface Renderable {
    fun render(graphicsContext: GraphicsContext)
}