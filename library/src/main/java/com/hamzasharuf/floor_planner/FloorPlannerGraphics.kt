package com.hamzasharuf.floor_planner

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import com.hamzasharuf.floor_planner.model.Polygon

/**
 * This class is responsible for rendering a [FloorPlanner] on a canvas.
 * It also takes care of resizing the [Polygon]
 * whenever needed.
 *
 * @author Hamza Sharaf <hamzasharuf20@gmail.com>
 * @property floorPlanner the floor planner object to draw.
 * @constructor Creates the graphics object with specified floor planner.
 */
internal class FloorPlannerGraphics constructor(private val floorPlanner: FloorPlanner) {

    companion object {
        internal const val DEFAULT_MARKER_COLOR = Color.RED

        internal val DEFAULT_STROKE_COLOR = Color.rgb(120, 161, 46)

        internal val DEFAULT_FILL_COLOR = Color.argb(90, 120, 161, 46)
    }

    internal var markerColor = DEFAULT_MARKER_COLOR

    internal var strokeColor = DEFAULT_STROKE_COLOR

    internal var fillColor = DEFAULT_FILL_COLOR

    /**
     * The polygon object which represents the visible [FloorPlannerView] using
     * its embedded vertexes.
     */
    private val polygon: Polygon
        get() = floorPlanner.polygon

    /**
     * The paint object that will be used to draw the marker circles which
     * represents the [polygon] vertexes on the canvas.
     */
    private val markerPaint: Paint by lazy {
        Paint().apply {
            color = markerColor
            style = Paint.Style.FILL
        }
    }

    /**
     * The paint object that will be used to draw the [polygon] stroke which are
     * the lines that connects the [polygon] vertexes.
     */
    private val strokePaint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            color = strokeColor
            strokeWidth = floorPlanner.strokeWidth
            isAntiAlias = true
            isDither = true
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }
    }

    /**
     * The paint object that will be used to draw the [polygon] fill only.
     */
    private val polygonPaint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = fillColor
            isAntiAlias = true
            isDither = true
        }
    }

    /**
     * Draw the full [polygon] with all it's components. This includes drawing
     * the [polygon] vertexes and its boundaries as lines.
     *
     * @param canvas the canvas to draw the [polygon] in.
     */
    fun draw(canvas: Canvas) {
        // Get the polygon path
        val path = getPolygonPath()

        // Draw polygon
        canvas.drawPath(path, polygonPaint)

        // Draw stroke
        canvas.drawPath(path, strokePaint)

        // Draw polygon vertexes
        polygon.vertexes.forEach {
            canvas.drawCircle(
                it.x.toFloat(),
                it.y.toFloat(),
                floorPlanner.markerRadius.toFloat(),
                markerPaint
            )
        }
    }

    /**
     * Draw the path of the current [polygon] using its [Polygon.sides].
     *
     * @return the path of the current [polygon].
     */
    private fun getPolygonPath(): Path {
        val start = polygon.vertexes.first()
        val path = Path()
        path.moveTo(start.x.toFloat(), start.y.toFloat())
        polygon.sides.forEach { path.lineTo(it.end.x.toFloat(), it.end.y.toFloat()) }
        path.close()
        return path
    }

}