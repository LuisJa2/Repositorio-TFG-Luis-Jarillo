package com.arenamix.app.ui.tournament

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.arenamix.app.R
import com.arenamix.app.model.Match
import com.arenamix.app.model.Round

class BracketView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var matches: List<Match> = emptyList()

    // Dimensions (dp → px)
    private val dp = resources.displayMetrics.density
    private val teamBoxW = 100 * dp
    private val teamBoxH = 28 * dp
    private val teamBoxGap = 6 * dp
    private val matchGap = 16 * dp
    private val colGap = 48 * dp
    private val rowPadTop = 20 * dp
    private val labelH = 18 * dp

    // Paints
    private val boxPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }
    private val boxHighlightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FFF3EC")
        style = Paint.Style.FILL
    }
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#E8650A")
        style = Paint.Style.STROKE
        strokeWidth = 1.5f * dp
    }
    private val strokeNormalPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#E0E0E0")
        style = Paint.Style.STROKE
        strokeWidth = 1f * dp
    }
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#BDBDBD")
        style = Paint.Style.STROKE
        strokeWidth = 1.5f * dp
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1A1A1A")
        textSize = 11 * dp
        textAlign = Paint.Align.LEFT
    }
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#757575")
        textSize = 10 * dp
        textAlign = Paint.Align.LEFT
        isFakeBoldText = true
    }
    private val livePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#E53935")
        style = Paint.Style.FILL
    }
    private val liveTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 8 * dp
        textAlign = Paint.Align.LEFT
        isFakeBoldText = true
    }

    fun setMatches(matches: List<Match>) {
        this.matches = matches
        requestLayout()
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val qf = matches.filter { it.round == Round.QUARTER_FINAL }
        val sf = matches.filter { it.round == Round.SEMI_FINAL }
        val f = matches.filter { it.round == Round.FINAL }

        val cols = listOf(qf, sf, f).count { it.isNotEmpty() }
        val maxRows = maxOf(qf.size, sf.size, f.size, 1)

        val w = (teamBoxW + colGap) * cols + teamBoxW + 32 * dp
        val matchH = teamBoxH * 2 + teamBoxGap + matchGap
        val h = rowPadTop + labelH + matchH * maxRows + 32 * dp

        setMeasuredDimension(w.toInt(), h.toInt())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val qfMatches = matches.filter { it.round == Round.QUARTER_FINAL }
        val sfMatches = matches.filter { it.round == Round.SEMI_FINAL }
        val fMatches = matches.filter { it.round == Round.FINAL }

        val columns = mutableListOf<Pair<String, List<Match>>>()
        if (qfMatches.isNotEmpty()) columns.add("CUARTOS DE FINAL" to qfMatches)
        if (sfMatches.isNotEmpty()) columns.add("SEMI-FINALS" to sfMatches)
        if (fMatches.isNotEmpty()) columns.add("FINAL" to fMatches)

        val matchH = teamBoxH * 2 + teamBoxGap + matchGap
        val colWidth = teamBoxW + colGap

        // Store center-Y of each match per column for connector lines
        val colCenterYs = Array(columns.size) { mutableListOf<Float>() }

        columns.forEachIndexed { colIdx, (label, colMatches) ->
            val x = colIdx * colWidth + 16 * dp

            // Draw column label
            canvas.drawText(label, x, rowPadTop + labelH - 4 * dp, labelPaint)

            // Vertical offset: center matches between parent pairs
            val parentColSize = if (colIdx > 0) columns[colIdx - 1].second.size else colMatches.size
            val stride = if (parentColSize > 0 && colMatches.isNotEmpty())
                (parentColSize.toFloat() / colMatches.size) else 1f

            colMatches.forEachIndexed { matchIdx, match ->
                val baseY = rowPadTop + labelH +
                        if (colIdx == 0) matchIdx * matchH
                        else (matchIdx * stride + (stride - 1) / 2) * matchH

                val yA = baseY
                val yB = baseY + teamBoxH + teamBoxGap
                val centerY = yA + teamBoxH + teamBoxGap / 2f
                colCenterYs[colIdx].add(centerY)

                // Draw team boxes
                drawTeamBox(canvas, x, yA, match.teamA, match.isLive)
                drawTeamBox(canvas, x, yB, match.teamB, false)

                // Connector line to next column
                if (colIdx < columns.size - 1) {
                    val lineX = x + teamBoxW
                    val midX = lineX + colGap / 2f
                    canvas.drawLine(lineX, centerY, midX, centerY, linePaint)
                }

                // Connector lines from previous column
                if (colIdx > 0 && colCenterYs[colIdx - 1].size >= (matchIdx + 1) * 2) {
                    val prevMidX = (colIdx - 1) * colWidth + 16 * dp + teamBoxW + colGap / 2f
                    val topY = colCenterYs[colIdx - 1][matchIdx * 2]
                    val botY = colCenterYs[colIdx - 1][matchIdx * 2 + 1]
                    // Vertical line joining two parents
                    canvas.drawLine(prevMidX, topY, prevMidX, botY, linePaint)
                    // Horizontal line to this match
                    canvas.drawLine(prevMidX, centerY, x, centerY, linePaint)
                }
            }
        }
    }

    private fun drawTeamBox(
        canvas: Canvas, x: Float, y: Float,
        team: com.arenamix.app.model.Team, isLive: Boolean
    ) {
        val rect = RectF(x, y, x + teamBoxW, y + teamBoxH)
        val radius = 4 * dp

        canvas.drawRoundRect(rect, radius, radius, if (isLive) boxHighlightPaint else boxPaint)
        canvas.drawRoundRect(rect, radius, radius, if (isLive) strokePaint else strokeNormalPaint)

        // Team name
        val nameX = x + 6 * dp
        val textY = y + teamBoxH / 2f - (textPaint.descent() + textPaint.ascent()) / 2f
        canvas.drawText(team.name, nameX, textY, textPaint)

        // LIVE badge
        if (team.isLive) {
            val badgeW = 26 * dp
            val badgeH = 12 * dp
            val badgeX = x + teamBoxW - badgeW - 4 * dp
            val badgeY = y + (teamBoxH - badgeH) / 2f
            val badgeRect = RectF(badgeX, badgeY, badgeX + badgeW, badgeY + badgeH)
            canvas.drawRoundRect(badgeRect, 3 * dp, 3 * dp, livePaint)
            canvas.drawText("LIVE", badgeX + 3 * dp, badgeY + badgeH - 2.5f * dp, liveTextPaint)
        }
    }
}
