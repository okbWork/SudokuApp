package com.example.sudokuapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth

class SudokuGameFragment : Fragment(),SudokuBoardView.OnTouchListener {
    private lateinit var viewModel: PlaySudokuViewModel
    private lateinit var numberButtons: List<Button>

    private lateinit var auth: FirebaseAuth
    private lateinit var sudokuBoardView: SudokuBoardView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sudoku_game, container, false)
        val sudokuBoardView = view.findViewById<SudokuBoardView>(R.id.sudokuBoardView)
        viewModel = PlaySudokuViewModel()
        sudokuBoardView.registerListener(this)
        return view
    }
    private fun updateCells(cells: List<Cell>?) = cells?.let {
        sudokuBoardView.updateCells(cells)
    }

    private fun updateSelectedCellUI(cell: Pair<Int, Int>?) = cell?.let {
        sudokuBoardView.updateSelectedCellUI(cell.first, cell.second)
    }

    override fun onCellTouched(row: Int, col: Int) {
        viewModel.sudokuGame.updateSelectedCell(row, col)
    }

    companion object {
        fun newInstance(): SudokuGameFragment{
            return SudokuGameFragment()
            }
    }
}