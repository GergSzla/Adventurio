package ie.wit.adventurio.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import ie.wit.adventurio.R
import ie.wit.adventurio.models.Trip
import ie.wit.adventurio.models.Vehicle
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.card_trip.view.*
import kotlinx.android.synthetic.main.card_vehicle.view.*

interface VehiclesListener {
}

class VehiclesAdapter constructor(var vehicles: ArrayList<Vehicle>,
                               private val listener: VehiclesListener) : RecyclerView.Adapter<VehiclesAdapter.MainHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(
            LayoutInflater.from(
                parent?.context
            ).inflate(R.layout.card_vehicle, parent, false)
        )

    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val vehicle = vehicles[holder.adapterPosition]
        holder.bind(vehicle, listener)
    }

    override fun getItemCount(): Int = vehicles.size

    fun removeAt(position: Int) {
        vehicles.removeAt(position)
        notifyItemRemoved(position)
    }

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(vehicle: Vehicle, listener: VehiclesListener) {
            itemView.tag = vehicle
            itemView.txtVehicleName.text = vehicle.vehicleName
            itemView.txtVehicleBrand.text = vehicle.vehicleBrand
            itemView.txtVehicleModel.text = vehicle.vehicleModel
            itemView.txtVehicleOdo.text = vehicle.currentOdometer.toString() + " km"

            if (vehicle.vehicleImage == ""){
                itemView.imgVehicle.setImageResource(R.drawable.ic_directions_car_black_24dp)
            } else {
                var ref = FirebaseStorage.getInstance().getReference("vehicleImages/${vehicle.vehicleId}.jpg")
                ref.downloadUrl.addOnSuccessListener {
                    Picasso.get().load(it)
                        .fit()
                        .centerInside()
                        .transform(RoundedCornersTransformation(50,0))
                        .into(itemView.imgVehicle)
                }
            }

        }
    }
}