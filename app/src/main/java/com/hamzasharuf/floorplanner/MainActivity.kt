package com.hamzasharuf.floorplanner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.hamzasharuf.floor_planner.FloorPlannerView
import com.hamzasharuf.floor_planner.model.Polygon

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fp = findViewById<FloorPlannerView>(R.id.floor_planner)
        fp.setExtendedTouchRadius(50)
        fp.setPadding(80.0)
        fp.onCoordinatesUpdatedListener = object : FloorPlannerView.OnCoordinatesUpdatedListener {
            override fun onCoordinatesUpdated(polygon: Polygon) {
                Log.d("MainActivityLogs", "New Vertexes Coordinates => ${polygon.vertexes}")
            }
        }
    }
}