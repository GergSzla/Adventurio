package ie.wit.adventurio.models

data class WalkingTrip (var tripID:Int,
                        //var tripType:String,
                        var tripDistance:Double,
                        var tripSteps:Int)

data class DrivingTrip (var tripID:Int,
                        //var tripType:String,
                        var tripDistance:Double,
                        var fuelLevel:Int)