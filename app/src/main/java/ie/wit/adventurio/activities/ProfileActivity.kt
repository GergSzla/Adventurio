/*package ie.wit.adventurio.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import ie.wit.adventurio.R
import ie.wit.adventurio.helpers.readImageFromPath
import ie.wit.adventurio.main.MainApp
import ie.wit.adventurio.models.Account
import kotlinx.android.synthetic.main.fragment_profile.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.startActivity

class ProfileActivity : AppCompatActivity() {

    var user = Account()
    lateinit var app: MainApp


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_profile)

        app = application as MainApp

        if (intent.hasExtra("userLoggedIn")) {
            user = intent.extras.getParcelable<Account>("userLoggedIn")
            txtNameProf.setText(user.firstName + " " + user.surname)
            txtEmailProf.setText(user.Email)
            txtUsernameProf.setText(user.username)
            imageView.setImageBitmap(readImageFromPath(this, user.image))
        }

        btnDelAccount.setOnClickListener {
            app.users.deleteAccount(user)
            startActivity<LoginActivity>()
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_profile, menu)
        if (menu != null) menu.getItem(0).setVisible(true)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.item_cancel -> {
                finish()
            }
            R.id.item_edit -> {
                startActivityForResult(intentFor<ProfileEditActivity>().putExtra("EditUserLoggedIn", user), 0)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        startActivityForResult(intentFor<WalkingStatsActivity>().putExtra("userLoggedIn", user), 0)
        finish()
    }
}*/
