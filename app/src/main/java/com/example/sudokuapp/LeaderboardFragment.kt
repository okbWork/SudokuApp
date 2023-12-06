package com.example.sudokuapp

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
class LeaderboardFragment : Fragment() {

    private val t3 = "\t\t\t"   // triple indent character, probably better way to do this
    private val storageRef = FirebaseStorage.getInstance().reference.child("pfps")
    lateinit var dailyLBAdapter: RecyclerViewAdapter
    lateinit var alltimeLBAdapter: RecyclerViewAdapter
    val dailyLBArray: MutableList<LeaderboardRVEntryModel> = mutableListOf<LeaderboardRVEntryModel>()
    val alltimeLBArray: MutableList<LeaderboardRVEntryModel> = mutableListOf<LeaderboardRVEntryModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    fun getDailyLeaderboard(){
        dailyLBArray.clear()
        val today = LocalDate.now().toString()
        val database = Firebase.database
        val lbRef = database.getReference("DailyLeaderboard")
        val lbArray: MutableList<LeaderboardEntryModel> = mutableListOf<LeaderboardEntryModel>()
        val todayLB = lbRef.child(today).get()
        todayLB.addOnSuccessListener {d ->
            for(p in d.children){
                val playerEntry = p.getValue(LeaderboardEntryModel::class.java)
                lbArray.add(playerEntry!!)
            }
            for(entry in lbArray){
                val userRef = database.getReference("Users").child(entry?.userId!!)
                val userQ = userRef.get()
                userQ.addOnSuccessListener {e ->
                    var user = e.getValue(SudokuPlayerModel::class.java)
                    val rvEntry = LeaderboardRVEntryModel()
                    rvEntry.score = entry?.score
                    rvEntry.pfpPath = user?.pfpPath
                    rvEntry.username = user?.username
                    dailyLBArray.add(rvEntry)
                    dailyLBArray.sortByDescending { it.score }
                    dailyLBAdapter.notifyDataSetChanged()
                }
            }
        }
    }
    fun getAllTimeLeaderboard(){
        alltimeLBArray.clear()
        val database = Firebase.database
        val lbRef = database.getReference("Leaderboard")
        val lbArray: MutableList<LeaderboardEntryModel> = mutableListOf<LeaderboardEntryModel>()
        val todayLB = lbRef.get()
        todayLB.addOnSuccessListener {d ->
            for(p in d.children){
                val playerEntry = p.getValue(LeaderboardEntryModel::class.java)
                lbArray.add(playerEntry!!)
            }
            for(entry in lbArray){
                val userRef = database.getReference("Users").child(entry?.userId!!)
                val userQ = userRef.get()
                userQ.addOnSuccessListener {e ->
                    var user = e.getValue(SudokuPlayerModel::class.java)
                    val rvEntry = LeaderboardRVEntryModel()
                    rvEntry.score = entry?.score
                    rvEntry.pfpPath = user?.pfpPath
                    rvEntry.username = user?.username
                    alltimeLBArray.add(rvEntry)
                    alltimeLBArray.sortByDescending { it.score }
                    alltimeLBAdapter.notifyDataSetChanged()
                }
            }
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_leaderboard, container, false)
        val dailyLBRV: RecyclerView = view.findViewById(R.id.dailyRV)
        val alltimeLBRV: RecyclerView = view.findViewById(R.id.alltimeRV)
        getDailyLeaderboard()
        getAllTimeLeaderboard()
        dailyLBAdapter = RecyclerViewAdapter(view!!.context,dailyLBArray)
        alltimeLBAdapter = RecyclerViewAdapter(view!!.context,alltimeLBArray)

        dailyLBRV.adapter = dailyLBAdapter
        alltimeLBRV.adapter = alltimeLBAdapter

        return view
    }
    inner class RecyclerViewAdapter(
        private val context: Context,
        private val entries: MutableList<LeaderboardRVEntryModel>,
    ): RecyclerView.Adapter<RecyclerViewAdapter.EntryViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.leaderboard_entry_item, parent, false)
            return EntryViewHolder(view)
        }

        override fun getItemCount() = entries.size

        override fun onBindViewHolder(holder: EntryViewHolder, position: Int) {
            val entry = entries[position]
            holder.item = entry
            holder.scoreTV.text = entry.score.toString()
            holder.positionTV.text = (1+position).toString()
            holder.usernameTV.text = entry.username
            Glide.with(holder.itemView)
                .load(storageRef.child(entry.pfpPath!!))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .centerCrop()
                .circleCrop()
                .error(R.drawable.outline_person_24)
                .into(holder.pfpIV)
        }



        inner class EntryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
            var item: LeaderboardRVEntryModel? = null
            val pfpIV: ImageView = itemView.findViewById(R.id.pfpRV)
            val scoreTV: TextView = itemView.findViewById(R.id.scoreRV)
            val usernameTV: TextView = itemView.findViewById(R.id.usernameRV)
            val positionTV: TextView = itemView.findViewById(R.id.placeRV)

            init {
                itemView.setOnClickListener(this)
            }

            override fun onClick(p0: View?) {
            }
        }
    }

    companion object {
        fun newInstance(): LeaderboardFragment{
            return LeaderboardFragment()
            }
    }
}