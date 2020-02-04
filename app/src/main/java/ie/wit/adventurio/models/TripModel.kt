package ie.wit.adventurio.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WalkingTrip (var tripID:String = "",
                        var tripType:String = "",
                        var tripDistance:Double = 0.0,
                        var tripSteps:Int = 0,
                        var tripTime:String ="",
                        var tripOwner:String = "",
                        /*var startLat:Double = 0.0,
                        var startLng:Double = 0.0,
                        var endLat:Double = 0.0,
                        var endLng:Double = 0.0,*/
                        var trip: String = "",
                        var zoom:Float = 0F) : Parcelable

/*data class DrivingTrip (var tripID:Int,
                        //var tripType:String,
                        var tripDistance:Double,
                        var fuelLevel:Int)*/