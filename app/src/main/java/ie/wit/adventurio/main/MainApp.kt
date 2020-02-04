package ie.wit.adventurio.main

import android.app.Application
import ie.wit.adventurio.models.TripJsonStore
import ie.wit.adventurio.models.TripStore
import ie.wit.adventurio.models.UserJsonStore
import ie.wit.adventurio.models.UserStore
import org.jetbrains.anko.AnkoLogger

class MainApp : Application(), AnkoLogger {

    lateinit var users: UserStore
    lateinit var trips: TripStore


    override fun onCreate() {
        super.onCreate()
        users = UserJsonStore(applicationContext)
        trips = TripJsonStore(applicationContext)
    }
}