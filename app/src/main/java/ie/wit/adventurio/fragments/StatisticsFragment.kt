package ie.wit.adventurio.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
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
import ie.wit.adventurio.models.WalkingTrip
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_statistics.*
import kotlinx.android.synthetic.main.fragment_statistics.view.*
import kotlinx.android.synthetic.main.nav_header_home.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.util.*


class StatisticsFragment : Fragment(), AnkoLogger {

    lateinit var app: MainApp
    internal var user: Account? = null  // declare user object outside onCreate Method
    lateinit var eventListener : ValueEventListener
    var ref = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().currentUser!!.uid)
    var userStats: Account? = null
    var UserTrips = ArrayList<WalkingTrip>()
    lateinit var loader : AlertDialog
    lateinit var root: View


    var totalSteps=0
    var totalDistance = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp



        /**/

        arguments?.let {
            userStats = it.getParcelable("user-stats")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_statistics, container, false)
        activity?.title = getString(R.string.menu_stats)



        /*val bundle = arguments
        if (bundle != null) {
            user = bundle.getParcelable("user_key")
        }*/


        //var UserTrips= app.trips.getAllUserTripsById(user.id) as ArrayList<WalkingTrip>
        getAllTrips(app.auth.currentUser!!.uid)


        return root
    }


    fun getAllTrips(userId: String?) {
        loader = createLoader(activity!!)
        showLoader(loader, "Downloading Your Trips . . . ")
        app.database.child("user-trips").child(userId!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase Trip error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    hideLoader(loader)
                    val children = snapshot.children
                    children.forEach {
                        val trip = it.
                        getValue<WalkingTrip>(WalkingTrip::class.java)

                        UserTrips.add(trip!!)
                        updateStats()
                        app.database.child("user-trips").child(userId)
                            .removeEventListener(this)
                    }
                }
            })
    }


    private fun getPercentage(v1:Double, v2:Double):String{
        return  "${"%.0f".format(v1 * 100f / v2)}"
    }

    fun updateStats(){
        if(UserTrips.size > 0){
            root.txtTotalTrips.setText(UserTrips.size.toString())
            for(trip in UserTrips){
                totalSteps += trip.tripSteps
                totalDistance += trip.tripDistance

            }
            root.txtTotalStepsStats.setText(totalSteps.toString())
            root.txtCurrentStepsGoal.setText(userStats!!.stepsGoal.toString())
            root.txtAvgSteps.setText((totalSteps/UserTrips.size).toString())


            root.txtTotalDistStats.setText("%.1f".format(totalDistance).toString()+ "km")
            root.txtCurrentDistGoal.setText("%.1f".format(userStats!!.distanceGoal).toString()+ "km")
            root.txtAvgDist.setText("%.1f".format(totalDistance/UserTrips.size).toString() + "km")


            if (userStats!!.stepsGoal != 0 ){
                root.txtTotalTripsPrecentage.setText(getPercentage(totalSteps.toDouble(),userStats!!.stepsGoal.toDouble())+"%")
                root.progressBar.setProgress(getPercentage(totalSteps.toDouble(),userStats!!.stepsGoal.toDouble()).toInt())
            }
            if (userStats!!.distanceGoal != 0.0){
                root.progressBar7.setProgress(getPercentage(totalDistance,userStats!!.distanceGoal).toInt())
                root.txtTotalDistPrecentage.setText(getPercentage(totalDistance,userStats!!.distanceGoal)+"%")
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(user: Account) =
            StatisticsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("user-stats", user)
                }
            }
    }
}
