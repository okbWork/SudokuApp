package com.example.sudokuapp

data class LeaderboardEntryModel(
    var userId: String? = null,
    var score: Int? = null,
    var time: Int? = null,
    var moves: Int? = null
)
