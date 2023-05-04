package com.gameoflife

import javafx.animation.AnimationTimer
import javafx.application.Application
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.control.Button
import javafx.scene.control.Slider
import javafx.scene.input.KeyCode
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import javafx.stage.Stage

class Game : Application() {

    companion object {
        private const val WIDTH = 1024
        private const val HEIGHT = 512
    }

    private lateinit var mainScene: Scene
    private lateinit var graphicsContext: GraphicsContext
    private lateinit var universe: Universe

    private var lastFrameTime: Long = System.nanoTime()
    private var elapsedSeconds: Double = 0.0
    private var simulateSleep: Double = 1.0     // Seconds

    // use a set so duplicates are not possible
    private val currentlyActiveKeys = mutableSetOf<KeyCode>()

    override fun start(mainStage: Stage) {
        mainStage.title = "Game of life"

        // Create a Universe
        universe = Universe(WIDTH, HEIGHT)

        val borderPane = BorderPane()

        mainScene = Scene(borderPane)
        mainStage.scene = mainScene

        // Add canvas to the center of the BorderPane
        val canvas = Canvas(WIDTH.toDouble(), HEIGHT.toDouble())
        borderPane.center = canvas

        // Add buttons to the bottom of the BorderPane
        val buttonsHBox = HBox()
        buttonsHBox.spacing = 10.0
        buttonsHBox.padding = Insets(10.0)
        addControllersToBottom(buttonsHBox)
        borderPane.bottom = buttonsHBox

        // Actions handlers
        prepareActionHandlers()

        graphicsContext = canvas.graphicsContext2D
        // Canvas on click listener
        canvas.setOnMouseClicked {
            if(it.isControlDown)
                universe.invertNeighbours(it.x, it.y)
            else
                universe.invertCell(it.x, it.y)
        }

        // Main loop
        object : AnimationTimer() {
            override fun handle(currentNanoTime: Long) {
                tickAndRender(currentNanoTime)
            }
        }.start()

        mainStage.show()
    }

    private fun prepareActionHandlers() {
        mainScene.onKeyPressed = EventHandler { event ->
            currentlyActiveKeys.add(event.code)
        }
        mainScene.onKeyReleased = EventHandler { event ->
            currentlyActiveKeys.remove(event.code)
        }
    }

    private fun tickAndRender(currentNanoTime: Long) {
        // the time elapsed since the last frame, in nanoseconds
        // can be used for physics calculation, etc
        val elapsedNanos = currentNanoTime - lastFrameTime
        lastFrameTime = currentNanoTime

        // clear canvas
        graphicsContext.clearRect(0.0, 0.0, WIDTH.toDouble(), HEIGHT.toDouble())

        // draw background
        graphicsContext.fill = Color.GRAY
        graphicsContext.fillRect(0.0, 0.0, WIDTH.toDouble(), HEIGHT.toDouble())

        // perform world updates
        elapsedSeconds += elapsedNanos * 10e-9
        // println("[ELAPSED SECONDS] $elapsedSeconds")
        if(elapsedSeconds > simulateSleep){
            universe.simulate()
            elapsedSeconds = 0.0
        }

        // draw
        universe.drawCells(graphicsContext)

        // display crude fps counter
        val elapsedMs = elapsedNanos / 1_000_000
        if (elapsedMs != 0L) {
            graphicsContext.fill = Color.WHITE
            graphicsContext.fillText("${1000 / elapsedMs} fps", 10.0, 10.0)
        }
    }

    private fun addControllersToBottom(hBox: HBox){
        val simulateButton = Button("Start")
        simulateButton.setOnMouseClicked {
            universe.isSimulating = !universe.isSimulating
            simulateButton.text = if(universe.isSimulating){
                "Stop"
            }
            else{
                "Start"
            }
        }

        val clearButton = Button("Clear")
        clearButton.setOnMouseClicked {
            universe.clearUniverse()
        }

        val simulateTimeSlider = Slider(0.5, 3.0, 1.0)
        simulateTimeSlider.blockIncrement = 0.1
        simulateTimeSlider.isShowTickLabels = true
        simulateTimeSlider.isShowTickMarks = true
        simulateTimeSlider.valueProperty().addListener { observable, oldValue, newValue ->
            simulateSleep = simulateTimeSlider.value
        }

        hBox.children.addAll(
            simulateButton,
            clearButton,
            simulateTimeSlider,
        )
    }
}
