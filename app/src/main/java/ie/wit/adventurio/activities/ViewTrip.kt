package ie.wit.adventurio.activities

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import ie.wit.adventurio.R
import ie.wit.adventurio.main.MainApp
import ie.wit.adventurio.models.WalkingTrip
import kotlinx.android.synthetic.main.fragment_view_trip.*

class ViewTrip : AppCompatActivity(), GoogleMap.OnMarkerClickListener {

    private lateinit var map: GoogleMap
    lateinit var app: MainApp

    var trip = WalkingTrip()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_view_trip)

        if (intent.hasExtra("tripView")) {
            trip = intent.extras.getParcelable<WalkingTrip>("tripView")
        }
        mapView2.onCreate(savedInstanceState);
        mapView2.getMapAsync {
            map = it
            configureMap()
        }
    }

    fun configureMap() {
        map.setOnMarkerClickListener(this)
        var options = MarkerOptions()
        var i = 0
        var locationCollection = mutableListOf<LatLng>()
        for (coordinates in trip.lat)
        {
            var loc = LatLng(trip.lat[i].toDouble(), trip.lng[i].toDouble())
            if (i == 0 ){
                options = MarkerOptions()
                    .title("Start")
                    .snippet("GPS : " + loc.toString())
                    .draggable(false)
                    .position(loc)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))


            } else if (i == trip.lat.size - 1){
                options = MarkerOptions()
                    .title("Finish")
                    .snippet("GPS : " + loc.toString())
                    .draggable(false)
                    .position(loc)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            } else {
                options = MarkerOptions()
                    .title("Checkpoint ${i}")
                    .snippet("GPS : " + loc.toString())
                    .draggable(false)
                    .position(loc)
                    .visible(false)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))

            }

            map.addMarker(options)
            locationCollection.add(loc)

            i += 1
            if(i == trip.lat.size){
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, trip.zoom))
            }
        }
        map.addPolyline(
            PolylineOptions()
                .addAll(locationCollection)
                .width(5f)
                .color(Color.BLUE)

        )
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView2.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView2.onLowMemory()
    }

    override fun onPause() {
        super.onPause()
        mapView2.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapView2.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView2.onSaveInstanceState(outState)
    }


}
