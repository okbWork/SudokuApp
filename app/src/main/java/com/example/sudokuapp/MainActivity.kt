package com.example.sudokuapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.FloatRange
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.article_frame_layout, fragment)
        fragmentTransaction.commit()
    }

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = Firebase.auth
        val currentUser = auth.currentUser
        if (currentUser == null) {
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            finish()
        }

        val bottomNavigationBar: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationBar.selectedItemId = R.id.LeaderboardFragmentIcon
        replaceFragment(LeaderboardFragment())
        bottomNavigationBar.setOnItemSelectedListener { item ->
            lateinit var fragment: Fragment
            when (item.itemId) {
                R.id.SudokuFragmentIcon -> fragment = SudokuGameFragment()
                R.id.AccountFragmentIcon -> fragment = AccountFragment()
                R.id.LeaderboardFragmentIcon -> fragment = LeaderboardFragment()
                R.id.DailyFragmentIcon -> fragment = DailyChallengeFragment()
            }
            replaceFragment(fragment)
            true
        }


    }
}