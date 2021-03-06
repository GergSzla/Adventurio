package ie.wit.adventurio.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.FirebaseDatabase
import ie.wit.adventurio.R
import ie.wit.adventurio.helpers.createLoader
import ie.wit.adventurio.helpers.hideLoader
import ie.wit.adventurio.helpers.showLoader
import ie.wit.adventurio.main.MainApp
import ie.wit.adventurio.models.Account
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import java.util.*

class RegisterActivity : AppCompatActivity() {

    var account = Account()
    lateinit var app: MainApp
    lateinit var loader : AlertDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        val animrlr = AnimationUtils.loadAnimation(this,R.anim.swipe_lr)
        regScrollview.startAnimation(animrlr)


        app = application as MainApp
        loader = createLoader(this)

        btnGoToLoginScreen.setOnClickListener{
            startActivity<LoginActivity>()
        }
        btnRegisterAccount.setOnClickListener{
            //val AccountList = app.users.getAllAccounts() as ArrayList<Account>

            validateForm()

            if(!(txtUsernameReg.text.toString() == "" ||
                    txtEmailReg.text.toString() == "" ||
                    txtFirstNameReg.text.toString() == "" ||
                    txtSurnameReg.text.toString() == "" ||
                    txtPasswordReg.text.toString() == "" ||
                    txtConfPasswordReg.text.toString() == "")){
                if(txtPasswordReg.text.toString() == txtConfPasswordReg.text.toString()){
                        createAccount(txtEmailReg.text.toString(),txtPasswordReg.text.toString())

                } else {
                    txtConfPasswordReg.selectAll()
                    toast("Error: Your passwords don't match!")
                }
            }
        }

        btnRegPass.setOnClickListener {
            if(btnRegPass.text.toString().equals("Show")){
                txtPasswordReg.transformationMethod = HideReturnsTransformationMethod.getInstance()
                btnRegPass.text = "Hide"
            } else{
                txtPasswordReg.transformationMethod = PasswordTransformationMethod.getInstance()
                btnRegPass.text = "Show"
            }
        }


    }

    private fun validateForm(): Boolean {
        var valid = true

        //validate Username
        val un = txtUsernameReg.text.toString()
        if (TextUtils.isEmpty(un)) {
            txtUsernameReg.error = "Required."
            valid = false
        } else {
            txtUsernameReg.error = null
        }

        //validate Weight
        val weight = txtWeight.text.toString()
        if (TextUtils.isEmpty(weight)) {
            txtWeight.error = "Required (To Calculate Calories Burned!)."
            valid = false
        } else {
            txtWeight.error = null
        }

        //validate Fname
        val fname = txtFirstNameReg.text.toString()
        if (TextUtils.isEmpty(fname)) {
            txtFirstNameReg.error = "Required."
            valid = false
        } else {
            txtFirstNameReg.error = null
        }

        //validate Sname
        val sname = txtSurnameReg.text.toString()
        if (TextUtils.isEmpty(sname)) {
            txtSurnameReg.error = "Required."
            valid = false
        } else {
            txtSurnameReg.error = null
        }

        //validate Email
        val email = txtEmailReg.text.toString()
        if (TextUtils.isEmpty(email)) {
            txtEmailReg.error = "Required."
            valid = false
        } else {
            txtEmailReg.error = null
        }

        //validated pw
        val password = txtPasswordReg.text.toString()
        if (TextUtils.isEmpty(password)) {
            txtPasswordReg.error = "Required."
            valid = false
        } else {
            txtPasswordReg.error = null
        }

        //validated confpw
        val confpassword = txtConfPasswordReg.text.toString()
        if (TextUtils.isEmpty(confpassword)) {
            txtConfPasswordReg.error = "Required."
            valid = false
        } else {
            txtConfPasswordReg.error = null
        }

        return valid
    }

    fun writeNewUserStats(user: Account) {
        showLoader(loader, "Adding User to Firebase")
        //val uid = app.auth.currentUser!!.uid
        //val key = app.database.child("user-stats").push().key
        val uid = app.auth.currentUser!!.uid
        val userValues = user.toMap()

        val childUpdates = HashMap<String, Any>()
        childUpdates["/user-stats/$uid"] = userValues
        //childUpdates["/user-trips/${user.Email}/$key"] = userValues

        app.database.updateChildren(childUpdates)
        hideLoader(loader)
    }

    private fun createAccount(email: String, password: String) {
        /*if (!validateForm()) {
            return
        }*/

        showLoader(loader, "Creating Account...")
        // [START create_user_with_email]
        app.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = app.auth.currentUser
                    app.database = FirebaseDatabase.getInstance().reference
                    writeNewUserStats(Account(id = UUID.randomUUID().toString(), Email = app.auth.currentUser!!.email.toString(), firstName = txtFirstNameReg.text.toString(),
                    surname = txtSurnameReg.text.toString(),weight = txtWeight.text.toString().toDouble(), username = txtUsernameReg.text.toString(), loginUsed = "firebaseAuth"))
                    startActivity<LoginActivity>()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
                // [START_EXCLUDE]
                hideLoader(loader)
                // [END_EXCLUDE]
            }
        // [END create_user_with_email]
    }

    /*fun createAccount(){
        /*if(txtEmailReg.text.toString().contains("@") && txtEmailReg.text.toString().contains(".com")){
            account.Email = txtEmailReg.text.toString()
        } else {
            toast("Error: Invalid Email Address")
        }
        account.firstName = txtFirstNameReg.text.toString()
        account.surname = txtSurnameReg.text.toString()
        account.username = txtUsernameReg.text.toString()
        account.Password = txtPasswordReg.text.toString()
        account.id = UUID.randomUUID().toString()
        account.secondaryPWType = 0 //none
        account.secondaryPW = ""
        app.users.registerAccount(account.copy())
        toast("Your account as ${account.username} (${account.Email}) has been created")
        startActivity<LoginActivity>()*/
    }*/

}
