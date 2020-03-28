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
    var uid:String = "",
    var firstName:String = "",
    var surname:String = "",
    var username:String = "",
    var Email: String = "",
    var image: String = "",
    var stepsGoal: Int = 0,
    var avgSteps:Int = 0,
    var avgDistance:Double = 0.0,
    var distanceGoal:Double = 0.0,
    var phoneNo: String = "",
    var vehicles : ArrayList<Vehicle> = ArrayList<Vehicle>(),
    var secondaryPWType:Int = 0 /*eg: None:0 PIN:1 or FINGERPRINT:2*/,
    var secondaryPW:String = ""/*1234 or FingerPrint*/) : Parcelable

{
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "firstName" to firstName,
            "uid" to uid,
            "surname" to surname,
            "username" to username,
            "Email" to Email,
            "image" to image,
            "stepsGoal" to stepsGoal,
            "avgSteps" to avgSteps,
            "avgDistance" to avgDistance,
            "distanceGoal" to distanceGoal,
            "vehicles" to vehicles,
            "secondaryPWType" to secondaryPWType,
            "secondaryPW" to secondaryPW
        )
    }
}