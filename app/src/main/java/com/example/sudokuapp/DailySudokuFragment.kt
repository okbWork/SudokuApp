package com.example.sudokuapp

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.time.LocalDate


class DailySudokuFragment : Fragment() ,SudokuBoardView.OnTouchListener {
        private lateinit var viewModel: PlaySudokuViewModel
        private lateinit var numberButtons: List<Button>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_daily_challenge, container, false)
        // Inflate the layout for this fragment
        val today = LocalDate.now().toString()
        var dailyPuzzle: Array<IntArray> = Array(9) { IntArray(9) {0} }
        var dailySolution: Array<IntArray> = Array(9) { IntArray(9) {0}}
            val database = Firebase.database
        val myRef = database.getReference("DailyPuzzles")
        val dailyPuzzleQ = myRef.child(today).get()
        val lc = LineConverter()
        dailyPuzzleQ.addOnSuccessListener {d ->
        if(d.getValue(DailySudokuGameModel::class.java) != null){
            Log.i("firebase", "Found Today's Puzzle")
            var daily = d.getValue(DailySudokuGameModel::class.java)
            if (daily != null) {
                dailyPuzzle = lc.lineToGrid(daily.puzzle!!)
                dailySolution = lc.lineToGrid(daily.solution!!)
                Log.i("puzzle", dailyPuzzle[0][0].toString())
                Log.i("solution", dailySolution[0][0].toString())
            }

        }else{
            Log.e("firebase", "No Daily Puzzle Found")
            Log.d("Update: ", "Attempting Add")
            val gameGenerator = SudokuGameGenerator()
            Log.d("Update: ", "Puzzle Built")
            dailyPuzzle = gameGenerator.grid
            dailySolution = gameGenerator.solution
            val puzzle = DailySudokuGameModel(
                today,
                lc.gridToLine(gameGenerator.grid),
                lc.gridToLine(gameGenerator.solution)
            )
            myRef.child(today).setValue(puzzle)
                .addOnSuccessListener {
                    Log.d("Update: ", "Puzzle added")
                }
        }
            val sudokuBoardView = view.findViewById<SudokuBoardView>(R.id.sudokuBoardViewD)
            sudokuBoardView.registerListener(this)

            fun updateCells(cells: List<Cell>?) = cells?.let {
                sudokuBoardView.updateCells(cells)
            }

            fun updateSelectedCellUI(cell: Pair<Int, Int>?) = cell?.let {
                sudokuBoardView.updateSelectedCellUI(cell.first, cell.second)
            }
            viewModel = ViewModelProviders.of(this).get(PlaySudokuViewModel::class.java)
            viewModel.sudokuGame.puzzle = lc.gridToLine(dailyPuzzle)
            viewModel.sudokuGame.solution = lc.gridToLine(dailySolution)
            viewModel.sudokuGame.start()
            viewModel.sudokuGame.selectedCellLiveData.observe(viewLifecycleOwner, Observer { updateSelectedCellUI(it) })
            viewModel.sudokuGame.cellsLiveData.observe(viewLifecycleOwner, Observer { updateCells(it) })

            val buttons = listOf(view.findViewById<Button>(R.id.oneButtonD),
                view.findViewById<Button>(R.id.twoButtonD),
                view.findViewById<Button>(R.id.threeButtonD),
                view.findViewById<Button>(R.id.fourButtonD),
                view.findViewById<Button>(R.id.fiveButtonD),
                view.findViewById<Button>(R.id.sixButtonD),
                view.findViewById<Button>(R.id.sevenButtonD),
                view.findViewById<Button>(R.id.eightButtonD),
                view.findViewById<Button>(R.id.nineButtonD))

            buttons.forEachIndexed { index, button ->
                button.setOnClickListener { viewModel.sudokuGame.handleInput(index + 1) }
            }
        }.addOnFailureListener{
            Log.e("firebase", "No Daily Puzzle Found")
            Log.d("Update: ", "Attempting Add")
            val gameGenerator = SudokuGameGenerator()
            Log.d("Update: ", "Puzzle Built")
            val lc = LineConverter()
            val puzzle = SudokuGameModel(today, lc.gridToLine(gameGenerator.grid), lc.gridToLine(gameGenerator.solution))
            myRef.child(today).setValue(puzzle)
                .addOnSuccessListener{
                    Log.d("Update: ", "Puzzle added")
                }
        }



        return view
    }
    override fun onCellTouched(row: Int, col: Int) {
        viewModel.sudokuGame.updateSelectedCell(row, col)
    }


    companion object {
        fun newInstance(): DailySudokuFragment{
            return DailySudokuFragment()
        }
    }
}