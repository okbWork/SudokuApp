package com.example.sudokuapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
class SudokuGameFragment : Fragment(),SudokuBoardView.OnTouchListener {
    private lateinit var viewModel: PlaySudokuViewModel
    private lateinit var numberButtons: List<Button>

    private lateinit var auth: FirebaseAuth
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
        sudokuBoardView.registerListener(this)

        fun updateCells(cells: List<Cell>?) = cells?.let {
            sudokuBoardView.updateCells(cells)
        }

        fun updateSelectedCellUI(cell: Pair<Int, Int>?) = cell?.let {
            sudokuBoardView.updateSelectedCellUI(cell.first, cell.second)
        }

        viewModel = ViewModelProviders.of(this).get(PlaySudokuViewModel::class.java)
        viewModel.sudokuGame.selectedCellLiveData.observe(viewLifecycleOwner, Observer { updateSelectedCellUI(it) })
        viewModel.sudokuGame.cellsLiveData.observe(viewLifecycleOwner, Observer { updateCells(it) })
        val buttons = listOf(view.findViewById<Button>(R.id.oneButton),
            view.findViewById<Button>(R.id.twoButton),
            view.findViewById<Button>(R.id.threeButton),
            view.findViewById<Button>(R.id.fourButton),
            view.findViewById<Button>(R.id.fiveButton),
            view.findViewById<Button>(R.id.sixButton),
            view.findViewById<Button>(R.id.sevenButton),
            view.findViewById<Button>(R.id.eightButton),
            view.findViewById<Button>(R.id.nineButton))

        buttons.forEachIndexed { index, button ->
            button.setOnClickListener { viewModel.sudokuGame.handleInput(index + 1) }
        }
        return view
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