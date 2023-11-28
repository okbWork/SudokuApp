package com.example.sudokuapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
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
        logout.setOnClickListener{
            auth.signOut()
            startActivity(Intent(activity, LoginActivity::class.java))
        }

        val arrayAdapter: ArrayAdapter<*>
        val info = mutableListOf<String>()

        // get and append appropriate user data, these are placeholder examples
        info.add("UID: JohnDoe123")
        info.add("Best game: 00:00:01, 9 moves")
        info.add("Latest game: 00:00:02, 10 moves")

        var mListView = view.findViewById<ListView>(R.id.accountInfoListView)
        arrayAdapter = ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, info)
        mListView.adapter = arrayAdapter

        return view
    }

    companion object {
        fun newInstance(): AccountFragment {
            return AccountFragment()
        }
    }
}