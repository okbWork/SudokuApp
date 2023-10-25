package com.example.sudokuapp

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.time.LocalDate


class DailySudokuFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val today = LocalDate.now().toString()
        var dailyPuzzle: Array<IntArray>
        var dailySolution: Array<IntArray>
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
            val puzzle = SudokuGameModel(
                today,
                lc.gridToLine(gameGenerator.grid),
                lc.gridToLine(gameGenerator.solution)
            )
            myRef.child(today).setValue(puzzle)
                .addOnSuccessListener {
                    Log.d("Update: ", "Puzzle added")
                }
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


        return inflater.inflate(R.layout.fragment_daily_challenge, container, false)
    }

    companion object {
        fun newInstance(): DailySudokuFragment{
            return DailySudokuFragment()
        }
    }
}