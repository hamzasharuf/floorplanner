package com.hamzasharuf.floorplanner.model

import java.util.*
import kotlin.math.max
import kotlin.math.min

/**
 * The 2D polygon.
 *
 * @author Hamza Sharaf <hamzasharuf20@gmail.com>
 */
class Polygon private constructor(
    val sides: List<Line>,
    val vertexes: List<Point>,
    private val _boundingBox: BoundingBox?,
) {

    /**
     * Builder of the polygon
     */
    class Builder {
        private var _vertexes: MutableList<Point> = ArrayList()
        private val _sides: MutableList<Line> = ArrayList()
        private var _boundingBox: BoundingBox? = null
        private var _firstPoint = true
        private var _isClosed = false

        /**
         * Add vertex points of the polygon.<br></br>
         * It is very important to add the vertexes by order, like you were drawing them one by one.
         *
         * @param point The vertex point
         * @return The builder
         */
        fun addVertex(point: Point): Builder {
            if (_isClosed) {
                // each hole we start with the new array of vertex points
                _vertexes = ArrayList()
                _isClosed = false
            }
            updateBoundingBox(point)
            _vertexes.add(point)

            // add line (edge) to the polygon
            if (_vertexes.size > 1) {
                val Line = Line(_vertexes[_vertexes.size - 2], point)
                _sides.add(Line)
            }
            return this
        }

        /**
         * Close the polygon shape. This will create a new side (edge) from the **last** vertex point to the **first** vertex point.
         *
         * @return The builder
         */
        fun close(): Builder {
            validate()

            // add last Line
            _sides.add(Line(_vertexes[_vertexes.size - 1], _vertexes[0]))
            _isClosed = true
            return this
        }

        /**
         * Build the instance of the polygon shape.
         *
         * @return The polygon
         */
        fun build(): Polygon {
            validate()

            // in case you forgot to close
            if (!_isClosed) {
                // add last Line
                _sides.add(Line(_vertexes[_vertexes.size - 1], _vertexes[0]))
            }
            return Polygon(_sides, _vertexes, _boundingBox)
        }

        /**
         * Update bounding box with a new point.<br></br>
         *
         * @param point new point.
         */
        private fun updateBoundingBox(point: Point) {
            if (_firstPoint) {
                _boundingBox = BoundingBox()
                _boundingBox!!.xMax = point.x
                _boundingBox!!.xMin = point.x
                _boundingBox!!.yMax = point.y
                _boundingBox!!.yMin = point.y
                _firstPoint = false
            } else {
                // set bounding box
                if (point.x > _boundingBox!!.xMax) {
                    _boundingBox!!.xMax = point.x
                } else if (point.x < _boundingBox!!.xMin) {
                    _boundingBox!!.xMin = point.x
                }
                if (point.y > _boundingBox!!.yMax) {
                    _boundingBox!!.yMax = point.y
                } else if (point.y < _boundingBox!!.yMin) {
                    _boundingBox!!.yMin = point.y
                }
            }
        }

        private fun validate() {
            if (_vertexes.size < 3) {
                throw RuntimeException("Polygon must have at least 3 points")
            }
        }
    }

    /**
     * By given ray and one side of the polygon, check if both lines intersect.
     *
     * @return `True` if both lines intersect, otherwise return `False`
     */
    private fun intersect(ray: Line, side: Line): Boolean {

        // if both vectors aren't from the kind of x=1 lines then go into
        val intersectPoint: Point = if (!ray.isVertical && !side.isVertical) {
            // check if both vectors are parallel. If they are parallel then no intersection point will exist
            if (ray.a - side.a == 0.0) {
                return false
            }
            val x = (side.b - ray.b) / (ray.a - side.a) // x = (b2-b1)/(a1-a2)
            val y = side.a * x + side.b // y = a2*x+b2
            Point(x, y)
        } else if (ray.isVertical && !side.isVertical) {
            val x = ray.start.x
            val y = side.a * x + side.b
            Point(x, y)
        } else if (!ray.isVertical && side.isVertical) {
            val x = side.start.x
            val y = ray.a * x + ray.b
            Point(x, y)
        } else {
            return false
        }

        // System.out.println("Ray: " + ray.toString() + " ,Side: " + side);
        // System.out.println("Intersect point: " + intersectPoint.toString());
        return side.isInside(intersectPoint) && ray.isInside(intersectPoint)
    }

    /**
     * Create a ray. The ray will be created by given point and on point outside the polygon.<br></br>
     * The outside point is calculated automatically.
     */
    private fun createRay(point: Point): Line {
        // create outside point
        val epsilon = (_boundingBox!!.xMax - _boundingBox.xMin) / 10e6
        val outsidePoint = Point(_boundingBox.xMin - epsilon, _boundingBox.yMin)
        return Line(outsidePoint, point)
    }

    /**
     * Check if the given point is in bounding box
     *
     * @return `True` if the point in bounding box, otherwise return `False`
     */
    private fun inBoundingBox(point: Point): Boolean {
        return point.x >= _boundingBox!!.xMin && point.x <= _boundingBox.xMax && point.y >= _boundingBox.yMin && point.y <= _boundingBox.yMax
    }

    private class BoundingBox {
        var xMax = Double.POSITIVE_INFINITY
        var xMin = Double.NEGATIVE_INFINITY
        var yMax = Double.POSITIVE_INFINITY
        var yMin = Double.NEGATIVE_INFINITY
    }

    /**
     * Define Infinite (Using INT_MAX caused overflow problems)
     */
    private var infinity: Double = 10000.0

    /**
     * Given three collinear points p, q, r, the function checks if point q lies
     * on line segment 'pr'
     *
     * @return true if the point lies on the 'pr' segment, and false otherwise.
     */
    private fun onSegment(p: Point, q: Point, r: Point): Boolean {
        return q.x <= p.x.coerceAtLeast(r.x) && q.x >= min(p.x, r.x) && q.y <= max(p.y, r.y) && q.y >= min(p.y, r.y)
    }

    /**
     * To find orientation of ordered triplet (p, q, r).
     * The function returns following values
     * 0 --> p, q and r are collinear
     * 1 --> Clockwise
     * 2 --> Counterclockwise
     */
    private fun orientation(p: Point, q: Point, r: Point): Int {
        val `val` = ((q.y - p.y) * (r.x - q.x)
                - (q.x - p.x) * (r.y - q.y)).toInt()
        if (`val` == 0) {
            return 0 // collinear
        }
        return if (`val` > 0) 1 else 2 // clock or counterclock wise
    }

    /**
     * The function that returns true if line segment 'p1q1' and 'p2q2' intersect,
     * and false otherwise.
     *
     * @param p1 the p1 point.
     * @param q1 the q1 point.
     * @param p2 the p2 point.
     * @param q2 the q2 point.
     * @return true if the 2 line segments intersect and false otherwise.
     */
    private fun doIntersect(
        p1: Point, q1: Point,
        p2: Point, q2: Point
    ): Boolean {
        // Find the four orientations needed for
        // general and special cases
        val o1 = orientation(p1, q1, p2)
        val o2 = orientation(p1, q1, q2)
        val o3 = orientation(p2, q2, p1)
        val o4 = orientation(p2, q2, q1)

        // General case
        if (o1 != o2 && o3 != o4) {
            return true
        }

        // Special Cases
        // p1, q1 and p2 are collinear and
        // p2 lies on segment p1q1
        if (o1 == 0 && onSegment(p1, p2, q1)) {
            return true
        }

        // p1, q1 and p2 are collinear and
        // q2 lies on segment p1q1
        if (o2 == 0 && onSegment(p1, q2, q1)) {
            return true
        }

        // p2, q2 and p1 are collinear and
        // p1 lies on segment p2q2
        if (o3 == 0 && onSegment(p2, p1, q2)) {
            return true
        }

        // p2, q2 and q1 are collinear and
        // q1 lies on segment p2q2
        return o4 == 0 && onSegment(p2, q1, q2)

        // Doesn't fall in any of the above cases
    }

    /**
     * check if a specific [Point] lies inside this polygon.
     *
     * @return true if the [point] lies inside the polygon and false otherwise.
     */
    fun isInside(point: Point): Boolean {
        // number of sides of the polygon
        val n = sides.size

        // There must be at least 3 vertices in polygon[]
        if (n < 3) {
            return false
        }

        // Create a point for line segment from p to infinite
        val extreme = Point(infinity, point.y)

        // Count intersections of the above line
        // with sides of polygon
        var count = 0
        var i = 0
        do {
            val next = (i + 1) % n

            // Check if the line segment from 'p' to
            // 'extreme' intersects with the line
            // segment from 'polygon[i]' to 'polygon[next]'
            if (doIntersect(vertexes[i], vertexes[next], point, extreme)) {
                // If the point 'p' is collinear with line
                // segment 'i-next', then check if it lies
                // on segment. If it lies, return true, otherwise false
                if (orientation(vertexes[i], point, vertexes[next]) == 0) {
                    return onSegment(
                        vertexes[i], point,
                        vertexes[next]
                    )
                }
                count++
            }
            i = next
        } while (i != 0)

        // Return true if count is odd, false otherwise
        return count % 2 == 1 // Same as (count%2 == 1)
    }
}