package ie.wit.adventurio.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ie.wit.adventurio.R
import ie.wit.adventurio.models.Trip
import kotlinx.android.synthetic.main.card_trip.view.*

interface TripsListener {
    fun onTripClick(trip: Trip)

}

class TripsAdapter constructor(var trips: ArrayList<Trip>,
                               private val listener: TripsListener) : RecyclerView.Adapter<TripsAdapter.MainHolder>() {


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

    fun removeAt(position: Int) {
        trips.removeAt(position)
        notifyItemRemoved(position)
    }

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(trip: Trip, listener: TripsListener) {
            var num = trip.tripDistance
            itemView.tag = trip
            itemView.txtTripName.text = trip.tripName

            if (trip.tripType =="Walking"){
                itemView.imageView2.setImageResource(R.drawable.ic_walking_category)
                itemView.txtAvgSpeed.isVisible = false
                itemView.txtTotalSteps.isVisible = true
                itemView.txtTotalSteps.text = (trip.tripSteps).toString() + " Steps"
            } else if (trip.tripType =="Driving"){
                itemView.imageView2.setImageResource(R.drawable.ic_directions_car_black_24dp)
                itemView.txtAvgSpeed.isVisible = true
                itemView.txtTotalSteps.isVisible = false
                itemView.txtAvgSpeed.text = trip.averageSpeed + "km/h"

            } else if (trip.tripType =="Cycling"){
                itemView.imageView2.setImageResource(R.drawable.ic_cycling_category)
                itemView.txtAvgSpeed.isVisible = true
                itemView.txtTotalSteps.isVisible = false
                itemView.txtAvgSpeed.text = trip.averageSpeed + "km/h"

            }
            itemView.txtDistanceTotal.text = "%.2f".format(num) +" km"
            itemView.txtTimeElapsedTotal.text = trip.tripLength
            itemView.txtTimeAndDate.text = trip.DayOfWeek + ", " + trip.Date + ", " + trip.tripStartTime + " - " + trip.tripEndTime
            //itemView.mapView. = trip.make


            itemView.setOnClickListener {
                listener.onTripClick(trip)
            }
        }
    }
}