package ie.wit.adventurio.models

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import ie.wit.adventurio.helpers.exists
import ie.wit.adventurio.helpers.read
import ie.wit.adventurio.helpers.write
import org.jetbrains.anko.AnkoLogger
import java.util.ArrayList


val WALKINGTRIP_JSON_FILE = "walking_trips.json"
val gsonTripBuilder = GsonBuilder().setPrettyPrinting().create()
val listTripType = object : TypeToken<ArrayList<WalkingTrip>>() {}.type

class TripJsonStore : TripStore, AnkoLogger {

    val context: Context
    var trips = mutableListOf<WalkingTrip>()


    constructor (context: Context) {
        this.context = context
        if (exists(context, WALKINGTRIP_JSON_FILE)) {
            deserialize()
        }
    }

    override fun getAllUserTrips(): List<WalkingTrip> {
           return trips
    }

    /*override fun getUsersTrips(id:String): List<WalkingTrip> {
        val TripsList = getAllUserTrips() as ArrayList<WalkingTrip>
        var allTrips = TripsList.filter{ p -> p.tripOwner == id }

        return allTrips
    }*/


    override fun create(walkingTrip: WalkingTrip) {
        trips.add(walkingTrip)
        serialize()     }

    override fun update(walkingTrip: WalkingTrip) {
        val TripsList = getAllUserTrips() as ArrayList<WalkingTrip>
        var foundTrip: WalkingTrip? = TripsList.find { p -> p.tripID == walkingTrip.tripID }
        if (foundTrip != null){
            foundTrip.tripType = walkingTrip.tripType
            foundTrip.tripDistance = walkingTrip.tripDistance
            foundTrip.tripSteps = walkingTrip.tripSteps
            foundTrip.tripLength = walkingTrip.tripLength
            foundTrip.zoom = walkingTrip.zoom
        }
        serialize()
    }

    override fun delete(walkingTrip: WalkingTrip) {
        trips.remove(walkingTrip)
        serialize()
    }

    private fun serialize() {
        val jsonString = gsonTripBuilder.toJson(trips, listTripType)
        write(context, WALKINGTRIP_JSON_FILE, jsonString)
    }


    private fun deserialize() {
        val jsonString = read(context, WALKINGTRIP_JSON_FILE)
        trips = Gson().fromJson(jsonString, listTripType)
    }

}
