package ie.wit.adventurio.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
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
import ie.wit.adventurio.fragments.AddVehicleFragment
import ie.wit.adventurio.fragments.ManualTripFragment
import ie.wit.adventurio.fragments.ProfileFragment
import ie.wit.adventurio.fragments.StatisticsFragment
import ie.wit.adventurio.helpers.readImage
import ie.wit.adventurio.main.MainApp
import ie.wit.adventurio.models.Account
import ie.wit.fragments.CarsListFragment
import ie.wit.fragments.TripsListFragment
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)
        setSupportActionBar(toolbar)
        app = application as MainApp
        app.auth = FirebaseAuth.getInstance()


        navView.setNavigationItemSelectedListener(this)


        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.getHeaderView(0).nav_header_email.text = app.auth.currentUser?.email





        ft = supportFragmentManager.beginTransaction()

        navigateTo(StatisticsFragment.newInstance(user))




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

            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        uidRef.addListenerForSingleValueEvent(eventListener)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.nav_profile -> {
                navigateTo(ProfileFragment.newInstance(user))
            }
            R.id.nav_statistics -> {
                navigateTo(StatisticsFragment.newInstance(user))
            }


            //////////////////////////
            R.id.nav_record_walking -> {
                startActivity(intentFor<RecordWalkingTripActivity>())
            }

            R.id.nav_record_driving -> {
                startActivity(intentFor<RecordDrivingTripActivity>())
            }

            R.id.nav_record_cycling -> {
                startActivity(intentFor<RecordCyclingTripActivity>())
            }

            R.id.nav_manual_trips -> {
                navigateTo(ManualTripFragment.newInstance())
            }

            R.id.nav_trips_list -> {
                navigateTo(TripsListFragment.newInstance())
            }


            ///////////////////////////
            R.id.nav_cars_list -> {
                navigateTo(CarsListFragment.newInstance())
            }
            R.id.nav_cars_add -> {
                navigateTo(AddVehicleFragment.newInstance())
            }
            /////////////////////////
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
