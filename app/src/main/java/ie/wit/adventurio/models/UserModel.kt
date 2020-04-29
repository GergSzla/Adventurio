package ie.wit.adventurio.models

import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
@Parcelize
data class Account(
    var id:String = "" /*UUID*/,
    var weight: Double = 0.0,
    var firstName:String = "",
    var surname:String = "",
    var username:String = "",
    var Email: String = "",
    var image: Int = 0,
    var stepsGoal: Int = 0,
    var drivingDistanceGoal:Double = 0.0,
    var cyclingDistanceGoal:Double = 0.0,
    var distanceGoal:Double = 0.0,
    var loginUsed: String = "",
    var phoneNo: String = "",
    var vehicles : ArrayList<Vehicle> = ArrayList<Vehicle>()) : Parcelable

{
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "firstName" to firstName,
            "weight" to weight,
            "surname" to surname,
            "username" to username,
            "Email" to Email,
            "image" to image,
            "stepsGoal" to stepsGoal,
            "loginUsed" to loginUsed,
            "drivingDistanceGoal" to drivingDistanceGoal,
            "cyclingDistanceGoal" to cyclingDistanceGoal,
            "distanceGoal" to distanceGoal,
            "vehicles" to vehicles
        )
    }
}