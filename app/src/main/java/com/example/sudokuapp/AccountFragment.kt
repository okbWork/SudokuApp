package com.example.sudokuapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class AccountFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        auth = Firebase.auth
        val view = inflater.inflate(R.layout.fragment_account, container, false)
        val logout: Button = view.findViewById(R.id.logoutButton)

        val minTimeText: TextView = view.findViewById(R.id.minTimeVal)
        val minMovesText: TextView = view.findViewById(R.id.minMovesVal)
        val lastDayText: TextView = view.findViewById(R.id.lastDayVal)
        val dailyStreakText: TextView = view.findViewById(R.id.dailyStreakVal)
        val averagePuzzleTimeText: TextView = view.findViewById(R.id.averagePuzzleTimeVal)
        val highScoreText: TextView = view.findViewById(R.id.highScoreVal)
        val puzzlesCompletedText: TextView = view.findViewById(R.id.puzzlesCompletedVal)
        logout.setOnClickListener{
            auth.signOut()
            startActivity(Intent(activity, LoginActivity::class.java))
        }
        val database = Firebase.database
        val userDatabase = database.getReference("Users")
        val updateUserRef = database.getReference("Users")
        val currentUser = auth.currentUser?.let { it1 -> updateUserRef.child(it1.uid).get() }
        currentUser?.addOnSuccessListener { d ->
            if (d.getValue(SudokuPlayerModel::class.java) != null) {
                Log.i("firebase", "Found Player in Users")
                var player = d.getValue(SudokuPlayerModel::class.java)
                if (player != null) {
                    minTimeText.text = player.minTime.toString() + " secs"
                    minMovesText.text = player.minMoves.toString() + " moves"
                    lastDayText.text = player.lastDay.toString()
                    dailyStreakText.text = player.dailyStreak.toString() + " days"
                    averagePuzzleTimeText.text = player.averageTime.toString() + " seconds"
                    highScoreText.text = player.highScore.toString() + " points"
                    puzzlesCompletedText.text = player.gamesFinished.toString() + " puzzles"
                }
            }
        }
        return view
    }

    companion object {
        fun newInstance(): AccountFragment {
            return AccountFragment()
        }
    }
}