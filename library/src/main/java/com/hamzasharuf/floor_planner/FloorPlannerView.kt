package com.hamzasharuf.floor_planner

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
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
     * user interacts with the joystick.
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
     * Adds additional imaginary radius to the vertex to make the touch event on the radius more
     * easy for the user. Default value is 30.
     *
     * @param radius the new extended radius value.
     */
    fun setExtendedTouchRadius(radius: Int){
        controls.extendedVertexTouchRadius = radius
    }

    /**
     * Add padding to the surrounding box to prevent the polygon from exceeding this padding
     * and to have a sufficient space between the box borders and the polygon.
     * Default value is 50.
     *
     * @param padding the padding value.
     */
    fun setPadding(padding: Double) {
        controls.boxPadding = padding
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
     * @param oldw Old width of the view
     * @param oldh Old height of the view
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
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

}