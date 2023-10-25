package com.example.sudokuapp

import java.util.Date

data class SudokuPlayerModel(
    var userEmail:String? = null,
    var userId: String? = null,
    var minTime: Int? = null,
    val minMoves: Int? = null,
    var highScore: Int? = null,
    var dailyStreak: Int? = null,
    var dailyMinTime: Int? = null,
    var lastDay: Date? = null)