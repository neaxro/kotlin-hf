package com.gameoflife

import java.io.File
import java.sql.Timestamp
import java.text.SimpleDateFormat

class UniverseLoader {

    companion object{
        const val saveDirectoryPath: String = "src/main/resources"
    }

    fun getLevelFileNames(): List<String>{
        val files = mutableListOf<String>()

        File(saveDirectoryPath).walkTopDown().forEach { file ->
            if(file.name.endsWith(".gol")) {       // gol = Game Of Life
                files.add(file.name)
            }
        }

        return files
    }

    fun saveLevel(cells: List<Cell>, fileName: String = ""){
        val formatter = SimpleDateFormat("yyyyMMdd_HHmmss")
        val defaultFileName = formatter.format(Timestamp(System.currentTimeMillis()))

        val filePath = if(fileName.isEmpty()){
            "$saveDirectoryPath/level_$defaultFileName.gol"
        }
        else{
            "$saveDirectoryPath/$fileName.gol"
        }

        // Converts from State to Integer (0 -> Death, 1 -> Alive)
        val text = cells.map { cell ->
            if(cell.state == State.Alive){
                "1"
            }
            else{
                "0"
            }
        }.joinToString(separator = ";")

        File(filePath).writeText(text)
    }

    fun loadLevel(name: String, cells: List<Cell>){
        val data = File("$saveDirectoryPath/$name").readText().split(";")

        // Converts back from Integer to State (0 -> Death, 1 -> Alive)
        cells.forEachIndexed { index, cell ->
            cell.state = if(data[index] == "1"){
                State.Alive
            }
            else{
                State.Death
            }
        }
    }
}