package ie.wit.adventurio.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize


//change to super and sub classes Trip -> Walking,Cycling,Driving
@IgnoreExtraProperties
@Parcelize
data class Trip (var tripID:String = "",
                 var tripName:String = "",
                 var tripType:String = "",
                 var tripDistance:Double = 0.0,
                 var tripSteps:Int = 0,
                 var tripLength:String ="",
                 var tripOwner:String = "",
                 var averageSpeed: String ="",
                 var caloriesBurned: Double = 0.0,
                 var vehicleUsed: String = "",
                 var tripStartTime:String = "",
                 var tripEndTime:String = "",
                 var DayOfWeek:String = "", //eg Wed
                 var Date:String = "", //eg. 5 Feb
                 var dtID:String = "",
                 var orderByID: Long = 0,
                 var favourite:Boolean = false,
                        /*var startLat:Double = 0.0,
                        var startLng:Double = 0.0,
                        var endLat:Double = 0.0,
                        var endLng:Double = 0.0,*/
                 var lng: MutableList<String> = mutableListOf<String>(),
                 var lat: MutableList<String> = mutableListOf<String>(),

                 var zoom:Float = 15f) : Parcelable


{
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "tripID" to tripID,
            "tripName" to tripName,
            "tripType" to tripType,
            "tripDistance" to tripDistance,
            "tripSteps" to tripSteps,
            "tripLength" to tripLength,
            "tripOwner" to tripOwner,
            "averageSpeed" to averageSpeed,
            "caloriesBurned" to caloriesBurned,
            "vehicleUsed" to vehicleUsed,
            "tripStartTime" to tripStartTime,
            "tripEndTime" to tripEndTime,
            "DayOfWeek" to DayOfWeek,
            "Date" to Date,
            "dtID" to dtID,
            "orderByID" to orderByID,
            "favourite" to favourite,
            "lng" to lng,
            "lat" to lat,
            "zoom" to zoom
        )
    }
}