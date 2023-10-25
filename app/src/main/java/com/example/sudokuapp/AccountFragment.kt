package com.example.sudokuapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
        return view
    }

    companion object {
        fun newInstance(): AccountFragment {
            return AccountFragment()
        }
    }
}