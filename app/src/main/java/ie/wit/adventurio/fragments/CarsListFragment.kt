package ie.wit.fragments


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
import ie.wit.adventurio.adapters.VehiclesAdapter
import ie.wit.adventurio.adapters.VehiclesListener
import ie.wit.adventurio.fragments.*
import ie.wit.adventurio.helpers.createLoader
import ie.wit.adventurio.helpers.hideLoader
import ie.wit.adventurio.main.MainApp
import ie.wit.adventurio.models.Trip
import ie.wit.adventurio.models.Vehicle
import ie.wit.utils.SwipeToDeleteCallback
import ie.wit.utils.SwipeToEditCallback
import kotlinx.android.synthetic.main.fragment_cars_list.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info


class CarsListFragment : Fragment(), AnkoLogger, VehiclesListener {

    lateinit var app: MainApp
    var trip = Trip()
    var carsList = ArrayList<Vehicle>()
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


        root = inflater.inflate(R.layout.fragment_cars_list, container, false)
        val animltr = AnimationUtils.loadAnimation(context,R.anim.swipe_lr)
        root.linearLayout.startAnimation(animltr)


        root.recyclerView2.layoutManager = LinearLayoutManager(activity)



        setSwipeRefresh()
        val swipeDeleteHandler = object : SwipeToDeleteCallback(activity!!) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = root.recyclerView2.adapter as VehiclesAdapter
                adapter.removeAt(viewHolder.adapterPosition)
                deleteCar((viewHolder.itemView.tag as Vehicle).pos)
            }
        }
        val itemTouchDeleteHelper = ItemTouchHelper(swipeDeleteHandler)
        itemTouchDeleteHelper.attachToRecyclerView(root.recyclerView2)


        val swipeEditHandler = object : SwipeToEditCallback(activity!!) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                navigateTo(CarEditFragment.newInstance(viewHolder.itemView.tag as Vehicle))
            }
        }
        val itemTouchEditHelper = ItemTouchHelper(swipeEditHandler)
        itemTouchEditHelper.attachToRecyclerView(root.recyclerView2)


        root.addCarFab.setOnClickListener {
            val animrtl = AnimationUtils.loadAnimation(context,R.anim.swipe_rl)
            root.linearLayout.startAnimation(animrtl)

            Handler().postDelayed({
                navigateTo(AddVehicleFragment.newInstance())
            },500)

        }

        return root
    }

    fun deleteCar(carId: String?) {
        val uid = app.auth.currentUser!!.uid
        app.database.child("user-stats").child(uid).child("vehicles").child(carId!!)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.removeValue()
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
    }

    fun setSwipeRefresh() {
        root.swiperefresh2.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                root.swiperefresh2.isRefreshing = true
                getAllCars(app.auth.currentUser!!.uid)
            }
        })
    }

    fun checkSwipeRefresh() {
        if (root.swiperefresh2.isRefreshing) root.swiperefresh2.isRefreshing = false
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            CarsListFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }



    override fun onResume() {
        super.onResume()
        getAllCars(app.auth.currentUser!!.uid)
    }

    fun getAllCars(userId: String?) {
        loader = createLoader(activity!!)
        carsList = ArrayList<Vehicle>()
        app.database.child("user-stats").child(userId!!).child("vehicles")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase Vehicle error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    hideLoader(loader)
                    val children = snapshot.children
                    children.forEach {
                        val car = it.
                        getValue<Vehicle>(Vehicle::class.java)

                        carsList.add(car!!)
                        root.recyclerView2.adapter =
                            VehiclesAdapter(carsList, this@CarsListFragment)
                        root.recyclerView2.adapter?.notifyDataSetChanged()
                        checkSwipeRefresh()
                        app.database.child("user-stats").child(userId).child("vehicles")
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
}
