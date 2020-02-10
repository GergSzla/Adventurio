package ie.wit.adventurio.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ie.wit.adventurio.R
import ie.wit.adventurio.models.WalkingTrip
import kotlinx.android.synthetic.main.card_trip.view.*

interface TripsListener {
    fun onTripClick(trip: WalkingTrip)
    fun onTripHold(trip: WalkingTrip)
}

class TripsAdapter constructor(private var trips: List<WalkingTrip>,
                             private val listener: TripsListener
) : RecyclerView.Adapter<TripsAdapter.MainHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(
            LayoutInflater.from(
                parent?.context
            ).inflate(R.layout.card_trip, parent, false)
        )

    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val trip = trips[holder.adapterPosition]
        holder.bind(trip, listener)
    }

    override fun getItemCount(): Int = trips.size

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(trip: WalkingTrip, listener: TripsListener) {
            var num = trip.tripDistance
            itemView.txtTripType.text = trip.tripType
            itemView.txtTotalSteps.text = (trip.tripSteps).toString()
            itemView.txtDistanceTotal.text = "%.2f".format(num) +"km"
            itemView.txtTimeElapsedTotal.text = trip.tripLength
            itemView.txtTimeAndDate.text = trip.DayOfWeek + ", " + trip.Date + ", " + trip.tripStartTime + " - " + trip.tripEndTime
            //itemView.mapView. = trip.make


            itemView.setOnClickListener {
                listener.onTripClick(trip)
            }
            itemView.setOnLongClickListener {
                listener.onTripHold(trip)
                true
            }
        }
    }
}