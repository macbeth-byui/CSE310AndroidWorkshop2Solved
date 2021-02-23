package t.macbeth.cse310androidworkshop2solved

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    // Keep track of whose turn it is.  If the game is over,
    // then this will be set to Piece.EMPTY.
    private var turn = Piece.EMPTY
    private lateinit var boardView: BoardView
    private lateinit var bRestart: Button
    private lateinit var tvInstruct: TextView

    /* Initialize the activity. */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        boardView = findViewById<BoardView>(R.id.boardView)
        bRestart = findViewById<Button>(R.id.bRestart)
        tvInstruct = findViewById<TextView>(R.id.tvInstruct)

        // Listen for clicks on the board and on the button
        boardView.setOnTouchListener(this::handleTouch)
        bRestart.setOnClickListener(this::restartGame)

        // Read the game that was saved.  If not game exists,
        // this function will initialize the game.
        loadGame()
    }

    /* Called if the user is leaving the app for any reason.  We will save
       the game just in case the user is closing the app.
     */
    override fun onPause() {
        super.onPause()
        saveGame()
    }

    /* Respond to touch on the board view.  Determine the position in the grid,
       place a mark (if allowed), change turns, and then check the board (for a
       winner, tie, or game in progress).
     */
    private fun handleTouch(view: View, motionEvent: MotionEvent) : Boolean {
        // Only respond if the game is in progress
        if (turn != Piece.EMPTY) {
            val position = boardView.getPosition(motionEvent.x, motionEvent.y)
            // Only respond if the position has no piece in it yet
            if (boardView.getPiece(position) == Piece.EMPTY) {
                // Add the piece to the board
                boardView.changePiece(position, turn)
                // Change the turn
                turn = when (turn) {
                    Piece.X -> Piece.O
                    else -> Piece.X
                }
                checkBoard()
            }
        }
        // Required by the API
        view.performClick()
        return false
    }

    /* Check the board for a winner, a tie, or a game in progress.  The instructions will
       be updated.  If the game is over, the turn will chang to EMPTY.
     */
    private fun checkBoard() {
        if (checkRow(0, 1, 2) ||
                checkRow(3, 4, 5) ||
                checkRow(3, 4, 5) ||
                checkRow(6, 7, 8) ||
                checkRow(0, 3, 6) ||
                checkRow(1, 4, 7) ||
                checkRow(2, 5, 8) ||
                checkRow(0, 4, 8) ||
                checkRow(2, 4, 6)) {

            tvInstruct.text = "WINNER!"
            turn = Piece.EMPTY
        }
        else if (checkAllPiecesFull()) {

            tvInstruct.text = "TIE!"
            turn = Piece.EMPTY
        }
        else tvInstruct.text = "Turn: ${turn.text}"
    }

    /* Check if a row has all the same pieces either X or Y */
    private fun checkRow(pos1: Int, pos2: Int, pos3: Int): Boolean {
        val piece1 = boardView.getPiece(pos1)
        val piece2 = boardView.getPiece(pos2)
        val piece3 = boardView.getPiece(pos3)
        if (piece1 == piece2 && piece2 == piece3 && piece1 != Piece.EMPTY) return true
        return false
    }

    /* Check if all pieces are full with either X or O. */
    private fun checkAllPiecesFull() : Boolean {
        for (pos in 0..8) {
            if (boardView.getPiece(pos) == Piece.EMPTY) return false
        }
        return true
    }

    /* Start the game over with a clear board and X starting */
    private fun restartGame(view: View) {
        turn = Piece.X
        tvInstruct.text = "Turn: ${turn.text}"
        boardView.restartBoard()
    }

    /* Save the game including the pieces on the board and the current state of 'turn' */
    private fun saveGame() {
        Log.d("Debug","saveGame called")
        val sharedPref = getSharedPreferences("game_data", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("turn",turn.text)
        for (position in 0..8) {
            editor.putString("pos_$position",boardView.getPiece(position).text)
        }
        editor.apply()
    }

    /* Load the game.  If there was no saved game, then initialize the game.  */
    private fun loadGame() {
        Log.d("Debug","loadGame called")
        val sharedPref = getSharedPreferences("game_data", Context.MODE_PRIVATE)
        // Convert the string 'turn' to the enum value
        turn = when (sharedPref.getString("turn", "UNKNOWN")) {
            "X"     -> Piece.X
            "O"     -> Piece.O
            "EMPTY" -> Piece.EMPTY   // Game must be in the "Game Over" state
            else    -> Piece.X       // No game saved so start over
        }
        for (position in 0..8) {
            // Convert the string 'pos_#" to the enum value
            when (sharedPref.getString("pos_$position", "UNKNOWN")) {
                "X"  -> boardView.changePiece(position,Piece.X)
                "O"  -> boardView.changePiece(position,Piece.O)
                else -> boardView.changePiece(position,Piece.EMPTY) // Saved empty or no saved game
            }
        }
        // Determine if there is a winner, tie, or game in progress
        checkBoard()
    }
}