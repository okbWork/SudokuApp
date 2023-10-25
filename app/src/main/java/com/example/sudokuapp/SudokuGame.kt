package com.example.sudokuapp

import android.util.Log
import androidx.lifecycle.MutableLiveData
class SudokuGame(
    var puzzle: MutableList<Int>? = MutableList<Int>(9 * 9) {i -> i/9},
    var solution: MutableList<Int>? = MutableList<Int>(9 * 9) {i -> i/9}
){

    var selectedCellLiveData = MutableLiveData<Pair<Int, Int>>()
    var cellsLiveData = MutableLiveData<List<Cell>>()


    private var selectedRow = -1
    private var selectedCol = -1

    private var board: Board

    init {
        val cells = List(9 * 9) {i -> Cell(puzzle?.get(i) ?: (i / 9), i % 9, i % 9)}
        for(cell in cells){
            if(cell.value != 0){
                cell.isStartingCell = true
            }
        }
        board = Board(9, cells)

        selectedCellLiveData.postValue(Pair(selectedRow, selectedCol))
        cellsLiveData.postValue(board.cells)
    }
    fun isFinished(): Boolean{
        for(i in 0..80){
            if (board.cells[i].value== solution?.get(i) ?: 10){
                return false
            }
        }
        return true
    }
    fun start(){
        selectedCellLiveData = MutableLiveData<Pair<Int, Int>>()
        cellsLiveData = MutableLiveData<List<Cell>>()
        selectedRow = -1
        selectedCol = -1
        val cells = List(9 * 9) {i -> Cell(i/9, i % 9, puzzle?.get(i) ?: (i % 9))}
        board = Board(9, cells)

        selectedCellLiveData.postValue(Pair(selectedRow, selectedCol))
        cellsLiveData.postValue(board.cells)
    }

    fun handleInput(number: Int) {
        if (selectedRow == -1 || selectedCol == -1) return
        if (board.getCell(selectedRow, selectedCol).isStartingCell) return
        Log.d("start input", "adding val ${number}")

        board.getCell(selectedRow, selectedCol).value = number
        cellsLiveData.postValue(board.cells)
        Log.d("start input", "method completed.new val is ${board.getCell(selectedRow, selectedCol).value}")

    }


    fun updateSelectedCell(row: Int, col: Int) {
        if (!board.getCell(row, col).isStartingCell) {
            selectedRow = row
            selectedCol = col
            selectedCellLiveData.postValue(Pair(row, col))
        }
    }
}