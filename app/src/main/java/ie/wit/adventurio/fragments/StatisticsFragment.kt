package ie.wit.adventurio.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import ie.wit.adventurio.R
import ie.wit.adventurio.main.MainApp
import ie.wit.adventurio.models.Account
import ie.wit.adventurio.models.WalkingTrip
import kotlinx.android.synthetic.main.fragment_statistics.*
import kotlinx.android.synthetic.main.fragment_statistics.view.*
import java.util.ArrayList



class StatisticsFragment : Fragment() {

    lateinit var app: MainApp
    var user = Account()
    var totalSteps=0
    var totalDistance = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp

        /*if (intent.hasExtra("userLoggedIn")) {
            user = intent.extras.getParcelable<Account>("userLoggedIn")
        }*/


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_statistics, container, false)
        activity?.title = getString(R.string.menu_stats)

        var Trips = app.trips.getAllUserTrips() as ArrayList<WalkingTrip>


        if(Trips.size > 0){
            root.txtTotalTrips.setText(Trips.size.toString())
            for(trip in Trips){
                totalSteps += trip.tripSteps
                totalDistance += trip.tripDistance

            }
            root.txtTotalStepsStats.setText(totalSteps.toString())
            root.txtCurrentStepsGoal.setText(user.stepsGoal.toString())
            root.txtAvgSteps.setText((totalSteps/Trips.size).toString())


            root.txtTotalDistStats.setText("%.1f".format(totalDistance).toString()+ "km")
            root.txtCurrentDistGoal.setText("%.1f".format(user.distanceGoal).toString()+ "km")
            root.txtAvgDist.setText("%.1f".format(totalDistance/Trips.size).toString() + "km")


            if (user.stepsGoal != 0 ){
                root.txtTotalTripsPrecentage.setText(getPercentage(totalSteps.toDouble(),user.stepsGoal.toDouble())+"%")
                root.progressBar.setProgress(getPercentage(totalSteps.toDouble(),user.stepsGoal.toDouble()).toInt())
            }
            if (user.distanceGoal != 0.0){
                root.progressBar7.setProgress(getPercentage(totalDistance,user.distanceGoal).toInt())
                root.txtTotalDistPrecentage.setText(getPercentage(totalDistance,user.distanceGoal)+"%")
            }
        }
        return root;
    }


    private fun getPercentage(v1:Double, v2:Double):String{
        return  "${"%.0f".format(v1 * 100f / v2)}"
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            StatisticsFragment().apply {
                arguments = Bundle().apply {}
            }
    }
}