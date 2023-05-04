package com.gameoflife

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color

enum class State(){
    Alive, Death
}

class Cell(
    val positionX: Int,
    val positionY: Int,
    state: State = State.Death,
): Renderable {
    companion object{
        const val SIZE: Int = 20
        val COLOR_ALIVE: Color = Color.BLACK
        val COLOR_DEATH: Color = Color.WHITE
    }

    var state: State            // current state
    private var newState: State // calculated new state

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
            aliveNeighbours == 3 && state == State.Death -> State.Alive                             // Any dead cell with three live neighbours becomes a live cell.
            aliveNeighbours < 2 -> State.Death                                                      // Any live cell with fewer than two live neighbours dies, as if by underpopulation.
            state == State.Alive && (aliveNeighbours == 2 || aliveNeighbours == 3) -> State.Alive   // Any live cell with two or three live neighbours survives.
        else -> State.Death                                                                         // All other live cells die in the next generation. Similarly, all other dead cells stay dead.
        }
    }

    // Sets the new state based on the newState
    fun setNewState(){
        state = newState
    }

    // Draws the Cell
    override fun render(graphicsContext: GraphicsContext) {
        // Set the cell's color
        graphicsContext.fill = if(state == State.Alive){
            //COLOR_ALIVE
            colorful(positionX, positionY)
        }
        else{
            COLOR_DEATH
        }

        // Draw the cell
        graphicsContext.fillRect(positionX.toDouble(), positionY.toDouble(), SIZE.toDouble()-1, SIZE.toDouble()-1)
    }

    private fun colorful(posX: Int, posY: Int): Color{
        val xColor = 1.0 / Game.WIDTH
        val yColor = 1.0 / Game.HEIGHT

        val col = Color.color(
            (xColor * posX),
            (yColor * posY),
            0.6
        )

        return col
    }
}