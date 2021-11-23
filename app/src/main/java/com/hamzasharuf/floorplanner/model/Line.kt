package com.hamzasharuf.floorplanner.model

import kotlin.math.max
import kotlin.math.min

/**
 * Line is defined by starting point and ending point on 2D dimension.<br></br>
 *
 * @author Hamza Sharaf <hamzasharuf20@gmail.com>
 * @param start start point.
 * @param end end point.
 */
class Line(
    val start: Point,
    val end: Point
) {
    /**
     * y = **A**x + B
     *
     * @return The **A**
     */
    var a = Double.NaN

    /**
     * y = Ax + **B**
     *
     * @return The **B**
     */
    var b = Double.NaN

    /**
     * Indicate whereas the line is vertical. <br></br>
     * For example, line like x=1 is vertical, in other words parallel to axis Y. <br></br>
     * In this case the A is (+/-)infinite.
     *
     * @return `True` if the line is vertical, otherwise return `False`
     */
    var isVertical = false

    /**
     * Indicate whereas the point lays on the line.
     *
     * @param point - The point to check
     * @return `True` if the point lays on the line, otherwise return `False`
     */
    fun isInside(point: Point): Boolean {
        val maxX = max(start.x, end.x)
        val minX = min(start.x, end.x)
        val maxY = max(start.y, end.y)
        val minY = min(start.y, end.y)
        return point.x in minX..maxX && point.y >= minY && point.y <= maxY
    }

    override fun toString(): String {
        return String.format("%s-%s", start.toString(), end.toString())
    }

    init {
        if (end.x - start.x != 0.0) {
            a = (end.y - start.y) / (end.x - start.x)
            b = start.y - a * start.x
        } else {
            isVertical = true
        }
    }
}