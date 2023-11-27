package com.example.sudokuapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


class LeaderboardFragment : Fragment() {

    // num. entries to push from database
    private val leaderboardSize = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_leaderboard, container, false)

        // entry template
        val rankView = view.findViewById(R.id.rankTemplate) as TextView
        val uidView = view.findViewById(R.id.uidTemplate) as TextView
        val statView = view.findViewById(R.id.statTemplate) as TextView

        // change to "time"/"moves" as needed
        val statNameView = view.findViewById(R.id.statName) as TextView

        fun pushEntry(uid: String, stat: String) {
            // clone entry template views
        }

        fun populate() {
            // get list of users and stats from database

            for (i in 1..leaderboardSize) {
                // call pushEntry() until leaderboard filled
            }
        }

        return view
    }

    companion object {
        fun newInstance(): LeaderboardFragment{
            return LeaderboardFragment()
            }
    }
}