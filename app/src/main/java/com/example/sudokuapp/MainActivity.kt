package com.example.sudokuapp

import SudokuGameFragment
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.article_frame_layout, fragment)
        fragmentTransaction.commit()
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var gameGenerator: SudokuGameGenerator
    val puzzleMax = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = Firebase.auth
        val currentUser = auth.currentUser
        if (currentUser == null) {
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            finish()
        }

        var puzCount: Long = 0

        println("Update: Adding Puzzles")
        val database= Firebase.database
        /*
        val db = Firebase.firestore
        val query = db.collection("Puzzles")
        val countQuery = query.count()
        countQuery.get(AggregateSource.SERVER).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Count fetched successfully
                val snapshot = task.result
                Log.d(ContentValues.TAG, "Count: ${snapshot.count}")
                puzCount = snapshot.count
            } else {
                Log.d(ContentValues.TAG, "Count failed: ", task.getException())
            }
        }

            Log.d("Update: ", "Attempting Add")
            gameGenerator = SudokuGameGenerator()
            val puzzleId = myRef.push().key!!
            val puzzle = SudokuGameModel(puzzleId, gameGenerator.grid, gameGenerator.solution)
            myRef.child(puzzleId).setValue(puzzle)
                .addOnSuccessListener{
                    Log.d("Update: ", "Puzzle added")
                }
                puzCount += 1
        */

        val levels = mutableMapOf<Level, String>()
        levels[Level.JUNIOR] = "EASY"
        levels[Level.MID] = "MID"
        levels[Level.SENIOR] = "HARD"
        for(i in 0..<0){
            val randLevel = Level.values().random()
            var levelString = levels[randLevel]
            val myRef = database.getReference("Puzzles").child(levelString!!)
            println("Adding puzzle")
            Log.d("Update: ", "Attempting Add")
            gameGenerator = SudokuGameGenerator(randLevel)
            Log.d("Update: ", "Puzzle Built")
            val puzzleId = myRef.push().key!!
            val lc = LineConverter()
            val puzzle = SudokuGameModel(puzzleId, levelString,lc.gridToLine(gameGenerator.grid), lc.gridToLine(gameGenerator.solution))
            myRef.child(puzzleId).setValue(puzzle)
                .addOnSuccessListener{
                    Log.d("Update: ", "Puzzle added")
                }
            puzCount += 1
        }
        Toast.makeText(this, "Puzzle Adding Complete", Toast.LENGTH_SHORT).show()





        val bottomNavigationBar: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationBar.selectedItemId = R.id.LeaderboardFragmentIcon
        replaceFragment(LeaderboardFragment())
        bottomNavigationBar.setOnItemSelectedListener { item ->
            lateinit var fragment: Fragment
            when (item.itemId) {
                R.id.SudokuFragmentIcon -> fragment = SudokuGameFragment()
                R.id.AccountFragmentIcon -> fragment = AccountFragment()
                R.id.LeaderboardFragmentIcon -> fragment = LeaderboardFragment()
                R.id.DailyFragmentIcon -> fragment = DailySudokuFragment()
            }
            replaceFragment(fragment)
            true
        }


    }
}