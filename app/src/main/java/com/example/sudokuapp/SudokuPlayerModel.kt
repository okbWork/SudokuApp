package com.example.sudokuapp

data class SudokuPlayerModel(
    var userEmail:String? = null,
    var userId: String? = null,
    var minTime: Int? = null,
    var minMoves: Int? = null,
    var highScore: Int? = 0,
    var dailyStreak: Int? = 0,
    var lastDay: String? = "1970-10-10")