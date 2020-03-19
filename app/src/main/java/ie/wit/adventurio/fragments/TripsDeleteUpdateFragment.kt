package ie.wit.adventurio.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import ie.wit.adventurio.R
import ie.wit.adventurio.main.MainApp
import ie.wit.adventurio.models.Account
import ie.wit.adventurio.models.WalkingTrip
import ie.wit.fragments.TripsListFragment
import kotlinx.android.synthetic.main.fragment_trips_delete_update.view.*


class TripsDeleteUpdateFragment : Fragment() {

    var trip = WalkingTrip()
    var user = Account()
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
        //val AccountList = app.users.getAllAccounts() as ArrayList<Account>

        //for (account in AccountList){
        //    if(account.id == trip.tripOwner){
         //       user = account
        //   }
        //}

        root.amountPickerHours1.minValue = 0
        root.amountPickerHours1.maxValue = 23
        root.amountPickerHours1.setFormatter(NumberPicker.Formatter { i -> String.format("%02d", i) })
        root.amountPickerMinutes1.minValue = 0
        root.amountPickerMinutes1.maxValue = 59
        root.amountPickerMinutes1.setFormatter(NumberPicker.Formatter { i -> String.format("%02d", i) })


        root.amountPickerHours2.minValue = 0
        root.amountPickerHours2.maxValue = 23
        root.amountPickerHours2.setFormatter(NumberPicker.Formatter { i -> String.format("%02d", i) })
        root.amountPickerMinutes2.minValue = 0
        root.amountPickerMinutes2.maxValue = 59
        root.amountPickerMinutes2.setFormatter(NumberPicker.Formatter { i -> String.format("%02d", i) })


        var num = trip.tripDistance
        root.editDistance.setText("%.2f".format(num))
        root.editSteps.setText(trip.tripSteps.toString())
        root.amountPickerHours1.value = trip.tripStartTime.substring(0,2).toInt()
        root.amountPickerMinutes1.value = trip.tripStartTime.substring(3,5).toInt()


        root.amountPickerHours2.value = trip.tripEndTime.substring(0,2).toInt()
        root.amountPickerMinutes2.value = trip.tripEndTime.substring(3,5).toInt()

        //del button
        root.deleteTripFab.setOnClickListener{
            //app.trips.delete(trip)
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

            if(root.amountPickerHours2.value < root.amountPickerHours1.value){
                val toast =
                    Toast.makeText(
                        activity!!.applicationContext,
                        "Time Ended is less than Time Initiated",
                        Toast.LENGTH_LONG
                    )
                toast.show()
            } else if((root.amountPickerHours2.value == root.amountPickerHours1.value) && (root.amountPickerMinutes2.value < root.amountPickerMinutes1.value)) {
                val toast =
                    Toast.makeText(
                        activity!!.applicationContext,
                        "Time Ended is less than Time Initiated",
                        Toast.LENGTH_LONG
                    )
                toast.show()
            } else {
                trip.tripDistance = (root.editDistance.text.toString()).toDouble()
                trip.tripSteps = (root.editSteps.text.toString()).toInt()
                trip.tripLength = ""

                if ((root.amountPickerHours2.value-root.amountPickerHours1.value) < 10){
                    trip.tripLength += "0"+(root.amountPickerHours2.value - root.amountPickerHours1.value).toString() + ":"
                }else{
                    trip.tripLength += (root.amountPickerHours2.value - root.amountPickerHours1.value).toString() + ":"
                }

                if(root.amountPickerMinutes2.value < root.amountPickerMinutes1.value){
                    if ((root.amountPickerMinutes1.value - root.amountPickerMinutes2.value) < 10){

                        trip.tripLength = ((root.amountPickerHours2.value - root.amountPickerHours1.value) - 1).toString() + ":"
                        trip.tripLength += (0 + (root.amountPickerMinutes1.value - root.amountPickerMinutes2.value)).toString() + ":00"



                    }else{
                        trip.tripLength = ((root.amountPickerHours2.value - root.amountPickerHours1.value) - 1).toString()+ ":"
                        trip.tripLength += "0"+(60-(root.amountPickerMinutes1.value - root.amountPickerMinutes2.value)).toString() + ":00"
                    }
                }else{
                    if ((root.amountPickerMinutes2.value - root.amountPickerMinutes1.value) < 10){
                        trip.tripLength += "0"+(root.amountPickerMinutes2.value - root.amountPickerMinutes1.value).toString() + ":00"
                    }else{
                        trip.tripLength += (root.amountPickerMinutes2.value - root.amountPickerMinutes1.value).toString() + ":00"
                    }
                }


                if ( root.amountPickerMinutes1.value < 10){
                    trip.tripStartTime = root.amountPickerHours1.value.toString() + ":0" + root.amountPickerMinutes1.value.toString()
                }else{
                    trip.tripStartTime = root.amountPickerHours1.value.toString() + ":" + root.amountPickerMinutes1.value.toString()
                }
                if(root.amountPickerMinutes2.value < 10){
                    trip.tripEndTime = root.amountPickerHours2.value.toString() + ":0" + root.amountPickerMinutes2.value.toString()
                }else{
                    trip.tripEndTime = root.amountPickerHours2.value.toString() + ":" + root.amountPickerMinutes2.value.toString()
                }



                //app.trips.update(trip.copy())
                val toast =
                    Toast.makeText(
                        activity!!.applicationContext,
                        "Adventure Updated!",
                        Toast.LENGTH_LONG
                    )
                toast.show()
                navigateTo(TripsListFragment.newInstance(user))
            }
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
