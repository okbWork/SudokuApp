package com.example.sudokuapp.viewmodel

import android.arch.lifecycle.ViewModel
import com.example.sudokuapp.game.SudokuGame

class PlaySudokuViewModel : ViewModel() { // save game state
    val sudokuGame = SudokuGame()
}