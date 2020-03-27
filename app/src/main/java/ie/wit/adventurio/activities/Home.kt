package ie.wit.adventurio.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ie.wit.adventurio.R
import ie.wit.adventurio.fragments.ProfileFragment
import ie.wit.adventurio.fragments.StatisticsFragment
import ie.wit.adventurio.helpers.readImage
import ie.wit.adventurio.main.MainApp
import ie.wit.adventurio.models.Account
import ie.wit.fragments.TripsListFragment
import kotlinx.android.synthetic.main.activity_record_trip.*
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.fragment_profile_edit.*
import kotlinx.android.synthetic.main.home.*
import kotlinx.android.synthetic.main.nav_header_home.view.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.startActivity


class Home : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {

    lateinit var app: MainApp

    lateinit var ft: FragmentTransaction
    var user = Account()
    lateinit var eventListener : ValueEventListener
    val IMAGE_REQUEST = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)
        setSupportActionBar(toolbar)
        app = application as MainApp
        /*if (intent.hasExtra("user_key")) {
            user = intent.extras.getParcelable<Account>("user_key")
        }*/



        navView.setNavigationItemSelectedListener(this)


        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.getHeaderView(0).nav_header_email.text = app.auth.currentUser?.email





        ft = supportFragmentManager.beginTransaction()

        //var profileFragment = ProfileFragment()
        /*var tripsListFragment = TripsListFragment()

        val bundle = Bundle()
        bundle.putParcelable("user_key",user)
        profileFragment.arguments = bundle
        statsFragment.arguments = bundle
        tripsListFragment.arguments = bundle*/



    }

    override fun onResume() {
        super.onResume()
        getUser(app.auth.currentUser?.uid)
    }



    fun getUser(userId : String?){
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val rootRef = FirebaseDatabase.getInstance().reference
        val uidRef = rootRef.child("user-stats").child(uid)
        eventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                user = dataSnapshot.getValue(Account::class.java)!!

                var statsFragment = StatisticsFragment.newInstance(user)
                //fragment.arguments = bundle

                ft.replace(R.id.homeFrame, statsFragment)
                ft.commit()
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        uidRef.addListenerForSingleValueEvent(eventListener)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.nav_statistics -> {
                navigateTo(StatisticsFragment.newInstance(user))
            }

            R.id.nav_trips_list -> {
                navigateTo(TripsListFragment.newInstance())
            }

            R.id.nav_record_trips -> {
                startActivity(intentFor<RecordTripActivity>())
            }

            R.id.nav_profile -> {
                navigateTo(ProfileFragment.newInstance(user))
            }
            R.id.nav_logout -> {
                signOut()
            }

        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }





    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START)
        else
            navigateTo(StatisticsFragment.newInstance(user))

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            IMAGE_REQUEST -> {
                if (data != null) {
                    user.image = data.getData().toString()
                    profImage.setImageBitmap(readImage(this, resultCode, data))
                    addImage.setText(R.string.btnChangeImage)
                }
            }
        }
    }

    private fun signOut(){
        finish()
        app.auth.signOut()
        startActivity<LoginActivity>()
    }

    private fun navigateTo(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.homeFrame, fragment)
            .addToBackStack(null)
            .commit()
    }

}
