package com.hamzasharuf.floor_planner

import android.view.MotionEvent
import com.hamzasharuf.floor_planner.model.Point
import com.hamzasharuf.floor_planner.model.Polygon
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * This class is responsible for handling events and propagating them
 * to the [FloorPlanner] object. It offers the user interaction with
 * the vertexes.
 *
 * @author Hamza Sharaf <hamzasharuf20@gmail.com>
 * @property floorPlanner the floor planner object to control.
 * @constructor Creates the controls object for specified floor planner.
 */
internal class FloorPlannerControls(private val floorPlanner: FloorPlanner) {

    internal companion object {
        internal const val DEFAULT_EXTENDED_VERTEX_TOUCH_RADIUS = 30
        internal const val DEFAULT_BOX_PADDING: Float = 50f
    }

    /**
     * Additional imaginary radius to the vertex to make the touch event on the radius more
     * easy for the user.
     */
    internal var extendedVertexTouchRadius = DEFAULT_EXTENDED_VERTEX_TOUCH_RADIUS

    /**
     * Add padding to the surrounding box to prevent the [polygon] from exceeding this padding
     * and to have a sufficient space between the box borders and the [polygon].
     */
    internal var boxPadding = DEFAULT_BOX_PADDING

    /**
     * The draggable object identifies the currently dragged item and whether it is a [Draggable.Vertex] or
     * [Draggable.Polygon] to take action based on the dragged item.
     */
    private var draggable: Draggable? = null

    /**
     * The minimum x coordinate that the polygon vertexes can't exceed.
     */
    private val xMin: Double
        get() = boxPadding.toDouble()

    /**
     * The minimum y coordinate that the polygon vertexes can't exceed.
     */
    private val yMin: Double
        get() = boxPadding.toDouble()

    /**
     * The maximum x coordinate that the polygon vertexes can't exceed.
     */
    private val xMax: Double
        get() = floorPlanner.width.toDouble() - boxPadding

    /**
     * The maximum y coordinate that the polygon vertexes can't exceed.
     */
    private val yMax: Double
        get() = floorPlanner.height.toDouble() - boxPadding


    /**
     * The vertex touch radius which will be used to detect the touch operation
     * on the vertex. It's equal to the radius of the vertex plus the [extendedVertexTouchRadius].
     */
    private val vertexTouchRadius =
        floorPlanner.markerRadius + extendedVertexTouchRadius

    /**
     * The polygon object which represents the visible [FloorPlannerView] using
     * its embedded vertexes.
     */
    private val polygon: Polygon
        get() = floorPlanner.polygon

