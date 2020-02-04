package ie.wit.adventurio.activities

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isVisible
import ie.wit.adventurio.R
import ie.wit.adventurio.models.Account
import kotlinx.android.synthetic.main.activity_walking_trip_tracking.*

class WalkingTripTrackingActivity : AppCompatActivity(), SensorEventListener {

    var user = Account()
    var running = false
    var sensorManager:SensorManager? = null
    var step_goal = 0
    var currentSteps = 0
    private var progressBar: ProgressBar? = null
    /*
    ADD STEP GOAL PER TRIP (OPTIONAL
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_walking_trip_tracking)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepsValue.setText("0")
        start_button.isVisible = true
        stop_button.isVisible = false
        linear1.isVisible = true
        linear2.isVisible = false
        progressBar = findViewById<ProgressBar>(R.id.progressBar) as ProgressBar


        stop_button.setOnClickListener {
            currentSteps = 0
            start_button.isVisible = true
            stop_button.isVisible = false
            linear1.isVisible = true
            linear2.isVisible = false
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
            onResume()
        }

        if (intent.hasExtra("userLoggedIn")) {
            user = intent.extras.getParcelable<Account>("userLoggedIn")
        }
    }

    override fun onResume() {
        super.onResume()
        running = true
        var stepsSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) ///add ACCELLOOMMETETER

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
            progressBar!!.isVisible = false
            currentSteps += (event.values[0].toInt() - event.values[0].toInt()) + 1
            stepsValue.setText("" + currentSteps)
        }

    }
}
