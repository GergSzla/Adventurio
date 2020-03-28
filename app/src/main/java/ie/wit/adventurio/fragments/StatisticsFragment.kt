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
import ie.wit.adventurio.models.Trip
import kotlinx.android.synthetic.main.fragment_statistics.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.util.*


class StatisticsFragment : Fragment(), AnkoLogger {

    lateinit var app: MainApp
    internal var user: Account? = null  // declare user object outside onCreate Method
    lateinit var eventListener : ValueEventListener
    var ref = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().currentUser!!.uid)
    var userStats: Account? = null
    var UserTrips = ArrayList<Trip>()
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


        //var UserTrips= app.trips.getAllUserTripsById(user.id) as ArrayList<Trip>
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
                        getValue<Trip>(Trip::class.java)

                        UserTrips.add(trip!!)
                        app.database.child("user-trips").child(userId)
                            .removeEventListener(this)
                    }
                    updateStats()

                }
            })
    }


    private fun getPercentage(v1:Double, v2:Double):String{
        return  "${"%.0f".format(v1 * 100f / v2)}"
    }

    fun updateStats(){
        if(UserTrips.size > 0){
            root.txtTotalTrips.text = UserTrips.size.toString()
            for(trip in UserTrips){
                totalSteps += trip.tripSteps
                totalDistance += trip.tripDistance

            }
            root.txtTotalStepsStats.text = totalSteps.toString()
            root.txtCurrentStepsGoal.text = userStats!!.stepsGoal.toString()
            root.txtAvgSteps.text = (totalSteps/UserTrips.size).toString()


            root.txtTotalDistStats.text = "%.1f".format(totalDistance).toString()+ "km"
            root.txtCurrentDistGoal.text = "%.1f".format(userStats!!.distanceGoal).toString()+ "km"
            root.txtAvgDist.text = "%.1f".format(totalDistance/UserTrips.size).toString() + "km"


            if (userStats!!.stepsGoal != 0 ){
                root.txtTotalTripsPrecentage.text = getPercentage(totalSteps.toDouble(),userStats!!.stepsGoal.toDouble())+"%"
                root.progressBar.progress = getPercentage(totalSteps.toDouble(),userStats!!.stepsGoal.toDouble()).toInt()
            }
            if (userStats!!.distanceGoal != 0.0){
                root.progressBar7.progress = getPercentage(totalDistance,userStats!!.distanceGoal).toInt()
                root.txtTotalDistPrecentage.text = getPercentage(totalDistance,userStats!!.distanceGoal)+"%"
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