    /**
     * Handles the given event by updating the polygon or vertexes'
     * position accordingly.
     *
     * @param event The event to process
     */
    fun handleEvent(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> handleActionDown(Point(event.x.toDouble(), event.y.toDouble()))
            MotionEvent.ACTION_MOVE -> handleActionMove(Point(event.x.toDouble(), event.y.toDouble()))
            MotionEvent.ACTION_UP -> handleActionUp()
        }
    }

    /**
     * A pressed gesture has started, the motion contains the initial starting location.
     * Used to handle the starting of vertex dragging operation to identify the exact point
     * which the user wants to drag.
     */
    private fun handleActionDown(point: Point) {
        val index = polygon.vertexes.indexOfFirst { isTouchInsideTheVertex(it, point) }
        draggable = when {
            index >= 0 -> Draggable.Vertex(index)
            polygon.isInside(point) -> Draggable.Polygon(point)
            else -> null
        }
    }

    /**
     * A change has happened during a press gesture (between [MotionEvent.ACTION_DOWN]
     * and [MotionEvent.ACTION_UP]).
     * The motion contains the most recent point, as well as any intermediate
     * points since the last down or move event.
     * Used to drag the [polygon] vertex to the new position (identified by the [x]
     * and [y] components) where the user touched.
     */
    private fun handleActionMove(point: Point) {
        draggable?.let {
            when (it) {
                is Draggable.Polygon -> {
                    val xDiff = point.x - it.draggingPoint.x
                    val yDiff = point.y - it.draggingPoint.y
                    capPolygonToContainerSize(xDiff, yDiff)
                    it.draggingPoint = point
                }
                is Draggable.Vertex -> {
                    polygon.vertexes[it.index].update(point)
                    capVertexToContainerSize(point, it.index)
                }
            }
        }
    }

    /**
     * A pressed gesture has finished, the motion contains the final release location
     * as well as any intermediate points since the last down or move event.
     * Used to detect the end of dragging event of the [polygon] vertex.
     */
    private fun handleActionUp() {
        draggable = null
    }

    /**
     * To indicate if the current touched point is actually a marker point
     * and that it's eligible for move (dragging) event.
     *
     * @param markerPoint the point where the maker is currently positioned.
     * @param touchPoint the point where the user is currently touching.
     * @return **True** if the touch event is inside the [markerPoint], and **False** otherwise.
     */
    private fun isTouchInsideTheVertex(markerPoint: Point, touchPoint: Point): Boolean {
        val markerCenter = getVertexCenter(markerPoint)
        val xDistance = touchPoint.x - markerCenter.x
        val yDistance = touchPoint.y - markerCenter.y
        val touchRadius = sqrt(xDistance.pow(2.0) + yDistance.pow(2.0))
        return touchRadius < vertexTouchRadius
    }

    /**
     * Get the [polygon] vertex center based on the [FloorPlanner.DEFAULT_MARKER_RADIUS].
     *
     * @param point the vertex coordinates.
     * @return the center point of the vertex.
     */
    private fun getVertexCenter(point: Point): Point {
        return Point(
            point.x + (floorPlanner.markerRadius),
            point.y + (floorPlanner.markerRadius),
        )
    }

    /**
     * Move the polygon and cap it's vertexes to the borders and prevent it from exceeding it.
     * If the [boxPadding] is greater than 0, it will be taken into consideration.
     *
     * @param xDiff the distance that the polygon will move in the x coordinate.
     * @param yDiff the distance that the polygon will move in the y coordinate.
     */
    private fun capPolygonToContainerSize(xDiff: Double, yDiff: Double) {

        val originalMinPointX = polygon.vertexes.minOf { it.x }
        val originalMaxPointX = polygon.vertexes.maxOf { it.x }
        val originalMinPointY = polygon.vertexes.minOf { it.y }
        val originalMaxPointY = polygon.vertexes.maxOf { it.y }

        val destinationMinPointX = polygon.vertexes.minOf { it.x + xDiff }
        val destinationMaxPointX = polygon.vertexes.maxOf { it.x + xDiff }
        val destinationMinPointY = polygon.vertexes.minOf { it.y + yDiff }
        val destinationMaxPointY = polygon.vertexes.maxOf { it.y + yDiff }

        /*
        Find the final distance that the polygon will move in the x axis taking into
        account the [boxPadding] to prevent the polygon from exceeding the padding.
         */
        val xFinalDiff = when {
            destinationMinPointX < xMin -> xMin - originalMinPointX
            destinationMaxPointX > xMax -> xMax - originalMaxPointX
            else -> xDiff
        }

        /*
        Find the final distance that the polygon will move in the y axis taking into
        account the [boxPadding] to prevent the polygon from exceeding the padding.
         */
        val yFinalDiff = when {
            destinationMinPointY < yMin -> yMin - originalMinPointY
            destinationMaxPointY > yMax -> yMax - originalMaxPointY
            else -> yDiff
        }

        // Move all polygon vertexes to the new [xFinalDiff] and [yFinalDiff].
        polygon.vertexes.forEach {
            it.x = it.x + xFinalDiff
            it.y = it.y + yFinalDiff
        }
    }

    /**
     * Keep the markers inside the container constraints to prevent
     * the user from dragging the polygon vertexes outside the
     * box boundaries.
     */
    private fun capVertexToContainerSize(point: Point, index: Int) {
        if (point.x < xMin) {
            polygon.vertexes[index].x = xMin
        } else if (point.x > xMax) {
            polygon.vertexes[index].x = xMax
        }
        if (point.y < yMin) {
            polygon.vertexes[index].y = yMin
        } else if (point.y > yMax) {
            polygon.vertexes[index].y = yMax
        }
    }
}


private sealed class Draggable {
    data class Vertex(val index: Int) : Draggable()
    data class Polygon(var draggingPoint: Point) : Draggable()
}