package ie.wit.adventurio.main

import android.app.Application
import android.net.Uri
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference

class MainApp : Application() {

    lateinit var auth: FirebaseAuth
    lateinit var database: DatabaseReference
    lateinit var googleSignInClient: GoogleSignInClient
    lateinit var storage: StorageReference
    lateinit var userImage: Uri
    //lateinit var users: UserStore
    //lateinit var trips: TripStore


    override fun onCreate() {
        super.onCreate()
        //users = UserJsonStore(applicationContext)
        //trips = TripJsonStore(applicationContext)
    }
}