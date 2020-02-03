package ie.wit.adventurio.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import ie.wit.adventurio.R
import ie.wit.adventurio.models.Account
import kotlinx.android.synthetic.main.activity_profile.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class ProfileActivity : AppCompatActivity() {

    var user = Account()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        if (intent.hasExtra("userLoggedIn")) {
            user = intent.extras.getParcelable<Account>("userLoggedIn")
            txtNameProf.setText(user.firstName + " " + user.surname)
            txtEmailProf.setText(user.Email)
            txtUsernameProf.setText(user.username)
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
                startActivity<ProfileEditActivity>()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        finish()
    }
}
