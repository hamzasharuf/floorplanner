package com.hamzasharuf.floorplanner

import com.hamzasharuf.floorplanner.model.Point
import com.hamzasharuf.floorplanner.model.Polygon


/**
 * Floor planner is used to draw a polygon over an image and gives
 * you the ability to drag the polygon vertexes based on your preferences.
 *
 * @author Hamza Sharaf <hamzasharuf20@gmail.com>
 * @param width the width of [FloorPlannerView].
 * @param height the height of [FloorPlannerView].
 */
class FloorPlanner(
    var width: Int,
    var height: Int,
) {

    companion object {
        /**
         * The default radius of the marker which is used to describe
         * a polygon vertex on the [FloorPlannerView].
         */
        const val DEFAULT_MARKER_RADIUS = 8

        /**
         * The default width of the [polygon] stroke.
         */
        const val DEFAULT_STROKE_WIDTH = 7f

        /**
         * Describes the width ratio the polygon will take out of the
         * full [FloorPlanner] width once it's drawn for the first time.
         */
        private const val INITIAL_POLYGON_WIDTH_RATIO = 0.75

        /**
         * Describes the height ratio the polygon will take out of the
         * full [FloorPlanner] height once it's drawn for the first time.
         */
        private const val INITIAL_POLYGON_HEIGHT_RATIO = 0.75
    }

    /**
     * The polygon object which describes the visible view using
     * its embedded vertexes.
     * the initial value of the polygon is defined by [getInitialPolygon].
     */
    val polygon: Polygon by lazy { getInitialPolygon() }

    /**
     * Draws the initial polygon based on the given view size.
     */
    private fun getInitialPolygon(): Polygon {
        return Polygon.Builder()
            .addVertex(
                Point(
                    width * (1 - INITIAL_POLYGON_WIDTH_RATIO),
                    height * (1 - INITIAL_POLYGON_HEIGHT_RATIO)
                )
            )
            .addVertex(
                Point(
                    width * INITIAL_POLYGON_WIDTH_RATIO,
                    height * (1 - INITIAL_POLYGON_HEIGHT_RATIO)
                )
            )
            .addVertex(
                Point(
                    width * INITIAL_POLYGON_WIDTH_RATIO,
                    height * INITIAL_POLYGON_HEIGHT_RATIO
                )
            )
            .addVertex(
                Point(
                    width * (1 - INITIAL_POLYGON_WIDTH_RATIO),
                    height * INITIAL_POLYGON_HEIGHT_RATIO
                )
            )
            .close()
            .build()
    }

}