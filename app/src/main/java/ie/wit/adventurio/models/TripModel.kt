package ie.wit.adventurio.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

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