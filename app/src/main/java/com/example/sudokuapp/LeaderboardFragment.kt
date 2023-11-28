package com.example.sudokuapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView


class LeaderboardFragment : Fragment() {

    private val t3 = "\t\t\t"   // triple indent character, probably better way to do this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_leaderboard, container, false)

        val arrayAdapter: ArrayAdapter<*>
        val users = mutableListOf<String>()

        fun appendScore(rank: String, stat: String, uid: String) {
            users.add(rank + t3 + stat + t3 + uid)
        }

        // get stats from database, call appendScore() in a loop to populate leaderboard
        appendScore("1", "00:00:01", "JohnDoe123")

        var mListView = view.findViewById<ListView>(R.id.leaderboardListView)
        arrayAdapter = ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, users)
        mListView.adapter = arrayAdapter

        return view
    }

    companion object {
        fun newInstance(): LeaderboardFragment{
            return LeaderboardFragment()
            }
    }
}