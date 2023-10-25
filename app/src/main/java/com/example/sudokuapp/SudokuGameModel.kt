package com.example.sudokuapp

data class SudokuGameModel(
    var id: String? = null,
    var puzzle: MutableList<Int>? = null,
    var solution: MutableList<Int>? = null
)