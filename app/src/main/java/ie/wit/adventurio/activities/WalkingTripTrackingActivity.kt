package ie.wit.adventurio.activities

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import ie.wit.adventurio.R
import ie.wit.adventurio.models.Account
import kotlinx.android.synthetic.main.activity_walking_trip_tracking.*

class WalkingTripTrackingActivity : AppCompatActivity(), SensorEventListener {

    var user = Account()
    var running = false
    var sensorManager:SensorManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_walking_trip_tracking)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        start_button.isVisible = true
        stop_button.isVisible = false
        stepsLbl.isVisible = false
        stepsValue.isVisible = false
        txt4.isVisible = true
        spinnerPicker.isVisible = true

        stop_button.setOnClickListener {
            start_button.isVisible = true
            stop_button.isVisible = false
            stepsLbl.isVisible = false
            stepsValue.isVisible = false
            txt4.isVisible = true
            spinnerPicker.isVisible = true
            onPause()
        }

        start_button.setOnClickListener {
            start_button.isVisible = false
            stop_button.isVisible = true
            stepsLbl.isVisible = true
            stepsValue.isVisible = true
            txt4.isVisible = false
            spinnerPicker.isVisible = false
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
        if (running) {
            stepsValue.setText("" + event.values[0])
        }
    }
}
