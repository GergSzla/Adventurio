package ie.wit.adventurio.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.Menu
import android.view.MenuItem
import ie.wit.adventurio.R
import ie.wit.adventurio.main.MainApp
import ie.wit.adventurio.models.Account
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile_edit.*
import kotlinx.android.synthetic.main.activity_profile_edit.btnLoginPass
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast

class ProfileEditActivity : AppCompatActivity() {

    var user = Account()
    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)

        app = application as MainApp


        if (intent.hasExtra("EditUserLoggedIn")) {
            user = intent.extras.getParcelable<Account>("EditUserLoggedIn")
            editFirstName.setText(user.firstName)
            editSurname.setText(user.surname)
            editEmail.setText(user.Email)
            editPassword.setText(user.Password)
            editUsername.setText(user.username)
        }
        btnLoginPass.setOnClickListener {
            if(btnLoginPass.text.toString().equals("Show")){
                editPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                btnLoginPass.text = "Hide"
            } else{
                editPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                btnLoginPass.text = "Show"
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit_profile, menu)
        if (menu != null) menu.getItem(0).setVisible(true)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.item_update -> {
                updateProfile()
            }
            R.id.item_cancel -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        finish()

    }

    fun updateProfile(){
        if(editPassword.text.toString() == editPasswordConf.text.toString()){
            user.firstName = editFirstName.text.toString()
            user.surname = editSurname.text.toString()
            user.username = editUsername.text.toString()
            user.Email = editEmail.text.toString()
            user.Password = editPassword.text.toString()
            app.users.updateAccount(user.copy())
            finish()
            startActivityForResult(intentFor<ProfileActivity>().putExtra("userLoggedIn", user), 0)
        }else{
            toast("Passwords don't match!")
        }


    }
}
