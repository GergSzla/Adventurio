package ie.wit.adventurio.models

interface TripStore {
    fun getAllUserTrips(): List<WalkingTrip>
    fun getAllUserTripsById(id:String): List<WalkingTrip>

    //fun getUsersTrips(id:String): List<WalkingTrip>
    fun create(walkingTrip: WalkingTrip)
    fun update(walkingTrip: WalkingTrip)
    fun delete(walkingTrip: WalkingTrip)
}