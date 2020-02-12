package ie.wit.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ie.wit.adventurio.R
import ie.wit.adventurio.adapters.TripsAdapter
import ie.wit.adventurio.adapters.TripsListener
import ie.wit.adventurio.main.MainApp
import ie.wit.adventurio.models.Account
import ie.wit.adventurio.models.WalkingTrip
import kotlinx.android.synthetic.main.fragment_trips_list.view.*


class TripsListFragment : Fragment(),TripsListener {

    lateinit var app: MainApp
    var trip = WalkingTrip()
    var user = Account()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val bundle = arguments
        if (bundle != null) {
            user = bundle.getParcelable("user_key")
        }

        var root = inflater.inflate(R.layout.fragment_trips_list, container, false)

        root.recyclerView.setLayoutManager(LinearLayoutManager(activity))
        root.recyclerView.adapter = TripsAdapter(app.trips.getAllUserTrips(),this)

        return root
    }

    companion object {
        @JvmStatic
        fun newInstance(account: Account) =
            TripsListFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("user_key", account)
                }
            }
    }

    override fun onTripClick(trip: WalkingTrip) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onTripHold(trip: WalkingTrip) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
