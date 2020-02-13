package ie.wit.adventurio.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager

import ie.wit.adventurio.R
import ie.wit.adventurio.main.MainApp
import ie.wit.adventurio.models.WalkingTrip
import ie.wit.fragments.TripsListFragment
import kotlinx.android.synthetic.main.fragment_trips_delete_update.view.*



class TripsDeleteUpdateFragment : Fragment() {

    var trip = WalkingTrip()
    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_trips_delete_update, container, false)
        activity?.title = getString(R.string.menu_edit)

        val bundle = arguments
        if (bundle != null) {
            trip = bundle.getParcelable("trip_key")
        }

        root.amountPickerHours.minValue = 0
        root.amountPickerHours.maxValue = 24
        root.amountPickerMinutes.minValue = 0
        root.amountPickerMinutes.maxValue = 59
        root.amountPickerSeconds.minValue = 0
        root.amountPickerSeconds.maxValue = 59

        var num = trip.tripDistance
        root.editDistance.setText("%.2f".format(num))
        root.editSteps.setText(trip.tripSteps.toString())
        root.amountPickerHours.value = trip.tripLength.substring(0,2).toInt()
        root.amountPickerMinutes.value = trip.tripLength.substring(3,5).toInt()
        root.amountPickerSeconds.value = trip.tripLength.substring(6,8).toInt()

        //del button
        root.deleteTripFab.setOnClickListener{
            app.trips.delete(trip)
            val toast =
                Toast.makeText(
                    activity!!.applicationContext,
                    "Adventure Removed!",
                    Toast.LENGTH_LONG
                )
            toast.show()
            navigateTo(StatisticsFragment())
        }

        //update button
        root.updateTripFab.setOnClickListener {
            trip.tripDistance = (root.editDistance.text.toString()).toDouble()
            trip.tripSteps = (root.editSteps.text.toString()).toInt()
            trip.tripLength = ""
            if(root.amountPickerHours.value < 10){
                trip.tripLength += "0" + root.amountPickerHours.value.toString() + ":"
            } else {
                trip.tripLength += root.amountPickerHours.value.toString() + ":"
            }
            if(root.amountPickerMinutes.value < 10){
                trip.tripLength += "0" + root.amountPickerMinutes.value.toString() + ":"
            } else {
                trip.tripLength += root.amountPickerMinutes.value.toString() + ":"
            }
            if(root.amountPickerSeconds.value < 10){
                trip.tripLength += "0" + root.amountPickerSeconds.value.toString()
            } else {
                trip.tripLength += root.amountPickerSeconds.value.toString()
            }
            app.trips.update(trip.copy())
            val toast =
                Toast.makeText(
                    activity!!.applicationContext,
                    "Adventure Updated!",
                    Toast.LENGTH_LONG
                )
            toast.show()
            navigateTo(TripsListFragment())
        }


        return root
    }

    companion object {
        @JvmStatic
        fun newInstance(trip: WalkingTrip) =
            TripsDeleteUpdateFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("trip_key", trip)
                }
            }
    }

    private fun navigateTo(fragment: Fragment) {
        val fragmentManager: FragmentManager = activity!!.supportFragmentManager
        fragmentManager.beginTransaction()
            .replace(R.id.homeFrame, fragment)
            .addToBackStack(null)
            .commit()
    }
}
