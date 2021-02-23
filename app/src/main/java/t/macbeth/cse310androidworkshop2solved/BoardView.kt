package t.macbeth.cse310androidworkshop2solved

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

/* This is a custom view for the tic-tac-toe grid.  It also maintains the
   pieces and supports the drawing of the pieces.  The game logic is **not**
   in this class.
 */
class BoardView : View {

    // Used to change characteristics of Canvas components
    private val paint = Paint()

    // List of pieces associated with the display
    private val pieces = mutableListOf(Piece.EMPTY, Piece.EMPTY, Piece.EMPTY,
        Piece.EMPTY, Piece.EMPTY, Piece.EMPTY,
        Piece.EMPTY, Piece.EMPTY, Piece.EMPTY)

    // Constructors needed by Android View's
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    /* Contains all of our drawing for this view */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = this.width.toFloat()
        val height = this.height.toFloat()
        // Draw the grid
        canvas.drawLine(width/3.0f, 0.0f, width/3.0f, height, paint)
        canvas.drawLine(2.0f*width/3.0f, 0.0f, 2.0f*width/3.0f, height, paint)
        canvas.drawLine(0.0f, height/3.0f, width, height/3.0f, paint)
        canvas.drawLine(0.0f, 2.0f*height/3.0f, width, 2.0f*height/3.0f, paint)
        // Draw the pieces
        for (pos in 0 until pieces.size) {
            drawPiece(pieces[pos].text, pos, canvas)
        }
    }

    /* Draw a single piece */
    private fun drawPiece(mark: String, pos: Int, canvas: Canvas) {
        // Determine row and column
        val row = pos / 3
        val col = pos % 3
        val height = this.height
        val width = this.width
        // Determine x and y based on the row and column.  The adjustment
        // of 45 is based on the font size of the piece
        val x = (width * col / 3.0f) + (width / 6.0f) - 45.0f
        val y = (height * row / 3.0f) + (height / 6.0f) + 45.0f
        // Set font size to something big
        paint.textSize = 180.0f
        // Draw the mark
        canvas.drawText(mark, x, y, paint)
    }

    /* Convert an (x,y) coordinate to a position 0..8 */
    fun getPosition(x: Float, y:Float): Int {
        val height = this.height
        val width = this.width
        val col = x.toInt() / (width / 3)
        val row = y.toInt() / (height / 3)
        return (row * 3) + col
    }

    /* Change a piece on the board */
    fun changePiece(position: Int, value: Piece) {
        // Perform some error checking
        if (position < 0 || position >= pieces.size) return
        pieces[position] = value
        // Redraw the view
        invalidate()
    }

    /* Get the piece at a position */
    fun getPiece(position: Int) : Piece {
        // Perform some error checking
        if (position < 0 || position >= pieces.size) return Piece.EMPTY
        return pieces[position]
    }

    /* Restart the board by clearing all the pieces */
    fun restartBoard() {
        for (pos in 0 until pieces.size) {
            pieces[pos] = Piece.EMPTY
        }
        // Redraw the view
        invalidate()
    }

}