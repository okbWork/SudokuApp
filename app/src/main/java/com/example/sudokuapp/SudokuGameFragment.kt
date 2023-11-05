import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.sudokuapp.Cell
import com.example.sudokuapp.LineConverter
import com.example.sudokuapp.PlaySudokuViewModel
import com.example.sudokuapp.R
import com.example.sudokuapp.SudokuBoardView
import com.example.sudokuapp.SudokuGameGenerator
import com.example.sudokuapp.SudokuGameModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.random.Random

class SudokuGameFragment : Fragment(), SudokuBoardView.OnTouchListener {
    private lateinit var viewModel: PlaySudokuViewModel
    private lateinit var numberButtons: List<Button>

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_daily_challenge, container, false)
        // Inflate the layout for this fragment
        var dailyPuzzle: Array<IntArray> = Array(9) { IntArray(9) {0} }
        var dailySolution: Array<IntArray> = Array(9) { IntArray(9) {0}}
        val database = Firebase.database
        val myRef = database.getReference("Puzzles")
        val puzzleList = myRef.get()
        val lc = LineConverter()
        val newPuzzleId = myRef.push().key!!
        val allPuzzles: MutableList<SudokuGameModel> = mutableListOf<SudokuGameModel>()
        puzzleList.addOnSuccessListener {d ->
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
                }

            }else{
                Log.e("firebase", "No Puzzles Found")
                Log.d("Update: ", "Attempting Add")
                val gameGenerator = SudokuGameGenerator()
                Log.d("Update: ", "Puzzle Built")
                dailyPuzzle = gameGenerator.grid
                dailySolution = gameGenerator.solution
                val puzzle = SudokuGameModel(
                    newPuzzleId,
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
            viewModel.sudokuGame.selectedCellLiveData.observe(viewLifecycleOwner, Observer { updateSelectedCellUI(it) })
            viewModel.sudokuGame.cellsLiveData.observe(viewLifecycleOwner, Observer { updateCells(it) })
        }.addOnFailureListener{
            Log.e("firebase", "No Puzzle Category Found")
            Log.d("Update: ", "Attempting Add")
            val gameGenerator = SudokuGameGenerator()
            Log.d("Update: ", "Puzzle Built")
            val lc = LineConverter()
            val puzzle = SudokuGameModel(newPuzzleId, lc.gridToLine(gameGenerator.grid), lc.gridToLine(gameGenerator.solution))
            myRef.child(newPuzzleId).setValue(puzzle)
                .addOnSuccessListener{
                    Log.d("Update: ", "Puzzle added")
                }
        }


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
                viewModel.sudokuGame.handleInput(index + 1)
                if( viewModel.sudokuGame.isFinished()){
                    //TODO clock
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