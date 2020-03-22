package ie.wit.fragments


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import ie.wit.adventurio.R
import ie.wit.adventurio.adapters.TripsListener
import ie.wit.adventurio.activities.RecordTripActivity
import ie.wit.adventurio.adapters.TripsAdapter
import ie.wit.adventurio.fragments.TripsDeleteUpdateFragment
import ie.wit.adventurio.fragments.ViewTripFragment
import ie.wit.adventurio.helpers.createLoader
import ie.wit.adventurio.helpers.hideLoader
import ie.wit.adventurio.helpers.showLoader
import ie.wit.adventurio.main.MainApp
import ie.wit.adventurio.models.Account
import ie.wit.adventurio.models.WalkingTrip
import kotlinx.android.synthetic.main.fragment_trips_list.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info


class TripsListFragment : Fragment(), AnkoLogger, TripsListener {

    lateinit var app: MainApp
    var trip = WalkingTrip()
    var userTripsList: Account? = null
    lateinit var root: View
    lateinit var loader : AlertDialog



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


        root = inflater.inflate(R.layout.fragment_trips_list, container, false)

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

    override fun onResume() {
        super.onResume()
        getAllTrips(app.auth.currentUser!!.uid)
    }

    fun getAllTrips(userId: String?) {
        loader = createLoader(activity!!)
        showLoader(loader, "Downloading Trips from Firebase")
        val tripsList = ArrayList<WalkingTrip>()
        app.database.child("user-trips").child(userId!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase Donation error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    hideLoader(loader)
                    val children = snapshot.children
                    children.forEach {
                        val trip = it.
                        getValue<WalkingTrip>(WalkingTrip::class.java)

                        tripsList.add(trip!!)
                        root.recyclerView.adapter =
                            TripsAdapter(tripsList, this@TripsListFragment)
                        root.recyclerView.adapter?.notifyDataSetChanged()
                        //checkSwipeRefresh()

                        app.database.child("user-trips").child(userId)
                            .removeEventListener(this)
                    }
                }
            })
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
