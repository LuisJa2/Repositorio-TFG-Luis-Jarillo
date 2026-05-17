package com.arenamix.app.ui.tournament

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.arenamix.app.model.Match
import com.arenamix.app.model.Round
import com.arenamix.app.model.Team

class BracketView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var matches: List<Match> = emptyList()
    private val matchRects = mutableListOf<MatchClickArea>()

    var onMatchClickListener: ((Match) -> Unit)? = null

    // Dimensions (dp → px)
    private val dp = resources.displayMetrics.density
    private val teamBoxW = 110 * dp
    private val teamBoxH = 28 * dp
    private val teamBoxGap = 6 * dp
    private val matchGap = 24 * dp
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
        textSize = 10 * dp
        textAlign = Paint.Align.LEFT
    }
    private val textPlaceholderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#BDBDBD")
        textSize = 10 * dp
        textAlign = Paint.Align.LEFT
        textSkewX = -0.25f
    }
    private val scorePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#E8650A")
        textSize = 12 * dp
        textAlign = Paint.Align.RIGHT
        isFakeBoldText = true
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

    data class MatchClickArea(val match: Match, val rect: RectF)

    fun setMatches(matches: List<Match>) {
        this.matches = matches
        requestLayout()
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val qfCount = 4
        val maxRows = qfCount

        val cols = 3 // QF, SF, F
        val w = (teamBoxW + colGap) * (cols - 1) + teamBoxW + 64 * dp
        val matchH = teamBoxH * 2 + teamBoxGap + matchGap
        val h = rowPadTop + labelH + matchH * maxRows + 32 * dp

        setMeasuredDimension(w.toInt(), h.toInt())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        matchRects.clear()

        val qfMatches = matches.filter { it.round == Round.QUARTER_FINAL }.sortedBy { it.id }
        val sfMatches = matches.filter { it.round == Round.SEMI_FINAL }.sortedBy { it.id }
        val fMatches = matches.filter { it.round == Round.FINAL }.sortedBy { it.id }

        val columns = mutableListOf<Pair<String, List<Match>>>()
        columns.add("CUARTOS DE FINAL" to qfMatches)
        columns.add("SEMI-FINALS" to sfMatches)
        columns.add("FINAL" to fMatches)

        val matchH = teamBoxH * 2 + teamBoxGap + matchGap
        val colWidth = teamBoxW + colGap

        val colCenterYs = Array(columns.size) { mutableListOf<Float>() }

        columns.forEachIndexed { colIdx, (label, colMatches) ->
            val x = colIdx * colWidth + 16 * dp
            canvas.drawText(label, x, rowPadTop + labelH - 4 * dp, labelPaint)

            // Dynamic positioning based on fixed 4-2-1 structure
            val stride = when(colIdx) {
                0 -> 1f
                1 -> 2f
                else -> 4f
            }

            colMatches.forEachIndexed { matchIdx, match ->
                val baseY = rowPadTop + labelH + (matchIdx * stride + (stride - 1) / 2f) * matchH

                val yA = baseY
                val yB = baseY + teamBoxH + teamBoxGap
                val centerY = yA + teamBoxH + teamBoxGap / 2f
                colCenterYs[colIdx].add(centerY)

                val matchRect = RectF(x, yA, x + teamBoxW, yB + teamBoxH)
                matchRects.add(MatchClickArea(match, matchRect))

                drawTeamBox(canvas, x, yA, match.teamA, match.scoreA, match.isLive)
                drawTeamBox(canvas, x, yB, match.teamB, match.scoreB, false)

                // Draw score connections and lines
                if (colIdx < columns.size - 1) {
                    val lineX = x + teamBoxW
                    val midX = lineX + colGap / 2f
                    canvas.drawLine(lineX, centerY, midX, centerY, linePaint)
                }

                if (colIdx > 0) {
                    val prevMidX = x - colGap / 2f
                    val topY = colCenterYs[colIdx - 1].getOrNull(matchIdx * 2) ?: 0f
                    val botY = colCenterYs[colIdx - 1].getOrNull(matchIdx * 2 + 1) ?: 0f
                    
                    if (topY != 0f && botY != 0f) {
                        canvas.drawLine(prevMidX, topY, prevMidX, botY, linePaint)
                        canvas.drawLine(prevMidX, centerY, x, centerY, linePaint)
                    }
                }
            }
        }
    }

    private fun drawTeamBox(
        canvas: Canvas, x: Float, y: Float,
        team: Team?, score: Int, isLive: Boolean
    ) {
        val rect = RectF(x, y, x + teamBoxW, y + teamBoxH)
        val radius = 4 * dp

        canvas.drawRoundRect(rect, radius, radius, if (isLive) boxHighlightPaint else boxPaint)
        canvas.drawRoundRect(rect, radius, radius, if (isLive) strokePaint else strokeNormalPaint)

        val nameX = x + 6 * dp
        val textY = y + teamBoxH / 2f - (textPaint.descent() + textPaint.ascent()) / 2f
        
        if (team != null) {
            canvas.drawText(team.name, nameX, textY, textPaint)
            val scoreX = x + teamBoxW - 6 * dp
            canvas.drawText(score.toString(), scoreX, textY, scorePaint)
        } else {
            canvas.drawText("TBD", nameX, textY, textPlaceholderPaint)
        }

        if (team?.isLive == true) {
            val badgeW = 26 * dp
            val badgeH = 12 * dp
            val badgeX = x + teamBoxW - 45 * dp
            val badgeY = y + (teamBoxH - badgeH) / 2f
            val badgeRect = RectF(badgeX, badgeY, badgeX + badgeW, badgeY + badgeH)
            canvas.drawRoundRect(badgeRect, 3 * dp, 3 * dp, livePaint)
            canvas.drawText("LIVE", badgeX + 3 * dp, badgeY + badgeH - 2.5f * dp, liveTextPaint)
        }
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            val x = event.x
            val y = event.y
            for (area in matchRects) {
                if (area.rect.contains(x, y)) {
                    // Only click if at least one team is assigned
                    if (area.match.teamA != null || area.match.teamB != null) {
                        onMatchClickListener?.invoke(area.match)
                        performClick()
                        return true
                    }
                }
            }
        }
        return true
    }
}
