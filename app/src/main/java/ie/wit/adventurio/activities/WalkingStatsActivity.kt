package ie.wit.adventurio.activities

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupWindow
import ie.wit.adventurio.R
import ie.wit.adventurio.main.MainApp
import ie.wit.adventurio.models.Account
import ie.wit.adventurio.models.WalkingTrip
import kotlinx.android.synthetic.main.activity_statistics.*
import kotlinx.android.synthetic.main.logout_popup.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import java.util.ArrayList

class WalkingStatsActivity : AppCompatActivity() {

    lateinit var app: MainApp
    var user = Account()
    var totalSteps=0
    var totalDistance = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        app = application as MainApp

        if (intent.hasExtra("userLoggedIn")) {
            user = intent.extras.getParcelable<Account>("userLoggedIn")
        }
        var Trips = app.trips.getAllUserTrips() as ArrayList<WalkingTrip>


        txtTotalTrips.setText(Trips.size.toString())
        for(trip in Trips){
            totalSteps += trip.tripSteps
            totalDistance += trip.tripDistance

        }
        txtTotalStepsStats.setText(totalSteps.toString())
        txtCurrentStepsGoal.setText(user.stepsGoal.toString())
        txtAvgSteps.setText((totalSteps/Trips.size).toString())


        txtTotalDistStats.setText(totalDistance.toString())
        txtCurrentDistGoal.setText(user.distanceGoal.toString())
        txtAvgDist.setText((totalDistance/Trips.size).toString())

        txtTotalTripsPrecentage.setText(getPercentage(totalSteps.toDouble(),user.stepsGoal.toDouble()))
    }

    private fun getPercentage(v1:Double, v2:Double):String{
        return  "${"%.1f".format(v1 * 100f / v2)}%"
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        val inflater: LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        // Inflate a custom view using layout inflater
        val view = inflater.inflate(R.layout.logout_popup,null)
        // Initialize a new instance of popup window
        val popupWindow = PopupWindow(
            view, // Custom view to show in popup window
            LinearLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
            LinearLayout.LayoutParams.WRAP_CONTENT // Window height
        )


        toast("There is no back action")
        // Set an elevation for the popup window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.elevation = 10.0F
        }


        // If API level 23 or higher then execute the code
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            // Create a new slide animation for popup window enter transition
            val slideIn = Slide()
            slideIn.slideEdge = Gravity.TOP
            popupWindow.enterTransition = slideIn

            // Slide animation for popup window exit transition
            val slideOut = Slide()
            slideOut.slideEdge = Gravity.RIGHT
            popupWindow.exitTransition = slideOut
        }

        val btnCancelPopup = view.findViewById<Button>(R.id.btnCancelPopup)
        val btnLogoutPopup = view.findViewById<Button>(R.id.btnLogoutPopup)


        btnCancelPopup.setOnClickListener{
            // Dismiss the popup window
            popupWindow.dismiss()
        }

        btnLogoutPopup.setOnClickListener{
            finish();
            System.exit(0);
        }
        TransitionManager.beginDelayedTransition(root_layout)
        popupWindow.showAtLocation(
            root_layout, // Location to display popup window
            Gravity.CENTER, // Exact position of layout to display popup
            0, // X offset
            0 // Y offset
        )

        return
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        if (menu != null) menu.getItem(0).setVisible(true)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.item_trips -> {
                startActivityForResult(intentFor<TripsListActivity>().putExtra("userLoggedIn", user), 0)
            }
            R.id.item_profile -> {
                startActivityForResult(intentFor<ProfileActivity>().putExtra("userLoggedIn", user), 0)
            }
        }
        return super.onOptionsItemSelected(item)
    }


}
