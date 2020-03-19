package ie.wit.fragments


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import ie.wit.adventurio.R
import ie.wit.adventurio.adapters.TripsListener
import ie.wit.adventurio.activities.RecordTripActivity
import ie.wit.adventurio.fragments.TripsDeleteUpdateFragment
import ie.wit.adventurio.fragments.ViewTripFragment
import ie.wit.adventurio.main.MainApp
import ie.wit.adventurio.models.Account
import ie.wit.adventurio.models.WalkingTrip
import kotlinx.android.synthetic.main.fragment_trips_list.view.*


class TripsListFragment : Fragment(),TripsListener {

    lateinit var app: MainApp
    var trip = WalkingTrip()
    var userTripsList: Account? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp

        arguments?.let {
            userTripsList = it.getParcelable("user-profile-edit")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        var viewTripFragment = ViewTripFragment()
        var tripDeleteUpdateFragment = TripsDeleteUpdateFragment()
        val bundleForTrips = Bundle()
        bundleForTrips.putParcelable("trip_key",trip)
        viewTripFragment.arguments = bundleForTrips
        tripDeleteUpdateFragment.arguments = bundleForTrips


        var root = inflater.inflate(R.layout.fragment_trips_list, container, false)

        root.addTripFab.setOnClickListener {
            val intent = Intent(activity, RecordTripActivity::class.java).putExtra("user_key",userTripsList /*user*/)
            startActivity(intent)
        }

        root.recyclerView.layoutManager = LinearLayoutManager(activity)
        //root.recyclerView.adapter = TripsAdapter(app.trips.getAllUserTripsById(user.id),this)

        return root
    }

    companion object {
        @JvmStatic
        fun newInstance(user: Account) =
            TripsListFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("user-trips-list", user)
                }
            }
    }

    private fun navigateTo(fragment: Fragment) {
        val fragmentManager: FragmentManager = activity!!.supportFragmentManager
        fragmentManager.beginTransaction()
            .replace(R.id.homeFrame, fragment)
            .addToBackStack(null)
            .commit()
    }
    override fun onTripClick(trip: WalkingTrip) {
        navigateTo(ViewTripFragment.newInstance(trip))
    }

    override fun onTripHold(trip: WalkingTrip) {
        navigateTo(TripsDeleteUpdateFragment.newInstance(trip))
    }
}
