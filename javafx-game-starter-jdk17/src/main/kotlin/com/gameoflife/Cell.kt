package com.gameoflife

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color

enum class State(boolean: Boolean){
    Alive(true),
    Death(false)
}

class Cell(
    private val positionX: Int,
    private val positionY: Int,
    state: State = State.Death,
): Renderable {
    companion object{
        const val SIZE: Int = 20
        val COLOR_ALIVE: Color = Color.BLACK
        val COLOR_DEATH: Color = Color.WHITE
    }

    var state: State
        private set
    private var newState: State

    init {
        this.state = state
        newState = this.state
    }

    // For the user click event
    fun invert(){
        state = if(state == State.Alive){
            State.Death
        } else{
            State.Alive
        }
    }

    // Calculates the new state based on the Game of life rules
    fun calcNewState(neighbours: List<Cell>){
        val aliveNeighbours: Int = neighbours.map { cell ->
            if(cell.state == State.Alive) 1
            else 0
        }.sum()

        newState = when {
            aliveNeighbours == 3 && state == State.Death -> State.Alive // Any dead cell with three live neighbours becomes a live cell.
            aliveNeighbours == 2 || aliveNeighbours == 3 -> State.Alive // Any live cell with two or three live neighbours survives.
            else -> State.Death                                         // All other live cells die in the next generation. Similarly, all other dead cells stay dead.
        }
    }

    // Sets the new state based on the newState
    fun changeState(){
        state = newState
    }

    // Draws the Cell
    override fun render(graphicsContext: GraphicsContext) {
        // Set the cell's color
        graphicsContext.fill = if(state == State.Alive){
            COLOR_ALIVE
        }
        else{
            COLOR_DEATH
        }

        // Draw the cell
        graphicsContext.fillRect(positionX.toDouble(), positionY.toDouble(), SIZE.toDouble(), SIZE.toDouble())
    }
}