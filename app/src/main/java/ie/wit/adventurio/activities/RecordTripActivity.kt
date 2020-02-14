package ie.wit.adventurio.activities

import android.content.Context
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
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import ie.wit.adventurio.R
import ie.wit.adventurio.main.MainApp
import ie.wit.adventurio.models.Account
import ie.wit.adventurio.models.WalkingTrip
import kotlinx.android.synthetic.main.activity_record_trip.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class RecordTripActivity : AppCompatActivity(), SensorEventListener {

    private var locationManager : LocationManager? = null

    lateinit var app: MainApp

    var user = Account()
    var trip = WalkingTrip()

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

        setContentView(R.layout.activity_record_trip)

        val d = Date()
        val sdf = SimpleDateFormat("EEEE")
        val cal = Calendar.getInstance()
        val month_date = SimpleDateFormat("MMMM")

        app = application as MainApp

        if (intent.hasExtra("user_key")) {
            user = intent.extras.getParcelable<Account>("user_key")
        }
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepsValue.setText("0")
        start_button.isVisible = true
        stop_button.isVisible = false
        linear1.isVisible = true
        linear2.isVisible = false
        progressBar = findViewById<ProgressBar>(R.id.progressBar) as ProgressBar


        stop_button.setOnClickListener {
            var currentEndDateTime= LocalDateTime.now()
            end = currentEndDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))

            saveTrip()

            currentSteps = 0
            start_button.isVisible = true
            stop_button.isVisible = false
            linear1.isVisible = true
            linear2.isVisible = false



            handler?.removeCallbacks(runnable)
            flag=false
            txtHint.isVisible = true

            onPause()
        }

        start_button.setOnClickListener {
            try {
                // Request location updates
                locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, locationListener)
            } catch(ex: SecurityException) {
                Log.d("myTag", "Security Exception, no location available")
            }
            currentSteps = 0
            start_button.isVisible = false
            stop_button.isVisible = true
            txtHint.isVisible = false
            if(txtStepGoal.text.toString() != ""){
                step_goal = (txtStepGoal.text.toString()).toInt()
            }else{
                step_goal = 0
            }
            linear1.isVisible = false
            linear2.isVisible = true
            bindViews()
            var currentStartDateTime= LocalDateTime.now()
            start = currentStartDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
            dow = sdf.format(d)
            date = cal.get(Calendar.DAY_OF_MONTH).toString() + ", " + month_date.format(cal.getTime())

            StartTime = SystemClock.uptimeMillis()
            handler?.postDelayed(runnable, 0)
            flag=true

            onResume()
        }

        if (intent.hasExtra("user_key")) {
            user = intent.extras.getParcelable<Account>("user_key")
        }


    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            if(stop_button.isVisible == true){
                lng.add("${location.longitude}")
                lat.add("${location.latitude}")
                ///Test

                /*
                lng.add("-7.251961")
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
                lat.add("52.671431")
*/

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
            stepsValue.setText("" + currentSteps + "/${step_goal.toString()}")
            progressBar!!.max = step_goal.toInt()
            progressBar!!.progress = currentSteps


        } else if(running && step_goal == 0){
            currentSteps += (event.values[0].toInt() - event.values[0].toInt()) + 1
            stepsValue.setText("" + currentSteps)
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

    private fun saveTrip(){
        trip.tripLength = "${hour!!.text.toString()}:${minute!!.text.toString()}:${seconds!!.text.toString()}"
        trip.tripID = UUID.randomUUID().toString()
        trip.tripType = "Walking"
        trip.tripOwner = user.id
        trip.tripSteps = currentSteps
        trip.tripDistance = 0.0008 * currentSteps
        trip.lng = lng
        trip.lat = lat
        trip.tripEndTime = end
        trip.tripStartTime = start
        trip.DayOfWeek = dow
        trip.Date = date

        app.trips.create(trip.copy())
        startActivityForResult(intentFor<Home>().putExtra("user_key", user), 0)
        finish()
    }


}
