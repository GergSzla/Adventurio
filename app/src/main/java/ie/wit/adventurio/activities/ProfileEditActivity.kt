/*package ie.wit.adventurio.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.Menu
import android.view.MenuItem
import ie.wit.adventurio.R
import ie.wit.adventurio.helpers.readImage
import ie.wit.adventurio.helpers.readImageFromPath
import ie.wit.adventurio.helpers.showImagePicker
import ie.wit.adventurio.main.MainApp
import ie.wit.adventurio.models.Account
import kotlinx.android.synthetic.main.fragment_profile_edit.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast

class ProfileEditActivity : AppCompatActivity() {

    var user = Account()
    lateinit var app: MainApp
    val IMAGE_REQUEST = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_profile_edit)

        app = application as MainApp


        if (intent.hasExtra("EditUserLoggedIn")) {
            user = intent.extras.getParcelable<Account>("EditUserLoggedIn")
            editFirstName.setText(user.firstName)
            editSurname.setText(user.surname)
            editEmail.setText(user.Email)
            editPhoneNo.setText(user.phoneNo)
            editPassword.setText(user.Password)
            editUsername.setText(user.username)
            editStepsGoal.setText(user.stepsGoal.toString())
            editDistanceGoal.setText(user.distanceGoal.toString())
            profImage.setImageBitmap(readImageFromPath(this, user.image))
            if (user.image != "") {
                addImage.setText(R.string.btnChangeImage)
            }else{
                addImage.setText(R.string.btnAddImg)
            }
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

        addImage.setOnClickListener {
            showImagePicker(this, IMAGE_REQUEST)
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

    fun updateProfile(){
        if(editPassword.text.toString() == editPasswordConf.text.toString()){
            user.firstName = editFirstName.text.toString()
            user.surname = editSurname.text.toString()
            user.username = editUsername.text.toString()
            user.Email = editEmail.text.toString()
            user.stepsGoal = (editStepsGoal.text.toString()).toInt()
            user.distanceGoal = (editDistanceGoal.text.toString()).toDouble()
            user.Password = editPassword.text.toString()
            user.phoneNo = editPhoneNo.text.toString()
            user.image
            app.users.updateAccount(user.copy())
            finish()
            startActivityForResult(intentFor<ProfileActivity>().putExtra("userLoggedIn", user), 0)
        }else{
            toast("Passwords don't match!")
        }


    }
}*/
