package ie.wit.adventurio.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@IgnoreExtraProperties
@Parcelize
data class WalkingTrip (var tripID:String = "",
                        var tripType:String = "",
                        var tripDistance:Double = 0.0,
                        var tripSteps:Int = 0,
                        var tripLength:String ="",
                        var tripOwner:String = "",
                        var tripStartTime:String = "",
                        var tripEndTime:String = "",
                        var DayOfWeek:String = "", //eg Wed
                        var Date:String = "", //eg. 5 Feb
                        var dtID:String = "",
                        /*var startLat:Double = 0.0,
                        var startLng:Double = 0.0,
                        var endLat:Double = 0.0,
                        var endLng:Double = 0.0,*/
                        var lng: MutableList<String> = mutableListOf<String>(),
                        var lat: MutableList<String> = mutableListOf<String>(),

                        var zoom:Float = 15f) : Parcelable

/*data class DrivingTrip (var tripID:Int,
                        //var tripType:String,
                        var tripDistance:Double,
                        var fuelLevel:Int)*/

{
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "tripID" to tripID,
            "tripType" to tripType,
            "tripDistance" to tripDistance,
            "tripSteps" to tripSteps,
            "tripLength" to tripLength,
            "tripOwner" to tripOwner,
            "tripStartTime" to tripStartTime,
            "tripEndTime" to tripEndTime,
            "DayOfWeek" to DayOfWeek,
            "Date" to Date,
            "dtID" to dtID,
            "lng" to lng,
            "lat" to lat,
            "zoom" to zoom
        )
    }
}