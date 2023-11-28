
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.sudokuapp.Cell
import com.example.sudokuapp.LeaderboardEntryModel
import com.example.sudokuapp.Level
import com.example.sudokuapp.LineConverter
import com.example.sudokuapp.PlaySudokuViewModel
import com.example.sudokuapp.R
import com.example.sudokuapp.SudokuBoardView
import com.example.sudokuapp.SudokuGameGenerator
import com.example.sudokuapp.SudokuGameModel
import com.example.sudokuapp.SudokuPlayerModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.util.Locale
import kotlin.math.min
import kotlin.random.Random

class SudokuGameFragment : Fragment(), SudokuBoardView.OnTouchListener {
    private lateinit var viewModel: PlaySudokuViewModel
    private lateinit var numberButtons: List<Button>

    private lateinit var auth: FirebaseAuth
    var seconds = 0
    var moves = 0
    var running = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_daily_challenge, container, false)
        fun runTimer() {

            // Get the text view.
            val timeView = view.findViewById(
                R.id.timeTV
            ) as TextView

            // Creates a new Handler
            val handler = Handler()

            // Call the post() method,
            // passing in a new Runnable.
            // The post() method processes
            // code without a delay,
            // so the code in the Runnable
            // will run almost immediately.
            handler.post(object : Runnable {
                override fun run() {
                    val hours: Int = seconds / 3600
                    val minutes: Int = seconds % 3600 / 60
                    val secs: Int = seconds % 60

                    // Format the seconds into hours, minutes,
                    // and seconds.
                    val time = String.format(
                        Locale.getDefault(),
                        "%d:%02d:%02d", hours,
                        minutes, secs
                    )

                    // Set the text view text.
                    timeView.text = time

                    // If running is true, increment the
                    // seconds variable.
                    if (running){
                        seconds++
                        handler.postDelayed(this, 1000)
                    }

                    // Post the code again
                    // with a delay of 1 second.
                }
            })
        }
        fun stopTimer(){
            running = false
        }
        // Inflate the layout for this fragment
        val today = LocalDate.now().toString()
        var dailyPuzzle: Array<IntArray> = Array(9) { IntArray(9) {0} }
        var dailySolution: Array<IntArray> = Array(9) { IntArray(9) {0}}
        val levelsR = mutableMapOf<String, Level>()
        levelsR["EASY"] = Level.JUNIOR
        levelsR["MID"] = Level.MID
        levelsR["HARD"] = Level.SENIOR
        val levels = mutableMapOf<Level, String>()
        levels[Level.JUNIOR] = "EASY"
        levels[Level.MID] = "MID"
        levels[Level.SENIOR] = "HARD"
        val randLevel = Level.values().random()
        var levelString = levels[randLevel]
        val database = Firebase.database
        val myRef =  database.getReference("Puzzles").child(levelString!!)
        val puzzleList = myRef.get()
        val lc = LineConverter()
        val newPuzzleId = myRef.push().key!!
        val allPuzzles: MutableList<SudokuGameModel> = mutableListOf<SudokuGameModel>()
        puzzleList.addOnSuccessListener { d ->
            for(p in d.children){
                p.getValue(SudokuGameModel::class.java)?.let { allPuzzles.add(it) }
            }
            if(allPuzzles.size>0){
                Log.i("firebase", "Found Today's Puzzle")
                var daily = allPuzzles[Random.nextInt(allPuzzles.size)]
                if (daily != null) {
                    dailyPuzzle = lc.lineToGrid(daily.puzzle!!)
                    dailySolution = lc.lineToGrid(daily.solution!!)
                    Log.i("puzzle", dailyPuzzle[0][0].toString())
                    Log.i("solution", dailySolution[0][0].toString())
                    levelString = daily.diff
                }

            }else{
                Log.e("firebase", "No Puzzles Found")
                Log.d("Update: ", "Attempting Add")
                val gameGenerator = SudokuGameGenerator(randLevel)
                Log.d("Update: ", "Puzzle Built")
                dailyPuzzle = gameGenerator.grid
                dailySolution = gameGenerator.solution
                val puzzle = SudokuGameModel(
                    newPuzzleId,
                    levelString,
                    lc.gridToLine(gameGenerator.grid),
                    lc.gridToLine(gameGenerator.solution)
                )
                myRef.child(newPuzzleId).setValue(puzzle)
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
            runTimer()
            viewModel.sudokuGame.selectedCellLiveData.observe(viewLifecycleOwner, Observer { updateSelectedCellUI(it) })
            viewModel.sudokuGame.cellsLiveData.observe(viewLifecycleOwner, Observer { updateCells(it) })
        }?.addOnFailureListener{
            Log.e("firebase", "No Puzzle Category Found")
            Log.d("Update: ", "Attempting Add")
            val gameGenerator = SudokuGameGenerator()
            Log.d("Update: ", "Puzzle Built")
            val lc = LineConverter()
            val puzzle = SudokuGameModel(newPuzzleId,levelString, lc.gridToLine(gameGenerator.grid), lc.gridToLine(gameGenerator.solution))
            myRef.setValue(puzzle)
                .addOnSuccessListener{
                    Log.d("Update: ", "Puzzle added")
                }
        }

        val diff:TextView = view.findViewById(R.id.difficultyTab)
        diff.text = levelString
        diff.visibility = View.VISIBLE

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
            button.setOnClickListener {
                moves += 1
                viewModel.sudokuGame.handleInput(index + 1)
                if( viewModel.sudokuGame.isFinished()){
                    stopTimer()
                    auth = Firebase.auth
                    var minS = seconds
                    var minM = moves
                    val playerModel = SudokuPlayerModel()
                    val puzzleLeaderBoard = database.getReference("Leaderboard")
                    val updateUserRef = database.getReference("Users")
                    val currentUser = auth.currentUser?.let { it1 -> updateUserRef.child(it1.uid).get() }
                    currentUser?.addOnSuccessListener { d ->
                        if(d.getValue(SudokuPlayerModel::class.java) != null){
                            Log.i("firebase", "Found Player in Users")
                            var player = d.getValue(SudokuPlayerModel::class.java)
                            if (player != null) {
                                playerModel.dailyStreak = player.dailyStreak
                                playerModel.highScore = player.highScore
                                playerModel.minMoves = min(moves, player.minMoves!!)
                                playerModel.minTime = min(seconds, player.minTime!!)
                                playerModel.userEmail = player.userEmail
                                playerModel.userId = player.userId
                                playerModel.highScore = player.highScore
                                playerModel.lastDay = player.lastDay
                                minS = playerModel.minTime!!
                                playerModel.gamesFinished = 1 + player.gamesFinished!!
                                player.averageTime = ((player.gamesFinished!! * player.averageTime!!) + seconds)/(playerModel.gamesFinished!!)
                            }

                        }else{
                            playerModel.dailyStreak = 1
                            playerModel.highScore = 0
                            playerModel.minMoves = moves
                            playerModel.minTime = seconds
                            playerModel.userEmail = auth.currentUser?.email
                            playerModel.userId = auth.currentUser?.uid
                            playerModel.highScore = 0
                            playerModel.gamesFinished = 1
                            playerModel.averageTime = seconds
                            playerModel.lastDay = "1970-10-10"
                            Log.e("firebase", "No User found in Users")
                        }
                        auth.currentUser?.let { it1 ->
                            updateUserRef.child(it1.uid).setValue(playerModel)
                                .addOnSuccessListener {
                                    Log.d("Update: ", "User Updated After Daily Entry")
                                }
                        }
                    }
                    val leaderboardEntryModel = LeaderboardEntryModel(auth.currentUser?.uid, minS, minM)
                    auth.currentUser?.let { it1 ->
                        puzzleLeaderBoard.child(it1.uid).setValue(leaderboardEntryModel).addOnSuccessListener {
                            Log.d("Update: ", "Score Added to Leaderboard")
                        }
                    }
                    buttons.forEachIndexed { _, button ->
                        button.setOnClickListener {}
                    }
                }
            }
        }


        return view
    }

    override fun onCellTouched(row: Int, col: Int) {
        viewModel.sudokuGame.updateSelectedCell(row, col)
    }

    companion object {
        fun newInstance(): SudokuGameFragment{
            return SudokuGameFragment()
        }
    }
}