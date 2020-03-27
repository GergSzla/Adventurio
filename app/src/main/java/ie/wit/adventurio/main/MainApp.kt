package ie.wit.adventurio.main

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

class MainApp : Application() {

    lateinit var auth: FirebaseAuth
    lateinit var database: DatabaseReference
    //lateinit var users: UserStore
    //lateinit var trips: TripStore


    override fun onCreate() {
        super.onCreate()
        //users = UserJsonStore(applicationContext)
        //trips = TripJsonStore(applicationContext)
    }
}