package com.hamzasharuf.floor_planner

import com.hamzasharuf.floor_planner.model.Point
import com.hamzasharuf.floor_planner.model.Polygon


/**
 * Floor planner is used to draw a polygon over an image and gives
 * you the ability to drag the polygon vertexes based on your preferences.
 *
 * @author Hamza Sharaf <hamzasharuf20@gmail.com>
 * @param width the width of [FloorPlannerView].
 * @param height the height of [FloorPlannerView].
 */
internal class FloorPlanner(
    internal var width: Int,
    internal var height: Int,
) {

    companion object {
        /**
         * The default radius of the marker which is used to describe
         * a polygon vertex on the [FloorPlannerView].
         */
        internal const val DEFAULT_MARKER_RADIUS = 8

        /**
         * The default width of the [polygon] stroke.
         */
        internal const val DEFAULT_STROKE_WIDTH = 7f

        /**
         * Describes the width ratio the polygon will take out of the
         * full [FloorPlanner] width once it's drawn for the first time.
         */
        internal const val INITIAL_POLYGON_WIDTH_RATIO = 0.75f

        /**
         * Describes the height ratio the polygon will take out of the
         * full [FloorPlanner] height once it's drawn for the first time.
         */
        internal const val INITIAL_POLYGON_HEIGHT_RATIO = 0.75f
    }

    /**
     * The polygon object which describes the visible view using
     * its embedded vertexes.
     * the initial value of the polygon is defined by [getInitialPolygon].
     */
    internal val polygon: Polygon by lazy { getInitialPolygon() }

    /**
     * The default radius of the marker which is used to describe
     * a polygon vertex on the [FloorPlannerView].
     */
    var markerRadius = DEFAULT_MARKER_RADIUS

    /**
     * The default width of the [polygon] stroke.
     */
    var strokeWidth = DEFAULT_STROKE_WIDTH

    /**
     * Describes the width ratio the polygon will take out of the
     * full [FloorPlanner] width once it's drawn for the first time.
     * should be less than 1.
     */
    var polygonWidthRatio = INITIAL_POLYGON_WIDTH_RATIO

    /**
     * Describes the height ratio the polygon will take out of the
     * full [FloorPlanner] height once it's drawn for the first time.
     * should be less than 1.
     */
    var polygonHeightRatio = INITIAL_POLYGON_HEIGHT_RATIO

    /**
     * Draws the initial polygon based on the given view size.
     */
    private fun getInitialPolygon(): Polygon {
        return Polygon.Builder()
            .addVertex(
                Point(
                    width * (1 - polygonWidthRatio),
                    height * (1 - polygonHeightRatio)
                )
            )
            .addVertex(
                Point(
                    width * polygonWidthRatio,
                    height * (1 - polygonHeightRatio)
                )
            )
            .addVertex(
                Point(
                    width * polygonWidthRatio,
                    height * polygonHeightRatio
                )
            )
            .addVertex(
                Point(
                    width * (1 - polygonWidthRatio),
                    height * polygonHeightRatio
                )
            )
            .close()
            .build()
    }

}