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


val WALKINGTRIP_JSON_FILE = "trips.json"
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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun create(walkingTrip: WalkingTrip) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun update(walkingTrip: WalkingTrip) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(walkingTrip: WalkingTrip) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
