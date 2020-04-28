package ie.wit.adventurio.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnScrollChangedListener
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
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
import kotlinx.android.synthetic.main.fragment_statistics.*
import kotlinx.android.synthetic.main.fragment_statistics.view.*
import kotlinx.android.synthetic.main.fragment_statistics.view.scrollViewStats
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.util.*


class StatisticsFragment : Fragment(), AnkoLogger {

    lateinit var app: MainApp
    internal var user: Account? = null  // declare user object outside onCreate Method
    lateinit var eventListener : ValueEventListener
    var ref = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().currentUser!!.uid)
    var userStats: Account? = null
    var UserTrips =  ArrayList<Trip>()
    var WalkingTrips = ArrayList<Trip>()
    var DrivingTrips = ArrayList<Trip>()
    var CyclingTrips = ArrayList<Trip>()
    lateinit var loader : AlertDialog
    lateinit var root: View


    //walking
    var totalSteps=0
    var totalDistance = 0.0
    var totalCaloriesWalking = 0.0
    //car
    var totalDistanceCar = 0.0
    var avgSpeed = 0.0

    //cycling
    var totalCaloriesCycling = 0.0
    var totalDistanceCycling = 0.0
    var avgSpeedCycling = 0.0



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
        val anim = AnimationUtils.loadAnimation(context,R.anim.slide_down)
        root.statsLayout.startAnimation(anim)
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
        UserTrips.forEach{
            WalkingTrips.add(it)
            DrivingTrips.add(it)
            CyclingTrips.add(it)
        }


        WalkingTrips.removeIf { n -> n.tripType != "Walking"}
        DrivingTrips.removeIf { n -> n.tripType != "Driving"}
        CyclingTrips.removeIf { n -> n.tripType != "Cycling"}

        root.speedViewcycling.setMinMaxSpeed(0F, 70F)
        root.speedViewcycling.sections[0].color = Color.parseColor("#6a6a6a")
        root.speedViewcycling.sections[1].color = Color.parseColor("#3d3232")
        root.speedViewcycling.sections[2].color = Color.parseColor("#321919")
        root.speedViewcycling.withTremble = false

        root.speedView.setMinMaxSpeed(0F, 140F)
        root.speedView.sections[0].color = Color.parseColor("#6a6a6a")
        root.speedView.sections[1].color = Color.parseColor("#3d3232")
        root.speedView.sections[2].color = Color.parseColor("#321919")
        root.speedView.withTremble = false

        //Update Walking Stats
        if(WalkingTrips.size > 0){
            root.txtTotalTrips.text = WalkingTrips.size.toString()
            for(trip in WalkingTrips){
                totalSteps += trip.tripSteps
                totalDistance += trip.tripDistance
                totalCaloriesWalking += trip.caloriesBurned
            }
            root.txtTotalStepsStats.text = totalSteps.toString()
            root.txtCurrentStepsGoal.text = userStats!!.stepsGoal.toString()
            root.txtAvgSteps.text = (totalSteps/WalkingTrips.size).toString()
            root.txtTotalCalories.text = "%.1f".format(totalCaloriesWalking).toString() + "kcal"
            root.txtAvgCalories.text = "%.1f".format(totalCaloriesWalking/WalkingTrips.size).toString() + "kcal"

            root.txtTotalDistStats.text = "%.1f".format(totalDistance).toString()+ "km"
            root.txtCurrentDistGoal.text = "%.1f".format(userStats!!.distanceGoal).toString()+ "km"
            root.txtAvgDist.text = "%.1f".format(totalDistance/WalkingTrips.size).toString() + "km"


            if (userStats!!.stepsGoal != 0 ){
                root.txtTotalTripsPrecentage.text = getPercentage(totalSteps.toDouble(),userStats!!.stepsGoal.toDouble())+"%"
                root.progressBar.progress = getPercentage(totalSteps.toDouble(),userStats!!.stepsGoal.toDouble()).toInt()
            }
            if (userStats!!.distanceGoal != 0.0){
                root.progressBar7.progress = getPercentage(totalDistance,userStats!!.distanceGoal).toInt()
                root.txtTotalDistPrecentage.text = getPercentage(totalDistance,userStats!!.distanceGoal)+"%"
            }
        }

        //Update Driving Stats
        if(DrivingTrips.size > 0){
            root.txtTotalDriving.text = DrivingTrips.size.toString()
            for(trip in DrivingTrips){
                totalDistanceCar += trip.tripDistance
                avgSpeed += trip.averageSpeed.toDouble()
            }



            root.speedView.speedTo("%.1f".format(avgSpeed/DrivingTrips.size).toFloat())
            root.txtAvgSpeedDriving.text = "%.1f".format(avgSpeed/DrivingTrips.size).toString()
            root.txtTotalDistanceDriving.text = "%.1f".format(totalDistanceCar).toString()
            root.txtDistGoalDriving.text = "%.1f".format(userStats!!.drivingDistanceGoal).toString()+ "km"
            root.txtAverageDistDriving.text = "%.1f".format(totalDistanceCar/DrivingTrips.size).toString() + "km"
            if (userStats!!.drivingDistanceGoal != 0.0){
                root.drivingdistpb.progress = getPercentage(totalDistanceCar,userStats!!.drivingDistanceGoal).toInt()
            }
        }
        //Update Cycling Stats
        if(CyclingTrips.size > 0){
            root.txtTotalCycling.text = CyclingTrips.size.toString()
            for(trip in CyclingTrips){
                totalDistanceCycling += trip.tripDistance
                avgSpeedCycling += trip.averageSpeed.toDouble()
                totalCaloriesCycling += trip.caloriesBurned
            }



            root.speedViewcycling.speedTo("%.1f".format(avgSpeedCycling/CyclingTrips.size).toFloat())
            root.txtAvgSpeedCycling.text = "%.1f".format(avgSpeedCycling/CyclingTrips.size).toString()
            root.txtTotalDistanceCycling.text = "%.1f".format(totalDistanceCycling).toString()
            root.txtDistGoalCycling.text = "%.1f".format(userStats!!.cyclingDistanceGoal).toString()+ "km"
            root.txtAverageDistCycling.text = "%.1f".format(totalDistanceCycling/CyclingTrips.size).toString() + "km"
            if (userStats!!.cyclingDistanceGoal != 0.0){
                root.cyclingdistpb.progress = getPercentage(totalDistanceCycling,userStats!!.cyclingDistanceGoal).toInt()
            }
            root.txtTotalCyclingCalories.text = "%.1f".format(totalCaloriesCycling).toString() + "kcal"
            root.txtAvgCyclingCalories.text = "%.1f".format(totalCaloriesCycling/CyclingTrips.size).toString() + "kcal"
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
