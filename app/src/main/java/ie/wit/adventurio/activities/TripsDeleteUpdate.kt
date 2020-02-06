package ie.wit.adventurio.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import ie.wit.adventurio.R
import ie.wit.adventurio.main.MainApp
import ie.wit.adventurio.models.Account
import ie.wit.adventurio.models.WalkingTrip
import kotlinx.android.synthetic.main.activity_trips_delete_update.*
import org.jetbrains.anko.intentFor

class TripsDeleteUpdate : AppCompatActivity() {

    var trip = WalkingTrip()
    lateinit var app: MainApp


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trips_delete_update)

        app = application as MainApp


        if (intent.hasExtra("tripEdit")) {
            trip = intent.extras.getParcelable<WalkingTrip>("tripEdit")
        }
        amountPickerHours.minValue = 0
        amountPickerHours.maxValue = 24
        amountPickerMinutes.minValue = 0
        amountPickerMinutes.maxValue = 59
        amountPickerSeconds.minValue = 0
        amountPickerSeconds.maxValue = 59

        var num = trip.tripDistance
        editDistance.setText("%.2f".format(num))
        editSteps.setText(trip.tripSteps.toString())
        amountPickerHours.value = trip.tripTime.substring(0,2).toInt()
        amountPickerMinutes.value = trip.tripTime.substring(3,5).toInt()
        amountPickerSeconds.value = trip.tripTime.substring(6,8).toInt()

        btnDelTrip.setOnClickListener {
            app.trips.delete(trip)
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit_profile, menu)
        if (menu != null) menu.getItem(0).setVisible(true)
        return super.onCreateOptionsMenu(menu)


    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.item_cancel -> {
                finish()
            }
            R.id.item_update -> {
                updateTrip()
                startActivityForResult(intentFor<TripsListActivity>().putExtra("tripEdit", trip), 0)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun updateTrip(){
        trip.tripDistance = (editDistance.text.toString()).toDouble()
        trip.tripSteps = (editSteps.text.toString()).toInt()
        trip.tripTime = ""
        if(amountPickerHours.value < 10){
            trip.tripTime += "0" + amountPickerHours.value.toString() + ":"
        } else {
            trip.tripTime += amountPickerHours.value.toString() + ":"
        }
        if(amountPickerMinutes.value < 10){
            trip.tripTime += "0" + amountPickerMinutes.value.toString() + ":"
        } else {
            trip.tripTime += amountPickerMinutes.value.toString() + ":"
        }
        if(amountPickerSeconds.value < 10){
            trip.tripTime += "0" + amountPickerSeconds.value.toString()
        } else {
            trip.tripTime += amountPickerSeconds.value.toString()
        }



        app.trips.update(trip.copy())
    }
}
