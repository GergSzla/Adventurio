package ie.wit.adventurio.activities

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.github.anastr.speedviewlib.SpeedView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ie.wit.adventurio.R
import ie.wit.adventurio.helpers.createLoader
import ie.wit.adventurio.helpers.hideLoader
import ie.wit.adventurio.helpers.showLoader
import ie.wit.adventurio.main.MainApp
import ie.wit.adventurio.models.Account
import ie.wit.adventurio.models.Trip
import ie.wit.adventurio.models.Vehicle
import kotlinx.android.synthetic.main.activity_record_driving_trip.*
import kotlinx.android.synthetic.main.fragment_manual_trip.view.*

import org.jetbrains.anko.intentFor
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class RecordDrivingTripActivity : AppCompatActivity(), SensorEventListener {

    private var locationManager : LocationManager? = null
    lateinit var loader : AlertDialog
    lateinit var eventListener : ValueEventListener

    lateinit var app: MainApp

    var user = Account()
    var trip = Trip()
    var dateId: String = ""
    var vehicles = ArrayList<String>()
    var speeds = ArrayList<Int>()



    var running = false
    var sensorManager:SensorManager? = null
    var step_goal = 0
    var currentSteps = 0
    private var progressBar: ProgressBar? = null

    var handler: Handler? = null
    var hour: TextView? = null
    var minute: TextView? = null
    var seconds: TextView? = null
    var milli_seconds: TextView? = null
    var lng : MutableList<String> = mutableListOf<String>()
    var lat : MutableList<String> = mutableListOf<String>()

    var start = ""
    var end = ""
    var dow = ""
    var date= ""

    internal var MillisecondTime: Long = 0
    internal var StartTime: Long = 0
    internal var TimeBuff: Long = 0
    internal var UpdateTime = 0L


    internal var Seconds: Int = 0
    internal var Minutes: Int = 0

    internal var flag:Boolean=false
    /*
    ADD STEP GOAL PER TRIP (OPTIONAL
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_record_driving_trip)

        val d = Date()
        val sdf = SimpleDateFormat("EEEE")
        val cal = Calendar.getInstance()
        val month_date = SimpleDateFormat("MMMM")

        app = application as MainApp
        loader = createLoader(this)

        speedView.setMinMaxSpeed(0F, 140F)
        speedView.sections[0].color = Color.parseColor("#6a6a6a")
        speedView.sections[1].color = Color.parseColor("#3d3232")
        speedView.sections[2].color = Color.parseColor("#321919")
        speedView.withTremble = false

        if (ContextCompat.checkSelfPermission(app, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            val permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
            ActivityCompat.requestPermissions(this, permissions,0)
        } else {
            txtWarning.text = ""
        }



        getUser()
        getVehiclesNames()



        //checkWarningPermission()
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        start_driving_button.isVisible = true
        stop_driving_button.isVisible = false
        linear1.isVisible = true
        linear2.isVisible = false


        stop_driving_button.setOnClickListener {
            var currentEndDateTime= LocalDateTime.now()
            end = currentEndDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))

            generateDateID()
            saveTrip()

            currentSteps = 0
            start_driving_button.isVisible = true
            stop_driving_button.isVisible = false
            linear1.isVisible = true
            linear2.isVisible = false



            handler?.removeCallbacks(runnable)
            flag=false
            txtHint.isVisible = true

            onPause()
        }

        start_driving_button.setOnClickListener {
            if(spinnerCarPick.selectedItem != null){
                try {
                    // Request location updates
                    locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, locationListener)
                } catch(ex: SecurityException) {
                    Log.d("myTag", "Security Exception, no location available")
                }
                currentSteps = 0
                start_driving_button.isVisible = false
                stop_driving_button.isVisible = true
                txtHint.isVisible = false

                linear1.isVisible = false
                linear2.isVisible = true
                bindViews()
                var currentStartDateTime= LocalDateTime.now()
                start = currentStartDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                dow = sdf.format(d)
                date = cal.get(Calendar.DAY_OF_MONTH).toString() + ", " + month_date.format(cal.getTime()) +" "+ cal.get(Calendar.YEAR).toString()

                StartTime = SystemClock.uptimeMillis()
                handler?.postDelayed(runnable, 0)
                flag=true

                onResume()
            } else {
                Toast.makeText(this, "No Vehicle Found/Exists!", Toast.LENGTH_SHORT).show()
            }
        }



    }

    fun checkWarningPermission(){
        var permissionEnabled =
            ContextCompat.checkSelfPermission(app, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (permissionEnabled == false){
            txtWarning.text = "GPS Permissions are not Granted! Trip Locations Aren't Recorded! "
            txtWarning.setTextColor(Color.parseColor("#970000"))
        } else {
            txtWarning.text = ""
        }
    }


    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            if(stop_driving_button.isVisible == true){
                lng.add("${location.longitude}")
                lat.add("${location.latitude}")

                speeds.add((location.speed * 3600 / 1000).toInt())
                speedDrivingValue.text = ((location.speed * 3600 / 1000).toInt()).toString()

                speedView.speedTo((location.speed * 3600 / 1000).toInt().toFloat())
                ///Test
                /*lng.add("-7.251961")
                lat.add("52.671500")

                lng.add("-7.252154")
                lat.add("52.672651")

                lng.add("-7.253526")
                lat.add("52.672515")

                lng.add("-7.253162")
                lat.add("52.671253")

                lng.add("-7.252615")
                lat.add("52.670244")

                lng.add("-7.252473")
                lat.add("52.669141")

                lng.add("-7.252584")
                lat.add("52.668460")

                lng.add("-7.252649")
                lat.add("52.667604")

                lng.add("-7.251228")
                lat.add("52.667754")

                lng.add("-7.251126")
                lat.add("52.668631")

                lng.add("-7.250072")
                lat.add("52.671575")

                lng.add("-7.250801")
                lat.add("52.671679")

                lng.add("-7.252109")
                lat.add("52.671431")*/


            }
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    override fun onResume() {
        super.onResume()
        running = true
        var stepsSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepsSensor == null) {
            Toast.makeText(this, "No Sensor Available", Toast.LENGTH_SHORT).show()
        } else {
            sensorManager?.registerListener(this, stepsSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        running = false
        sensorManager?.unregisterListener(this)

    }



    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }


    override fun onSensorChanged(event: SensorEvent) {
        if (running && step_goal != 0 ) {
            currentSteps += (event.values[0].toInt() - event.values[0].toInt()) + 1

            progressBar!!.max = step_goal.toInt()
            progressBar!!.progress = currentSteps


        } else if(running && step_goal == 0){
            currentSteps += (event.values[0].toInt() - event.values[0].toInt()) + 1
        }

    }

    private fun bindViews() {

        hour = findViewById(R.id.hour)
        minute = findViewById(R.id.minute)
        seconds = findViewById(R.id.seconds)

        handler = Handler()

    }


    var runnable: Runnable = object : Runnable {

        override fun run() {

            MillisecondTime = SystemClock.uptimeMillis() - StartTime

            UpdateTime = TimeBuff + MillisecondTime

            Seconds = (UpdateTime / 1000).toInt()

            Minutes = Seconds / 60

            Seconds = Seconds % 60


            if (Minutes.toString().length < 2) {
                minute?.text = "0" + Minutes.toString()
            } else {
                minute?.text = Minutes.toString()
            }
            if (Seconds.toString().length < 2) {
                seconds?.text = "0" + Seconds.toString()
            } else {
                seconds?.text = Seconds.toString()
            }

            handler?.postDelayed(this, 0)
        }

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

    var total_minutes = 0.0
    var carPos = ""
    var vehiclesList = ArrayList<Vehicle>()
    var vehicleUsed = Vehicle()

    private fun saveTrip(){
        trip.tripLength = "${hour!!.text.toString()}Hours, ${minute!!.text.toString()}Minutes, ${seconds!!.text.toString()}Seconds"
        trip.tripID = UUID.randomUUID().toString()
        trip.tripType = "Driving"
        trip.tripOwner = user.id
        trip.averageSpeed = (speeds.average().toInt()).toString()
        trip.lng = lng
        trip.lat = lat
        trip.tripEndTime = end
        if (hour!!.text.toString().toInt() > 0){
            total_minutes = (hour!!.text.toString().toDouble() * 60) + minute!!.text.toString().toInt()
        } else {
            total_minutes = minute!!.text.toString().toDouble()
        }
        var num = 0.029 * (user.weight / 0.45359237) * total_minutes
        trip.vehicleUsed = spinnerCarPick.selectedItem.toString()
        var speedsAvg =speeds.average()
        var minToHr = total_minutes / 60
        trip.tripDistance = speedsAvg * minToHr
        trip.tripStartTime = start
        trip.DayOfWeek = dow
        trip.Date = date
        trip.dtID = dateId

        carPos = vehicles.indexOf(trip.vehicleUsed).toString()
        vehiclesList.forEach {
            if(it.pos == carPos){
                vehicleUsed = it
            }
        }
        var num2 = trip.tripDistance
        vehicleUsed.currentOdometer +=  ("%.0f".format(num2)).toInt()
        updateVehicle(app.auth.currentUser!!.uid,carPos)

        //app.trips.create(trip.copy())
        createTrip()
        startActivityForResult(intentFor<Home>().putExtra("user_key", user), 0)
        finish()
    }

    fun updateVehicle(uid: String?,carPos : String) {
        app.database.child("user-stats").child(uid!!).child("vehicles").child(carPos)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.setValue(vehicleUsed)
                        this@RecordDrivingTripActivity.supportFragmentManager.beginTransaction()
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
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
                    val cars = vehicles

                    val adapter = ArrayAdapter(
                        this@RecordDrivingTripActivity, // Context
                        android.R.layout.simple_spinner_item, // Layout
                        cars // Array
                    )

                    adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)

                    spinnerCarPick.adapter = adapter;
                    if(vehicles.size == 0){
                        txtWarning.setText("No Vehicles Found!")
                        txtWarning.setTextColor(Color.parseColor("#970000"))
                    }
                }
            })
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

        showLoader(loader, "Creating Account...")

        //val user = app.auth.currentUser
        app.database = FirebaseDatabase.getInstance().reference
        writeNewTrip(trip)
        //startActivity<LoginActivity>()

        // [START_EXCLUDE]
        hideLoader(loader)
        // [END_EXCLUDE]

        // [END create_user_with_email]
    }

}
