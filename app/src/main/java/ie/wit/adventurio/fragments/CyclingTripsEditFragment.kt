package ie.wit.adventurio.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ie.wit.adventurio.R
import ie.wit.adventurio.main.MainApp
import ie.wit.adventurio.models.Account
import ie.wit.adventurio.models.Trip
import ie.wit.fragments.TripsListFragment
import kotlinx.android.synthetic.main.fragment_cycling_trips_edit.view.*
import kotlinx.android.synthetic.main.fragment_manual_trip.view.*
import kotlinx.android.synthetic.main.fragment_walking_trips_edit.view.amountPickerHours1
import kotlinx.android.synthetic.main.fragment_walking_trips_edit.view.amountPickerHours2
import kotlinx.android.synthetic.main.fragment_walking_trips_edit.view.amountPickerMinutes1
import kotlinx.android.synthetic.main.fragment_walking_trips_edit.view.amountPickerMinutes2
import kotlinx.android.synthetic.main.fragment_walking_trips_edit.view.editCaloriesBurned
import kotlinx.android.synthetic.main.fragment_walking_trips_edit.view.editDistance
import kotlinx.android.synthetic.main.fragment_walking_trips_edit.view.editTripName
import kotlinx.android.synthetic.main.fragment_walking_trips_edit.view.updateTripFab
import java.text.SimpleDateFormat
import java.util.*


class CyclingTripsEditFragment : Fragment() {

    var trip = Trip()
    var user = Account()
    lateinit var app: MainApp
    lateinit var root: View
    lateinit var eventListener : ValueEventListener
    val sdf = SimpleDateFormat("EEEE")
    val month_date = SimpleDateFormat("MMMM")
    var dow = ""
    var dateedit= ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_cycling_trips_edit, container, false)
        activity?.title = getString(R.string.menu_edit)

        val bundle = arguments
        if (bundle != null) {
            trip = bundle.getParcelable("trip_key")!!
        }

        getUser()

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
        root.editSpeed.setText(trip.averageSpeed)
        root.amountPickerHours1.value = trip.tripStartTime.substringBefore(":").toInt()
        root.amountPickerMinutes1.value = trip.tripStartTime.substringAfter(":").toInt()
        root.editCaloriesBurned.setText(trip.caloriesBurned.toString())

        if(trip.tripName != ""){
            root.editTripName.setText(trip.tripName.toString())
        }

        root.amountPickerHours2.value = trip.tripEndTime.substringBefore(":").toInt()
        root.amountPickerMinutes2.value = trip.tripEndTime.substringAfter(":").toInt()

        root.cbCyclingAddToFavs.isChecked = trip.favourite
        //del button


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
                val day: Int = root.date_picker_cycling.dayOfMonth
                val month: Int = root.date_picker_cycling.month
                val year: Int = root.date_picker_cycling.year
                val calendar = Calendar.getInstance()
                calendar.set(year,month,day)
                dow = sdf.format(calendar.time)
                dateedit = "${day}, ${month_date.format(calendar.time)} $year"

                trip.tripDistance = (root.editDistance.text.toString()).toDouble()
                trip.averageSpeed = (root.editSpeed.text.toString())
                trip.caloriesBurned = (root.editCaloriesBurned.text.toString()).toDouble()
                trip.tripName = root.editTripName.text.toString()
                trip.favourite = root.cbCyclingAddToFavs.isChecked
                trip.tripLength = ""
                trip.DayOfWeek = dow
                trip.Date = dateedit

                if ((root.amountPickerHours2.value-root.amountPickerHours1.value) < 10){
                    trip.tripLength += (root.amountPickerHours2.value - root.amountPickerHours1.value).toString() + "Hours"
                }else{
                    trip.tripLength += (root.amountPickerHours2.value - root.amountPickerHours1.value).toString() + "Hours"
                }

                if(root.amountPickerMinutes2.value < root.amountPickerMinutes1.value){
                    if ((root.amountPickerMinutes1.value - root.amountPickerMinutes2.value) < 10){

                        trip.tripLength = ((root.amountPickerHours2.value - root.amountPickerHours1.value) - 1).toString() + "Hours"
                        trip.tripLength += ", " +(0 + (root.amountPickerMinutes1.value - root.amountPickerMinutes2.value)).toString() + "Minutes"



                    }else{
                        trip.tripLength = ((root.amountPickerHours2.value - root.amountPickerHours1.value) - 1).toString()+ "Hours"
                        trip.tripLength += ", " + (60-(root.amountPickerMinutes1.value - root.amountPickerMinutes2.value)).toString() + "Minutes"
                    }
                }else{
                    if ((root.amountPickerMinutes2.value - root.amountPickerMinutes1.value) < 10){
                        trip.tripLength += ", " +(root.amountPickerMinutes2.value - root.amountPickerMinutes1.value).toString() + "Minutes"
                    }else{
                        trip.tripLength += ", " + (root.amountPickerMinutes2.value - root.amountPickerMinutes1.value).toString() + "Minutes"
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



                updateUserDonation(app.auth.currentUser!!.uid,trip)
                val toast =
                    Toast.makeText(
                        activity!!.applicationContext,
                        "Adventure Updated!",
                        Toast.LENGTH_LONG
                    )
                toast.show()
            }
        }


        return root
    }

    fun updateUserDonation(uid: String?, trip: Trip) {
        app.database.child("user-trips").child(uid!!).child(trip.dtID)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.setValue(trip)
                        activity!!.supportFragmentManager.beginTransaction()
                            .replace(R.id.homeFrame, TripsListFragment.newInstance())
                            .addToBackStack(null)
                            .commit()
                        //hideLoader(loader)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        //info("Firebase Donation error : ${error.message}")
                    }
                })
    }

    fun deleteUserTrip(uid: String?, trip: Trip) {
        app.database.child("user-trips").child(uid!!).child(trip.dtID)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.removeValue()
                        activity!!.supportFragmentManager.beginTransaction()
                            .replace(R.id.homeFrame, TripsListFragment.newInstance())
                            .addToBackStack(null)
                            .commit()
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
    }

    fun getUser(){
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val rootRef = FirebaseDatabase.getInstance().reference
        val uidRef = rootRef.child("user-stats").child(uid)
        eventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                user = dataSnapshot.getValue(Account::class.java)!!
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        uidRef.addListenerForSingleValueEvent(eventListener)
    }

    companion object {
        @JvmStatic
        fun newInstance(trip: Trip) =
            CyclingTripsEditFragment().apply {
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
