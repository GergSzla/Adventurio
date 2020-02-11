package ie.wit.adventurio.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import ie.wit.adventurio.R
import ie.wit.adventurio.fragments.ProfileFragment
import ie.wit.adventurio.fragments.RecordTripFragment
import ie.wit.adventurio.fragments.StatisticsFragment
import ie.wit.adventurio.models.Account
import ie.wit.fragments.TripsListFragment
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.home.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast


class Home : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {

    lateinit var ft: FragmentTransaction
    private val USER_KEY = "user_key"
    var user = Account()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)
        setSupportActionBar(toolbar)

        if (intent.hasExtra("userLoggedIn")) {
            user = intent.extras.getParcelable<Account>("userLoggedIn")

        }

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action",
                Snackbar.LENGTH_LONG).setAction("Action", null).show()
        }

        navView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        ft = supportFragmentManager.beginTransaction()

        var profileFragment = ProfileFragment()
        val bundle = Bundle()
        bundle.putParcelable("user_key",user)
        profileFragment.arguments = bundle
        profileFragment.user = user

        var StatsFragment = StatisticsFragment.newInstance()
        //fragment.arguments = bundle

        ft.replace(R.id.homeFrame, StatsFragment)
        ft.commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.nav_statistics -> navigateTo(StatisticsFragment.newInstance())
            R.id.nav_trips_list -> navigateTo(TripsListFragment.newInstance())
            R.id.nav_record_trips -> navigateTo(RecordTripFragment.newInstance())
            R.id.nav_profile -> navigateTo(ProfileFragment.newInstance())
            R.id.nav_logout -> startActivity<LoginActivity>()

            else -> toast("You Selected Something Else")
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.nav_statistics -> toast("You Selected Statistics")
            R.id.nav_trips_list -> toast("You Selected Trips List")
            R.id.nav_record_trips -> toast("You Selected Record Trip")
            R.id.nav_profile -> toast("You Selected Profile")
            R.id.nav_logout -> toast("Logging out. . . ")
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START)
        else
            super.onBackPressed()
    }

    private fun navigateTo(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.homeFrame, fragment)
            .addToBackStack(null)
            .commit()
    }
}
