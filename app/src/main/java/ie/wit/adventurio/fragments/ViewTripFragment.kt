package ie.wit.adventurio.fragments

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*

import ie.wit.adventurio.R
import ie.wit.adventurio.activities.ViewTrip
import ie.wit.adventurio.main.MainApp
import ie.wit.adventurio.models.Account
import ie.wit.adventurio.models.WalkingTrip
import kotlinx.android.synthetic.main.fragment_view_trip.*
import kotlinx.android.synthetic.main.fragment_view_trip.view.*


class ViewTripFragment : Fragment(),GoogleMap.OnMarkerClickListener  {

    private lateinit var map: GoogleMap
    lateinit var app: MainApp
    lateinit var root : View
    var trip = WalkingTrip()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_view_trip, container, false)
        activity?.title = getString(R.string.viewAdv)

        val bundle = arguments
        if (bundle != null) {
            trip = bundle.getParcelable("trip_key")
        }

        root.mapView2.onCreate(savedInstanceState)
        root.mapView2.getMapAsync {
            map = it
            configureMap()
        }
        return root
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
        root.txtTripCat.text = trip.tripType
        root.txtTripSteps.text = trip.tripSteps.toString()
        root.txtTripDist.text = trip.tripDistance.toString() +"km"
        root.txtTripTime.text = trip.tripLength
        root.txtTripTimeStart.text = trip.tripStartTime
        root.txtTripTimeEnd.text = trip.tripEndTime

    }

    override fun onMarkerClick(marker: Marker): Boolean {
        return false
    }

    override fun onDestroy() {

        super.onDestroy()
        root.mapView2.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        root.mapView2.onLowMemory()
    }

    override fun onPause() {
        super.onPause()
        root.mapView2.onPause()
    }

    override fun onResume() {
        super.onResume()
        root.mapView2.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        root.mapView2.onSaveInstanceState(outState)
    }

    companion object {

        @JvmStatic
        fun newInstance(trip: WalkingTrip) =
            ViewTripFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("trip_key", trip)
                }
            }
    }
}
