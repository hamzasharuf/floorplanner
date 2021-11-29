# Floor Planner
[![Kotlin Version](https://img.shields.io/badge/kotlin-1.6.0-blue.svg)](https://kotlinlang.org) [![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=21) [![Ktlint](https://camo.githubusercontent.com/5652fd33142bf88d0f46018325126931fe65d01d/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f636f64652532307374796c652d2545322539442541342d4646343038312e737667)](https://github.com/hamzasharuf/floorplanner)

A lightweight library which used to add a polygon with 4 vetrexes over an image and allow the user to move either the whole polygon at once or the vertexes one by one.

## Demo

<img src="art/record-20211129-172637.gif" width=300 />

## Prerequisites

Add this in your root `build.gradle` file (**not** your module `build.gradle` file):

```gradle
allprojects {
	repositories {
		...
		maven { url 'https://www.jitpack.io' }
	}
}
```

## Dependency

Add this to your module's `build.gradle` file (make sure the version matches the JitPack badge above):

```gradle
dependencies {
	...
	implementation 'com.github.hamzasharuf:floorplanner:1.2'
}
```

## Usage

In `your_layout.xml` add the `FloorPlannerView`.

#### your_layout.xml

```xml
<com.hamzasharuf.floor_planner.FloorPlannerView
        android:id="@+id/floor_planner"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:scaleType="centerCrop"
        android:src="@drawable/conan"
        app:boxPadding="20"
        app:fillColor="#995FAC4A"
        app:extendedTouchRadius="30"
        app:markerColor="#ff0000"
        app:polygonHeightRatio="0.75"
        app:polygonStrokeWidth="10"
        app:polygonWidthRatio="0.75"
        app:markerRadius="15"
        app:strokeColor="@color/teal_700" />
```

You can customize the `FloorPlannerView` by changing its attributes either in the `xml` layout or in the code.

### xml properties

- `app:boxPadding="20"` Add padding to the surrounding box to prevent the polygon from exceeding this padding and to have a sufficient space between the box borders and the polygon.

- `app:markerRadius="15"` The radius of the marker which is used to describe a polygon vertex on the `FloorPlannerView`.

- `app:markerColor="#ff0000"` The color of the vertexes' markers.

- `app:fillColor="#995FAC4A"` The fill color of the polygon.

- `app:strokeColor="#ff018786"` The stroke color of the polygon which is used to describe the sides of the polygon.

- `app:polygonStrokeWidth="10"` The stroke width of the polygon which is used to describe the sides of the polygon.

- `app:polygonWidthRatio="0.75"` Describes the width ratio the polygon will take out of the full FloorPlanner width once it's drawn for the first time. **should be between 0.5 and 1**. 

- `app:polygonHeightRatio="0.75"` Describes the height ratio the polygon will take out of the full FloorPlanner height once it's drawn for the first time. **should be between 0.5 and 1**.

- `app:extendedTouchRadius="30"` Additional imaginary radius to the vertex to make the touch event on the vertex more easy for the user (To avoid forcing the user to touch the vertex itself).

### Programatically

```kotlin
val fp = findViewById<FloorPlannerView>(R.id.floor_planner)
fp.apply {
    setExtendedTouchRadius(50)
    setBoxPadding(30f)
    setMarkerRadius(14)
    setFillColor(Color.CYAN)
    setStrokeColor(Color.GREEN)
    setMarkerColor(Color.RED)
    setPolygonHeightRatio(0.75f)
    setPolygonWidthRatio(0.75f)
    setExtendedTouchRadius(30)
}
```

You can extend the imaginary

You can access the Polygon or the vertexes directly 
```kotlin
val fp = findViewById<FloorPlannerView>(R.id.floor_planner)
fp.polygon
fp.vertexes
```

### Listeners

You can add a listener to the polygon changes and have a callback whenever the user moves the polygon or any of its vertexes.

```kotlin
fp.onCoordinatesUpdatedListener = object : FloorPlannerView.OnCoordinatesUpdatedListener {
    override fun onCoordinatesUpdated(polygon: Polygon) {
        Log.d(TAG, "New Vertexes Coordinates => ${polygon.vertexes}")
    }
}
```

## CONTRIBUTING
### Would you like to contribute code?
1. [Fork floorplanner](https://github.com/hamzasharuf/floorplanner).
2. Create a new branch ([using GitHub](https://help.github.com/articles/creating-and-deleting-branches-within-your-repository/)) or the command `git checkout -b branch-name develop`).
3. [Start a pull request](https://github.com/hamzasharuf/floorplanner/compare). Reference [existing issues](https://github.com/hamzasharuf/floorplanner/issues) when possible.



