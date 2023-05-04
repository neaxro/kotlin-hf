package com.gameoflife

import javafx.scene.canvas.GraphicsContext
import java.util.Random
import kotlin.math.floor

class Universe(
    private val canvasWidth: Int,
    private val canvasHeight: Int,
) {
    private val cells = mutableListOf<Cell>()
    var isSimulating: Boolean = false

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
                    //state = if(random.nextFloat() < 0.35f) State.Alive else State.Death
                ))
            }
        }
    }

    fun drawCells(graphicsContext: GraphicsContext){
        cells.forEach { cell ->
            (cell as? Renderable)?.render(graphicsContext)
        }
    }

    fun invertCell(mouseX: Double, mouseY: Double){
        if(isSimulating) return

        println("[MOUSE CLICK] x: $mouseX\ty: $mouseY")

        val posX: Int = floor(mouseX / Cell.SIZE).toInt()
        val posY: Int = floor(mouseY / Cell.SIZE).toInt()
        println("\t--> [CELL POS] x: $posX\ty: $posY")

        val cellIndex: Int = getIndexFromCellPos(posX, posY)
        println("\t--> [CELL INDEX] index: $cellIndex")

        try {
            cells[cellIndex].invert()
        } catch (e: Exception){
            println("Clicked out of canvas...")
        }
    }

    private fun getNeighbours(cellX: Int, cellY: Int): List<Cell> {

        val maxX: Int = floor(canvasWidth / Cell.SIZE.toDouble()).toInt()
        val maxY: Int = floor(canvasHeight / Cell.SIZE.toDouble()).toInt()

        val neighbours = mutableListOf<Cell>()

        // top row
        if(0 < cellY){
            if(cellX > 0)
                neighbours.add(cells[(getIndexFromCellPos(cellX-1, cellY-1))])
            neighbours.add(cells[(getIndexFromCellPos(cellX, cellY-1))])
            if(cellX < maxX-1)
                neighbours.add(cells[(getIndexFromCellPos(cellX+1, cellY-1))])
        }

        // middle row
        if(cellX > 0)
            neighbours.add(cells[(getIndexFromCellPos(cellX-1, cellY))])
        if(cellX < maxX-1)
            neighbours.add(cells[(getIndexFromCellPos(cellX+1, cellY))])

        // bottom row
        if(cellY+1 < maxY){
            if(cellX > 0)
                neighbours.add(cells[(getIndexFromCellPos(cellX-1, cellY+1))])
            neighbours.add(cells[(getIndexFromCellPos(cellX, cellY+1))])
            if(cellX < maxX-1)
                neighbours.add(cells[(getIndexFromCellPos(cellX+1, cellY+1))])
        }

        return neighbours
    }

    private fun getIndexFromCellPos(cellX: Int, cellY: Int): Int{
        return (cellY * floor(canvasWidth / Cell.SIZE.toDouble()) + cellX).toInt()
    }

    fun invertNeighbours(mouseX: Double, mouseY: Double){
        val posX: Int = floor(mouseX / Cell.SIZE).toInt()
        val posY: Int = floor(mouseY / Cell.SIZE).toInt()

        val cells = getNeighbours(posX, posY)
        cells.forEach {
            it.invert()
        }
    }

    fun clearUniverse(){
        cells.forEach {  cell ->
            if(cell.state == State.Alive){
                cell.invert()
            }
        }
    }

    fun simulate(){
        if(!isSimulating) return

        // Calculate new state
        cells.forEach { cell ->
            val neighbours = getNeighbours(
                floor(cell.positionX.toDouble() / Cell.SIZE).toInt(),
                floor(cell.positionY.toDouble() / Cell.SIZE).toInt(),
            )

            cell.calcNewState(neighbours)
        }

        // Set new state
        cells.forEach { cell ->
            cell.setNewState()
        }
    }
}