package ie.wit.adventurio.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@IgnoreExtraProperties
@Parcelize
data class Vehicle (var vehicleId:String = "",
                    var pos : String = "",
                    var vehicleOwner : String = "",
                    var currentOdometer : Int = 0,
                    var vehicleName : String = "",
                    var vehicleReg:String="",
                    var vehicleBrand : String ="",
                    var vehicleModel : String = "",
                    var vehicleYear : String = "",
                    var tankCapacity : Double = 0.0,
                    var fuelType : String = "",
                    var vehicleImage : String = "") : Parcelable

{
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "vehicleId" to vehicleId,
            "pos" to pos,
            "vehicleOwner" to vehicleOwner,
            "currentOdometer" to currentOdometer,
            "vehicleName" to vehicleName,
            "vehicleReg" to vehicleReg,
            "vehicleBrand" to vehicleBrand,
            "vehicleModel" to vehicleModel,
            "vehicleYear" to vehicleYear,
            "tankCapacity" to tankCapacity,
            "fuelType" to fuelType,
            "vehicleImage" to vehicleImage
        )
    }
}