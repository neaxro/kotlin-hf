package com.gameoflife

import javafx.scene.canvas.GraphicsContext
import java.util.Random
import kotlin.math.floor

class Universe(
    private val canvasWidth: Int,
    private val canvasHeight: Int,
) {
    private val cells = mutableListOf<Cell>()

    init {
        generateCells()
    }

    private fun generateCells(){
        val random = Random()
        val numberInX: Int = floor(canvasWidth / Cell.SIZE.toDouble()).toInt()
        val numberInY: Int = floor(canvasHeight / Cell.SIZE.toDouble()).toInt()

        for(y in 0 until numberInY){
            for(x in 0 until numberInX){
                cells.add(Cell(
                    positionX = Cell.SIZE * x,
                    positionY = Cell.SIZE * y,
                    state = if(random.nextFloat() < 0.5f) State.Alive else State.Death
                ))
            }
        }
    }

    fun drawCells(graphicsContext: GraphicsContext){
        cells.forEach { cell ->
            (cell as? Renderable)?.render(graphicsContext)
        }
    }

    fun changeStateForCell(mouseX: Double, mouseY: Double){
        println("[MOUSE CLICK] x: $mouseX\ty: $mouseY")

        val posX: Int = floor(mouseX / Cell.SIZE).toInt()
        val posY: Int = floor(mouseY / Cell.SIZE).toInt()
        println("\t--> [CELL POS] x: $posX\ty: $posY")

        val cellIndex: Int = (posY * floor(canvasWidth / Cell.SIZE.toDouble()) + posX).toInt()
        println("\t--> [CELL INDEX] index: $cellIndex")

        cells[cellIndex].invert()
    }
}