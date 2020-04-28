package ie.wit.adventurio.fragments


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import ie.wit.adventurio.R
import ie.wit.adventurio.activities.RecordCyclingTripActivity
import ie.wit.adventurio.activities.RecordDrivingTripActivity
import ie.wit.adventurio.activities.RecordWalkingTripActivity
import ie.wit.adventurio.adapters.TripsAdapter
import ie.wit.adventurio.adapters.TripsListener
import ie.wit.adventurio.helpers.createLoader
import ie.wit.adventurio.helpers.hideLoader
import ie.wit.adventurio.helpers.showLoader
import ie.wit.adventurio.main.MainApp
import ie.wit.adventurio.models.Trip
import ie.wit.utils.SwipeToDeleteCallback
import ie.wit.utils.SwipeToEditCallback
import kotlinx.android.synthetic.main.fragment_trips_list.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info


class TripsListFragment : Fragment(), AnkoLogger, TripsListener {

    lateinit var app: MainApp
    var trip = Trip()
    var tripsList = ArrayList<Trip>()
    lateinit var root: View
    lateinit var loader : AlertDialog



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var viewTripFragment = ViewTripFragment()
        var tripDeleteUpdateFragment = WalkingTripsEditFragment()
        val bundleForTrips = Bundle()
        bundleForTrips.putParcelable("trip_key",trip)
        viewTripFragment.arguments = bundleForTrips
        tripDeleteUpdateFragment.arguments = bundleForTrips


        root = inflater.inflate(R.layout.fragment_trips_list, container, false)
        activity?.title = getString(R.string.tripsList)
        val animltr = AnimationUtils.loadAnimation(context,R.anim.swipe_lr)
        root.linearLayoutTrips.startAnimation(animltr)


        root.recordTripFab.setOnClickListener {
            val intent = Intent(activity, RecordWalkingTripActivity::class.java)
            startActivity(intent)
        }

        root.addTripFab.setOnClickListener{
            val animrtl = AnimationUtils.loadAnimation(context,R.anim.swipe_rl)
            root.linearLayoutTrips.startAnimation(animrtl)

            Handler().postDelayed({
                navigateTo(ManualTripFragment.newInstance())
            },500)

        }
        root.recordDrivingTripFab.setOnClickListener {
            val intent = Intent(activity, RecordDrivingTripActivity::class.java)
            startActivity(intent)
        }
        root.recordCyclingTripFab.setOnClickListener {
            val intent = Intent(activity, RecordCyclingTripActivity::class.java)
            startActivity(intent)
        }

        root.filterAll.setOnClickListener {
            root.recyclerView.adapter =
                TripsAdapter(tripsList, this@TripsListFragment)
            root.recyclerView.adapter?.notifyDataSetChanged()
            checkSwipeRefresh()
            root.filterStatus.text = "Showing All Trips (${tripsList.size})"

        }

        root.filterWalking.setOnClickListener {
            var filteredList = ArrayList<Trip>()

            tripsList.forEach{
                filteredList.add(it)
            }
            filteredList.removeIf({n -> n.tripType != "Walking"})
            root.recyclerView.adapter =
                TripsAdapter(filteredList, this@TripsListFragment)
            root.recyclerView.adapter?.notifyDataSetChanged()
            checkSwipeRefresh()
            root.filterStatus.text = "Showing Walking Trips (${filteredList.size})"

        }

        root.filterCycling.setOnClickListener {
            var filteredList = ArrayList<Trip>()

            tripsList.forEach{
                filteredList.add(it)
            }
            filteredList.removeIf({n -> n.tripType != "Cycling"})
            root.recyclerView.adapter =
                TripsAdapter(filteredList, this@TripsListFragment)
            root.recyclerView.adapter?.notifyDataSetChanged()
            checkSwipeRefresh()
            root.filterStatus.text = "Showing Cycling Trips (${filteredList.size})"

        }

