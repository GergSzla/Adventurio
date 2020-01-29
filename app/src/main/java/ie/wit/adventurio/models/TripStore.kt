package ie.wit.adventurio.models

interface TripStore {
    fun getAllUserTrips(): List<WalkingTrip>
    fun create(walkingTrip: WalkingTrip)
    fun update(walkingTrip: WalkingTrip)
    fun delete(walkingTrip: WalkingTrip)
}