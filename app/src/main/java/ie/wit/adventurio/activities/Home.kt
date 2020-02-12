package ie.wit.adventurio.activities

import android.app.ActionBar
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.navigation.NavigationView
import ie.wit.adventurio.R
import ie.wit.adventurio.fragments.ProfileFragment
import ie.wit.adventurio.fragments.RecordTripFragment
import ie.wit.adventurio.fragments.StatisticsFragment
import ie.wit.adventurio.helpers.readImage
import ie.wit.adventurio.models.Account
import ie.wit.fragments.TripsListFragment
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.fragment_profile_edit.*
import kotlinx.android.synthetic.main.home.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.startActivity


class Home : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {

    lateinit var ft: FragmentTransaction
    private val USER_KEY = "user_key"
    var user = Account()
    val IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)
        setSupportActionBar(toolbar)

        if (intent.hasExtra("userLoggedIn")) {
            user = intent.extras.getParcelable<Account>("userLoggedIn")
        }



        navView.setNavigationItemSelectedListener(this)


        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        ft = supportFragmentManager.beginTransaction()

        var profileFragment = ProfileFragment()
        var statsFragment = StatisticsFragment()
        var tripsListFragment = TripsListFragment()

        val bundle = Bundle()
        bundle.putParcelable("user_key",user)
        profileFragment.arguments = bundle
        statsFragment.arguments = bundle
        tripsListFragment.arguments = bundle


        statsFragment = StatisticsFragment.newInstance(user)
        //fragment.arguments = bundle

        ft.replace(R.id.homeFrame, statsFragment)
        ft.commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.nav_statistics -> {
                navigateTo(StatisticsFragment.newInstance(user))
            }

            R.id.nav_trips_list -> {
                navigateTo(TripsListFragment.newInstance(user))
            }

            R.id.nav_record_trips -> {
                startActivityForResult(intentFor<RecordTripFragment>().putExtra("user_key", user), 0)
            }

            R.id.nav_profile -> {
                navigateTo(ProfileFragment.newInstance(user))
            }

            R.id.nav_logout -> startActivity<LoginActivity>()

        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }





    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START)
        else
            super.onBackPressed()
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

    private fun navigateTo(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.homeFrame, fragment)
            .addToBackStack(null)
            .commit()
    }

}
