package com.hamzasharuf.floor_planner

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import com.hamzasharuf.floor_planner.model.Point
import com.hamzasharuf.floor_planner.model.Polygon

/**
 * This class represents a virtual floor planner that can be used as a view
 * component in android.
 *
 * @author Hamza Sharaf <hamzasharuf20@gmail.com>
 * @constructor Creates a view with given attributes and a [FloorPlanner] object.
 */
class FloorPlannerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    /**
     * This interface represents the callback to be invoked when the
     * user interacts with the floor planner.
     */
    interface OnCoordinatesUpdatedListener {

        /**
         * Callback function when the user interacts with the polygon vertexes.
         *
         * @param polygon the updated polygon instance
         */
        fun onCoordinatesUpdated(polygon: Polygon)
    }

    /**
     * The floor planner objects which holds the main features
     * and functions of the floor planner.
     */
    private val floorPlanner = FloorPlanner(width, height)

    /**
     * The graphic component of the [FloorPlanner] which is responsible of
     * drawing and updating the polygon on the [FloorPlannerView].
     */
    private val graphics = FloorPlannerGraphics(floorPlanner)

    /**
     * The control component of the [FloorPlanner] which is responsible of
     * controlling the user interactions and motion gestures of the [FloorPlannerView].
     */
    private val controls = FloorPlannerControls(floorPlanner)

    /**
     * Listener object for processing user interaction with the vertexes.
     */
    var onCoordinatesUpdatedListener: OnCoordinatesUpdatedListener? = null

    /**
     * Sets up the components and floor planner with the supplied or default attributes.
     */
    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.FloorPlannerView,
            0, 0
        ).apply {
            try {
                setMarkerRadius(getInt(R.styleable.FloorPlannerView_markerRadius, FloorPlanner.DEFAULT_MARKER_RADIUS))
                setPolygonStrokeWidth(
                    getFloat(
                        R.styleable.FloorPlannerView_polygonStrokeWidth,
                        FloorPlanner.DEFAULT_STROKE_WIDTH
                    )
                )
                setPolygonWidthRatio(
                    getFloat(
                        R.styleable.FloorPlannerView_polygonWidthRatio,
                        FloorPlanner.INITIAL_POLYGON_WIDTH_RATIO
                    )
                )
                setPolygonHeightRatio(
                    getFloat(
                        R.styleable.FloorPlannerView_polygonHeightRatio,
                        FloorPlanner.INITIAL_POLYGON_HEIGHT_RATIO
                    )
                )
                setBoxPadding(getFloat(R.styleable.FloorPlannerView_boxPadding, FloorPlannerControls.DEFAULT_BOX_PADDING))
                setExtendedTouchRadius(
                    getInt(
                        R.styleable.FloorPlannerView_extendedTouchRadius,
                        FloorPlannerControls.DEFAULT_EXTENDED_VERTEX_TOUCH_RADIUS
                    )
                )

                setMarkerColor(getColor(R.styleable.FloorPlannerView_markerColor, FloorPlannerGraphics.DEFAULT_MARKER_COLOR))
                setStrokeColor(getColor(R.styleable.FloorPlannerView_strokeColor, FloorPlannerGraphics.DEFAULT_STROKE_COLOR))
                setFillColor(getColor(R.styleable.FloorPlannerView_fillColor, FloorPlannerGraphics.DEFAULT_FILL_COLOR))

            } finally {
                recycle()
            }
        }
    }

    /**
     * Draws the floor plan's polygon and vertexes at their specified
     * coordinates.
     *
     * @param canvas Canvas on which to draw the polygon and vertexes.
     */
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let { graphics.draw(it) }
    }


    /**
     * Resizes and repositions the floor planner according to the new width and height.
     * This method is called when the size of the view has changed.
     *
     * @param w Current width of the view
     * @param h Current height of the view
     * @param oldW Old width of the view
     * @param oldH Old height of the view
     */
    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)
        floorPlanner.width = w
        floorPlanner.height = h
    }

    /**
     * Handles touch actions to update the position of the polygon accordingly.
     *
     * @param event The motion event that was fired.
     * @return True if the event was handled, false otherwise.
     */
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        super.onTouchEvent(event)
        event?.let { controls.handleEvent(it) }
        invalidate()
        onCoordinatesUpdatedListener?.onCoordinatesUpdated(floorPlanner.polygon)
        return true
    }

    /**
     * The current polygon object which include information about the
     * polygon, its sides, and the vertexes with their coordinates.
     */
    val polygon: Polygon
        get() = floorPlanner.polygon

    /**
     * A direct accessor for the vertexes coordinates on the polygon.
     * Note that these coordinates are related to the position of the coordinates
     * with respect to the [FloorPlannerView] and not for the entire screen.
     */
    val vertexes: List<Point>
        get() = polygon.vertexes

    /**
     * The color of the polygon fill.
     */
    fun setFillColor(color: Int) {
        graphics.fillColor = color
    }

    /**
     * The color of the outer borders of the polygon.
     */
    fun setStrokeColor(color: Int) {
        graphics.strokeColor = color
    }

    /**
     * The color of the marker which is used to describe
     * a polygon vertex on the [FloorPlannerView].
     */
    fun setMarkerColor(color: Int) {
        graphics.markerColor = color
    }

    /**
     * The radius of the marker which is used to describe
     * a polygon vertex on the [FloorPlannerView].
     */
    fun setMarkerRadius(radius: Int) {
        floorPlanner.markerRadius = radius
    }

    /**
     * The width of the outer borders of the polygon.
     */
    fun setPolygonStrokeWidth(width: Float) {
        floorPlanner.strokeWidth = width
    }

    /**
     * Describes the width ratio the polygon will take out of the
     * full [FloorPlanner] width once it's drawn for the first time.
     * should be between 0.6 and 1.
     */
    fun setPolygonWidthRatio(ratio: Float) {
        floorPlanner.polygonWidthRatio =
            when {
                ratio < 0.6 -> 0.6f
                ratio > 1 -> 1f
                else -> ratio
            }
    }

    /**
     * Describes the height ratio the polygon will take out of the
     * full [FloorPlanner] height once it's drawn for the first time.
     * should be between 0.5 and 1.
     */
    fun setPolygonHeightRatio(ratio: Float) {
        floorPlanner.polygonHeightRatio =
            when {
                ratio < 0.6 -> 0.6f
                ratio > 1 -> 1f
                else -> ratio
            }
    }

    /**
     * Adds additional imaginary radius to the vertex to make the touch event on the radius more
     * easy for the user. Default value is 30.
     *
     * @param radius the new extended radius value.
     */
    fun setExtendedTouchRadius(radius: Int) {
        controls.extendedVertexTouchRadius = radius
    }

    /**
     * Add padding to the surrounding box to prevent the polygon from exceeding this padding
     * and to have a sufficient space between the box borders and the polygon.
     * Default value is 50.
     * Note: Don't set the padding to more than the [FloorPlannerView] <width / 2> or <height / 2>
     * as it will lead to unexpected behaviour.
     *
     * @param padding the padding value.
     */
    fun setBoxPadding(padding: Float) {
        if (padding < 0) {
            controls.boxPadding = 0f
        } else {
            controls.boxPadding = padding
        }
    }

}