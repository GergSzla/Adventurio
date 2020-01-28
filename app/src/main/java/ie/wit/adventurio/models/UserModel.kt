package ie.wit.adventurio.models


data class Account (var id:String /*UUID*/,
                    var firstName:String,
                    var surname:String,
                    var username:String,
                    var Email:String,
                    var Password:String,
                    var secondaryPWType:String /*eg: PIN or FINGERPRINT*/,
                    var secondaryPW:String/*1234 or FingerPrint*/)