        root.filterDriving.setOnClickListener {
            var filteredList = ArrayList<Trip>()

            tripsList.forEach{
                filteredList.add(it)
            }
            filteredList.removeIf({n -> n.tripType != "Driving"})
            root.recyclerView.adapter =
                TripsAdapter(filteredList, this@TripsListFragment)
            root.recyclerView.adapter?.notifyDataSetChanged()
            checkSwipeRefresh()
            root.filterStatus.text = "Showing Driving Trips (${filteredList.size})"

        }

        root.filterFavourites.setOnClickListener {
            var filteredList = ArrayList<Trip>()

            tripsList.forEach{
                filteredList.add(it)
            }
            filteredList.removeIf({n -> n.favourite != true})
            root.recyclerView.adapter =
                TripsAdapter(filteredList, this@TripsListFragment)
            root.recyclerView.adapter?.notifyDataSetChanged()
            checkSwipeRefresh()
            root.filterStatus.text = "Showing Favourites (${filteredList.size})"

        }

        root.recyclerView.layoutManager = LinearLayoutManager(activity)
        setSwipeRefresh()

        val swipeDeleteHandler = object : SwipeToDeleteCallback(activity!!) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = root.recyclerView.adapter as TripsAdapter
                adapter.removeAt(viewHolder.adapterPosition)
                deleteTrip((viewHolder.itemView.tag as Trip).dtID)
            }
        }
        val itemTouchDeleteHelper = ItemTouchHelper(swipeDeleteHandler)
        itemTouchDeleteHelper.attachToRecyclerView(root.recyclerView)


        val swipeEditHandler = object : SwipeToEditCallback(activity!!) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                var selectedTrip = viewHolder.itemView.tag as Trip
                if(selectedTrip.tripType == "Walking"){
                    navigateTo(WalkingTripsEditFragment.newInstance(selectedTrip))
                } else if(selectedTrip.tripType == "Cycling"){
                    navigateTo(CyclingTripsEditFragment.newInstance(selectedTrip))
                } else if(selectedTrip.tripType == "Driving"){
                    navigateTo(DrivingTripsEditFragment.newInstance(selectedTrip))
                }
            }
        }
        val itemTouchEditHelper = ItemTouchHelper(swipeEditHandler)
        itemTouchEditHelper.attachToRecyclerView(root.recyclerView)
        //root.recyclerView.adapter = TripsAdapter(app.trips.getAllUserTripsById(user.id),this)

        return root
    }

    fun deleteTrip(tripId: String?) {
        val uid = app.auth.currentUser!!.uid
        app.database.child("user-trips").child(uid).child(tripId!!)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.removeValue()
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
    }



    companion object {
        @JvmStatic
        fun newInstance() =
            TripsListFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    fun setSwipeRefresh() {
        root.swiperefresh.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                root.swiperefresh.isRefreshing = true
                getAllTrips(app.auth.currentUser!!.uid)
                root.filterStatus.text = "Showing All Trips (${tripsList.size})"
            }
        })
    }

    fun checkSwipeRefresh() {
        if (root.swiperefresh.isRefreshing) root.swiperefresh.isRefreshing = false
    }

    override fun onResume() {
        super.onResume()
        getAllTrips(app.auth.currentUser!!.uid)
    }

    fun getAllTrips(userId: String?) {
        loader = createLoader(activity!!)
        showLoader(loader, "Downloading Trips from Firebase")
        tripsList = ArrayList<Trip>()
        app.database.child("user-trips").child(userId!!).orderByChild("orderByID")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase Donation error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    hideLoader(loader)
                    val children = snapshot.children
                    children.forEach {
                        val trip = it.
                        getValue<Trip>(Trip::class.java)

                        tripsList.add(trip!!)
                        root.recyclerView.adapter =
                            TripsAdapter(tripsList, this@TripsListFragment)
                        root.recyclerView.adapter?.notifyDataSetChanged()
                        checkSwipeRefresh()
                        root.filterStatus.text = "Showing All Trips (${tripsList.size})"

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
    override fun onTripClick(trip: Trip) {
        navigateTo(ViewTripFragment.newInstance(trip))
    }
}
