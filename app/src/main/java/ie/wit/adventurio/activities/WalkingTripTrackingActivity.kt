package ie.wit.adventurio.activities

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import ie.wit.adventurio.R
import ie.wit.adventurio.main.MainApp
import ie.wit.adventurio.models.Account
import ie.wit.adventurio.models.WalkingTrip
import kotlinx.android.synthetic.main.activity_walking_trip_tracking.*
import java.util.*

class WalkingTripTrackingActivity : AppCompatActivity(), SensorEventListener {

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
        setContentView(R.layout.activity_walking_trip_tracking)

        app = application as MainApp

        if (intent.hasExtra("userLoggedIn")) {
            user = intent.extras.getParcelable<Account>("userLoggedIn")
        }

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepsValue.setText("0")
        start_button.isVisible = true
        stop_button.isVisible = false
        linear1.isVisible = true
        linear2.isVisible = false
        progressBar = findViewById<ProgressBar>(R.id.progressBar) as ProgressBar


        stop_button.setOnClickListener {

            saveTrip()

            currentSteps = 0
            start_button.isVisible = true
            stop_button.isVisible = false
            linear1.isVisible = true
            linear2.isVisible = false

            handler?.removeCallbacks(runnable)
            flag=false

            onPause()
        }

        start_button.setOnClickListener {
            currentSteps = 0
            start_button.isVisible = false
            stop_button.isVisible = true
            if(txtStepGoal.text.toString() != ""){
                step_goal = (txtStepGoal.text.toString()).toInt()
            }else{
                step_goal = 0
            }
            linear1.isVisible = false
            linear2.isVisible = true
            bindViews()

            StartTime = SystemClock.uptimeMillis()
            handler?.postDelayed(runnable, 0)
            flag=true

            onResume()
        }

        if (intent.hasExtra("userLoggedIn")) {
            user = intent.extras.getParcelable<Account>("userLoggedIn")
        }
    }

    override fun onResume() {
        super.onResume()
        running = true
        var stepsSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepsSensor == null) {
            Toast.makeText(this, "No Step Counter Sensor !", Toast.LENGTH_SHORT).show()
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
        trip.tripTime = "${hour!!.text.toString()}:${minute!!.text.toString()}:${seconds!!.text.toString()}"
        trip.tripID = UUID.randomUUID().toString()
        trip.tripType = "Walking"
        trip.tripOwner = user.id
        trip.tripSteps = currentSteps
        trip.tripDistance = 0.0008 * currentSteps
        app.trips.create(trip.copy())
        finish()
    }


}
