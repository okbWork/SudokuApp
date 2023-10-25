package com.example.sudokuapp


data class DailySudokuGameModel(
    var date: String?= null,
    var puzzle: MutableList<Int>? = null,
    var solution: MutableList<Int>? = null
)
