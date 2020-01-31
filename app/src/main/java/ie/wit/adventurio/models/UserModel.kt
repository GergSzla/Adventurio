package ie.wit.adventurio.models

import kotlinx.android.parcel.Parcelize
import android.os.Parcelable


@Parcelize
data class Account (var id:String = "" /*UUID*/,
                    var firstName:String = "",
                    var surname:String = "",
                    var username:String = "",
                    var Email:String = "",
                    var Password:String = "",
                    var secondaryPWType:Int = 0 /*eg: None:0 PIN:1 or FINGERPRINT:2*/,
                    var secondaryPW:String = ""/*1234 or FingerPrint*/) : Parcelable
