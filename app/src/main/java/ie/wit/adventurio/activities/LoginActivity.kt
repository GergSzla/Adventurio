package ie.wit.adventurio.activities

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import ie.wit.adventurio.R
import ie.wit.adventurio.fragments.StatisticsFragment
import ie.wit.adventurio.helpers.createLoader
import ie.wit.adventurio.helpers.hideLoader
import ie.wit.adventurio.helpers.showLoader
import ie.wit.adventurio.main.MainApp
import ie.wit.adventurio.models.Account
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.*
import java.util.ArrayList

class LoginActivity : AppCompatActivity(),AnkoLogger {


    lateinit var app: MainApp
    lateinit var loader : AlertDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        app = application as MainApp

        app.auth = FirebaseAuth.getInstance()
        loader = createLoader(this)

        info("Login Activity started..")


        autoSignIn()

        //val AccountList = app.users.getAllAccounts() as ArrayList<Account>

        btnGoToRegScreen.setOnClickListener{
            startActivity<RegisterActivity>()
        }
        btnLoginToAccount.setOnClickListener{
            if(!(txtEmail.text.toString() == "" &&
                        txtPassword.text.toString() == "")){
                /*var existingUser = AccountList.find { p -> p.Email.toLowerCase() == txtEmail.text.toString().toLowerCase() }
                if (existingUser != null){
                    if (existingUser.Email == txtEmail.text.toString().toLowerCase() && existingUser.Password == txtPassword.text.toString().toLowerCase()){
                        loginToAccount(existingUser)
                    }
                } else {
                    longToast("Error: The account ${txtEmail.text.toString()} does not exist!")
                }*/

                signIn(txtEmail.text.toString(), txtPassword.text.toString())
            } else {
                toast("Email and Password fields are required to login!")
            }
        }



        btnLoginPass.setOnClickListener {
            if(btnLoginPass.text.toString().equals("Show")){
                txtPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                btnLoginPass.text = "Hide"
            } else{
                txtPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                btnLoginPass.text = "Show"
            }
        }

        btnForgotPW.setOnClickListener{
            startActivity<ForgotPasswordActivity>()
        }
    }


    fun autoSignIn(){
        app.database = FirebaseDatabase.getInstance().reference

        var user = app.auth.currentUser?.uid
        if (user != null){
            startActivity<Home>()
        }
    }

    private fun signIn(email: String, password: String) {
        /*if (!validateForm()) {
            return
        }*/
        showLoader(loader, "Logging In...")
        // [START sign_in_with_email]
        app.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = app.auth.currentUser
                    app.database = FirebaseDatabase.getInstance().reference

                    //startActivityForResult(intentFor<Home>().putExtra("user_key", user), 0)
                    startActivity<Home>()

                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
                // [START_EXCLUDE]
                /*if (!task.isSuccessful) {
                    status.setText(R.string.auth_failed)
                }*/
                hideLoader(loader)
                // [END_EXCLUDE]
            }
        // [END sign_in_with_email]
    }

    override fun onBackPressed() {
        System.exit(0)
    }

    fun loginToAccount(user:Account){
        if(txtEmail.text.toString().contains("@") && txtEmail.text.toString().contains(".com")){

            startActivityForResult(intentFor<Home>().putExtra("user_key", user), 0)
            txtEmail.setText("")
            txtPassword.setText("")
        } else {
            toast("Error: Invalid Email Address")
        }
    }
}
