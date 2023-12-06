package com.example.sudokuapp

data class SudokuPlayerModel(
    var username: String? = null,
    var userEmail:String? = null,
    var pfpPath: String? = "pfps/defaultPfp.jpg",
    var userId: String? = null,
    var minTime: Int? = null,
    var minMoves: Int? = null,
    var highScore: Int? = 0,
    var dailyStreak: Int? = 0,
    var gamesFinished: Int? = 0,
    var averageTime: Int? = 0,
    var lastDay: String? = "1970-10-10")