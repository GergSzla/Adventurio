package ie.wit.adventurio.fragments

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.NumberPicker
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.gms.maps.GoogleMap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ie.wit.adventurio.R
import ie.wit.adventurio.main.MainApp
import ie.wit.adventurio.models.Account
import ie.wit.adventurio.models.Trip
import ie.wit.adventurio.models.Vehicle
import ie.wit.fragments.TripsListFragment
import kotlinx.android.synthetic.main.fragment_manual_trip.*
import kotlinx.android.synthetic.main.fragment_manual_trip.view.*
import kotlinx.android.synthetic.main.fragment_walking_trips_edit.view.amountPickerHours1
import kotlinx.android.synthetic.main.fragment_walking_trips_edit.view.amountPickerHours2
import kotlinx.android.synthetic.main.fragment_walking_trips_edit.view.amountPickerMinutes1
import kotlinx.android.synthetic.main.fragment_walking_trips_edit.view.amountPickerMinutes2
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class ManualTripFragment : Fragment() {

    var trip = Trip()
    var user = Account()
    var vehicles = ArrayList<String>()
    var vehiclesList = ArrayList<Vehicle>()
    lateinit var app: MainApp
    lateinit var root: View
    lateinit var eventListener : ValueEventListener
    var dateId: String = ""
    val d = Date()
    val sdf = SimpleDateFormat("EEEE")
    val month_date = SimpleDateFormat("MMMM")
    var carPos = ""

    var vehicleUsed = Vehicle()

    var dow = ""
    var date= ""

    private lateinit var map: GoogleMap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_manual_trip, container, false)
        activity?.title = getString(R.string.menu_manual_record)


        //https://android--code.blogspot.com/2018/02/android-kotlin-spinner-example.html
        val categories = arrayOf("Walking","Driving","Cycling")

        val adapter = ArrayAdapter(
            activity!!, // Context
            android.R.layout.simple_spinner_item, // Layout
            categories // Array
        )

        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)

        root.spinner.adapter = adapter

        root.spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent:AdapterView<*>, view: View, position: Int, id: Long){
                if(parent.getItemAtPosition(position).toString() == "Walking"){
                    trip.tripType = "Walking"
                    root.WalkingTrip.isVisible = true
                    root.DrivingTrip.isVisible = false
                    root.CyclingTrip.isVisible = false
                } else if (parent.getItemAtPosition(position).toString() == "Driving"){
                    val cars = vehicles

                    val adapter = ArrayAdapter(
                        activity!!, // Context
                        android.R.layout.simple_spinner_item, // Layout
                        cars // Array
                    )

                    adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)

                    root.editDrivingTripVehicles.adapter = adapter

                    trip.tripType = "Driving"
                    root.WalkingTrip.isVisible = false
                    root.DrivingTrip.isVisible = true
                    root.CyclingTrip.isVisible = false
                    getVehiclesNames()
                } else if (parent.getItemAtPosition(position).toString() == "Cycling"){
                    trip.tripType = "Cycling"
                    root.WalkingTrip.isVisible = false
                    root.DrivingTrip.isVisible = false
                    root.CyclingTrip.isVisible = true
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>){
                // Another interface callback
            }
        }

        getUser()
        getVehiclesNames()
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


        /*var num = trip.tripDistance
        root.editDistance.setText("%.2f".format(num))
        root.editSteps.setText(trip.tripSteps.toString())
        root.amountPickerHours1.value = trip.tripStartTime.substring(0,2).toInt()
        root.amountPickerMinutes1.value = trip.tripStartTime.substring(3,5).toInt()

        if(trip.tripName != ""){
            root.editTripName.setText(trip.tripName.toString())
        }

        root.amountPickerHours2.value = trip.tripEndTime.substring(0,2).toInt()
        root.amountPickerMinutes2.value = trip.tripEndTime.substring(3,5).toInt()*/

        //update button
        root.createTripFab.setOnClickListener {

            val day: Int = root.date_Picker.dayOfMonth
            val month: Int = root.date_Picker.month
            val year: Int = root.date_Picker.year
            val calendar = Calendar.getInstance()
            calendar.set(year,month,day)
            dow = sdf.format(calendar.time)
            date = "${day}, ${month_date.format(calendar.time)} $year"

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
                trip.tripLength = ""

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



                generateDateID()
                if(trip.tripType == "Driving" && editDrivingTripVehicles.selectedItem.toString() == ""){
                    val toast =
                        Toast.makeText(
                            activity!!.applicationContext,
                            "No Vehicle Exists or Selected! Please Create One!",
                            Toast.LENGTH_LONG
                        )
                    toast.show()
                } else {
                    if(trip.tripType == "Walking"){
                        validateWalkingForm()
                        if(!(root.editWalkingTripName.text.toString() == "" ||
                                    root.editWalkingDistance.text.toString() == "" ||
                                    root.editWalkingTripSteps.text.toString() == "" ||
                                    root.editWalkingTripCalories.text.toString() == "" )){
                            addingData()
                            createTrip()
                        }
                    } else if(trip.tripType == "Driving"){
                        validateDrivingForm()
                        if(!(root.editDrivingTripName.text.toString() == "" ||
                                    root.editDrivingDistance.text.toString() == "" ||
                                    root.editDrivingTripSpeed.text.toString() == "")){
                            addingData()
                            createTrip()
                        }
                    } else if(trip.tripType == "Cycling"){
                        validateCyclingForm()
                        if(!(root.editCyclingTripName.text.toString() == "" ||
                                    root.editCyclingDistance.text.toString() == "" ||
                                    root.editCyclingTripSpeed.text.toString() == "" ||
                                    root.editCyclingTripCalories.text.toString() == "" )){
                            addingData()
                            createTrip()
                        }
                    }
                }
            }
        }


        return root
    }



    fun addingData(){
        //dow = sdf.format(d)
        //date = cal.get(Calendar.DAY_OF_MONTH).toString() + ", " + month_date.format(cal.time)

        when (trip.tripType) {
            "Walking" -> {
                trip.tripName = editWalkingTripName.text.toString()
                trip.tripDistance = editWalkingDistance.text.toString().toDouble()
                trip.caloriesBurned = editWalkingTripCalories.text.toString().toDouble()
                trip.tripSteps = editWalkingTripSteps.text.toString().toInt()
                trip.tripOwner = user.id
                trip.dtID = dateId
                trip.tripID = UUID.randomUUID().toString()
                trip.DayOfWeek = dow
                trip.Date = date

            }
            "Driving" -> {
                trip.tripName = editDrivingTripName.text.toString()
                trip.tripDistance = editDrivingDistance.text.toString().toDouble()
                trip.averageSpeed = editDrivingTripSpeed.text.toString()
                trip.vehicleUsed = editDrivingTripVehicles.selectedItem.toString()
                trip.tripOwner = user.id
                trip.dtID = dateId
                trip.tripID = UUID.randomUUID().toString()
                trip.DayOfWeek = dow
                trip.Date = date
                carPos = vehicles.indexOf(trip.vehicleUsed).toString()

                vehiclesList.forEach {
                    if(it.pos == carPos){
                        vehicleUsed = it
                    }
                }
                var num = trip.tripDistance
                vehicleUsed.currentOdometer +=  ("%.0f".format(num)).toInt()
                updateVehicle(app.auth.currentUser!!.uid,carPos)
            }
            "Cycling" -> {
                trip.tripName = editCyclingTripName.text.toString()
                trip.tripDistance = editCyclingDistance.text.toString().toDouble()
                trip.averageSpeed = editCyclingTripSpeed.text.toString()
                trip.caloriesBurned = editCyclingTripCalories.text.toString().toDouble()
                trip.tripOwner = user.id
                trip.dtID = dateId
                trip.tripID = UUID.randomUUID().toString()
                trip.DayOfWeek = dow
                trip.Date = date
            }
        }
    }

    fun writeNewTrip(trip: Trip) {
        //showLoader(loader, "Adding User to Firebase")
        //val uid = app.auth.currentUser!!.uid
        val key = dateId
        val uid = app.auth.currentUser!!.uid
        val tripValues = trip.toMap()

        val childUpdates = HashMap<String, Any>()
        childUpdates["/user-trips/$uid/$key"] = tripValues
        //childUpdates["/user-trips/${user.Email}/$key"] = userValues

        app.database.updateChildren(childUpdates)
        //hideLoader(loader)
    }

    fun generateDateID(){
        var currentEndDateTime= LocalDateTime.now()
        var year = Calendar.getInstance().get(Calendar.YEAR).toString()
        var month = ""
        if (Calendar.getInstance().get(Calendar.MONTH)+1 < 10){
            month = "0"+(Calendar.getInstance().get(Calendar.MONTH)+1).toString()
        }else{
            month = (Calendar.getInstance().get(Calendar.MONTH)+1).toString()
        }
        var day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH).toString()
        var hour = currentEndDateTime.format(DateTimeFormatter.ofPattern("HH")).toString()
        var minutes = currentEndDateTime.format(DateTimeFormatter.ofPattern("mm")).toString()
        var seconds = currentEndDateTime.format(DateTimeFormatter.ofPattern("ss")).toString()


        dateId = year+month+day+hour+minutes+seconds
    }

    private fun createTrip() {
        /*if (!validateForm()) {
            return
        }*/
        app.database = FirebaseDatabase.getInstance().reference
        writeNewTrip(trip)
        navigateTo(TripsListFragment.newInstance())
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

    fun getVehiclesNames(){
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        app.database.child("user-stats").child(uid).child("vehicles")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val children = snapshot.children
                    children.forEach {
                        val vehicle = it.
                        getValue<Vehicle>(Vehicle::class.java)

                        vehicles.add(vehicle!!.vehicleName)
                        vehiclesList.add(vehicle)

                        app.database.child("user-stats").child(uid).child("vehicles")
                            .removeEventListener(this)
                    }
                }
            })
    }

    fun updateVehicle(uid: String?,carPos : String) {
        app.database.child("user-stats").child(uid!!).child("vehicles").child(carPos)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.setValue(vehicleUsed)
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

    private fun validateWalkingForm(): Boolean {
        var valid = true

        //validate tripName
        val tripname = root.editWalkingTripName.text.toString()
        if (TextUtils.isEmpty(tripname)) {
            root.editWalkingTripName.error = "Required."
            valid = false
        } else {
            root.editWalkingTripName.error = null
        }

        //validate distance
        val distance = root.editWalkingDistance.text.toString()
        if (TextUtils.isEmpty(distance)) {
            root.editWalkingDistance.error = "Required (To Calculate Calories Burned!)."
            valid = false
        } else {
            root.editWalkingDistance.error = null
        }

        //validate steps
        val steps = root.editWalkingTripSteps.text.toString()
        if (TextUtils.isEmpty(steps)) {
            root.editWalkingTripSteps.error = "Required."
            valid = false
        } else {
            root.editWalkingTripSteps.error = null
        }

        //validate cal
        val cal = root.editWalkingTripCalories.text.toString()
        if (TextUtils.isEmpty(cal)) {
            root.editWalkingTripCalories.error = "Required."
            valid = false
        } else {
            root.editWalkingTripCalories.error = null
        }

        return valid
    }

    private fun validateDrivingForm(): Boolean {
        var valid = true

        //validate tripName
        val tripname = root.editDrivingTripName.text.toString()
        if (TextUtils.isEmpty(tripname)) {
            root.editDrivingTripName.error = "Required."
            valid = false
        } else {
            root.editDrivingTripName.error = null
        }

        //validate distance
        val distance = root.editDrivingDistance.text.toString()
        if (TextUtils.isEmpty(distance)) {
            root.editDrivingDistance.error = "Required (To Calculate Calories Burned!)."
            valid = false
        } else {
            root.editDrivingDistance.error = null
        }

        //validate speed
        val speed = root.editDrivingTripSpeed.text.toString()
        if (TextUtils.isEmpty(speed)) {
            root.editDrivingTripSpeed.error = "Required."
            valid = false
        } else {
            root.editDrivingTripSpeed.error = null
        }

        return valid
    }

    private fun validateCyclingForm(): Boolean {
        var valid = true

        //validate tripName
        val tripname = root.editCyclingTripName.text.toString()
        if (TextUtils.isEmpty(tripname)) {
            root.editCyclingTripName.error = "Required."
            valid = false
        } else {
            root.editCyclingTripName.error = null
        }

        //validate distance
        val distance = root.editCyclingDistance.text.toString()
        if (TextUtils.isEmpty(distance)) {
            root.editCyclingDistance.error = "Required (To Calculate Calories Burned!)."
            valid = false
        } else {
            root.editCyclingDistance.error = null
        }

        //validate speed
        val speed = root.editCyclingTripSpeed.text.toString()
        if (TextUtils.isEmpty(speed)) {
            root.editCyclingTripSpeed.error = "Required."
            valid = false
        } else {
            root.editCyclingTripSpeed.error = null
        }

        //validate cal
        val cal = root.editCyclingTripCalories.text.toString()
        if (TextUtils.isEmpty(cal)) {
            root.editCyclingTripCalories.error = "Required."
            valid = false
        } else {
            root.editCyclingTripCalories.error = null
        }

        return valid
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ManualTripFragment().apply {
                arguments = Bundle().apply {
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
