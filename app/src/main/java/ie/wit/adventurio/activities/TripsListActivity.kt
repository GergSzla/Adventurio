package ie.wit.adventurio.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import ie.wit.adventurio.R
import ie.wit.adventurio.main.MainApp
import ie.wit.adventurio.models.Account
import ie.wit.adventurio.models.WalkingTrip
import kotlinx.android.synthetic.main.activity_trips_list.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.startActivity

class TripsListActivity : AppCompatActivity(), TripsListener {

    lateinit var app: MainApp
    var trip = WalkingTrip()
    var user = Account()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trips_list)

        app = application as MainApp

        if (intent.hasExtra("userLoggedIn")) {
            user = intent.extras.getParcelable<Account>("userLoggedIn")
        }

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = TripsAdapter(app.trips.getAllUserTrips(), this)
        loadTrips()
    }

    private fun loadTrips(){
        showTrips(app.trips.getAllUserTrips())
    }

    private fun showTrips(trips: List<WalkingTrip>) {
        recyclerView.adapter = TripsAdapter(trips, this)
        recyclerView.adapter?.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_trips, menu)
        if (menu != null) menu.getItem(0).setVisible(true)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.item_cancel -> {
                finish()
            }
            R.id.item_add -> {
                startActivityForResult(intentFor<WalkingTripTrackingActivity>().putExtra("userLoggedIn", user), 0)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onTripClick(trip: WalkingTrip) {
        //view
        //startActivityForResult(intentFor<MTAActivity>().putExtra("challenge_edit ", challenge), 0)
    }

    override fun onTripHold(trip: WalkingTrip) {
        //edit
        //startActivityForResult(intentFor<MTAQuestionsListActivity>().putExtra("challenge_start ", challenge), 0) //change to MTAActivity? Start Challenge from there ?
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        loadTrips()
        super.onActivityResult(requestCode, resultCode, data)
    }
}
