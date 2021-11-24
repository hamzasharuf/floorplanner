package com.hamzasharuf.floor_planner.model

import kotlin.math.abs
import kotlin.math.hypot

/**
 * Point on 2D landscape
 *
 * @author Hamza Sharaf <hamzasharuf20@gmail.com>
 */
class Point(var x: Double, var y: Double) {

    constructor(x: Int, y: Int) : this(x.toDouble(), y.toDouble())

    constructor(x: Float, y: Float) : this(x.toDouble(), y.toDouble())

    fun distanceTo(point: Point): Double {
        val ac = abs(point.y - y)
        val cb = abs(point.x - x)
        return hypot(ac, cb)
    }

    fun update(point: Point){
        x = point.x
        y = point.y
    }

    fun update(newX: Double, newY: Double){
        x = newX
        y = newY
    }

    override fun toString(): String {
        return String.format("(%f,%f)", x, y)
    }
}