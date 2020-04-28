package ie.wit.adventurio.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import ie.wit.adventurio.R
import ie.wit.adventurio.adapters.TripsAdapter
import ie.wit.adventurio.fragments.StatisticsFragment
import ie.wit.adventurio.helpers.createLoader
import ie.wit.adventurio.helpers.hideLoader
import ie.wit.adventurio.helpers.showLoader
import ie.wit.adventurio.main.MainApp
import ie.wit.adventurio.models.Account
import ie.wit.adventurio.models.Trip
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.android.synthetic.main.fragment_trips_list.view.*
import org.jetbrains.anko.*
import java.util.*

class LoginActivity : AppCompatActivity(),AnkoLogger {


    lateinit var app: MainApp
    lateinit var loader : AlertDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        app = application as MainApp

        app.auth = FirebaseAuth.getInstance()
        app.database = FirebaseDatabase.getInstance().reference
        app.storage = FirebaseStorage.getInstance().reference

        loader = createLoader(this)



        info("Login Activity started..")


        //autoSignIn()


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        app.googleSignInClient = GoogleSignIn.getClient(this, gso)

        btnGoToRegScreen.setOnClickListener{
            val animrtl = AnimationUtils.loadAnimation(this,R.anim.swipe_rl)
            scrollView3.startAnimation(animrtl)
            Handler().postDelayed({
                startActivity<RegisterActivity>()
            },650)

        }
        btnLoginToAccount.setOnClickListener{
            validateForm()
            if(!(txtEmail.text.toString() == "" || txtPassword.text.toString() == "")){
                signIn(txtEmail.text.toString(), txtPassword.text.toString())
            } else {
                toast("Email and Password fields are required to login!")
            }
        }

        sign_in_button.setOnClickListener {
            googleSignIn()
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
            val animrlr = AnimationUtils.loadAnimation(this,R.anim.swipe_rl)
            scrollView3.startAnimation(animrlr)

            Handler().postDelayed({
                startActivity<ForgotPasswordActivity>()
            },650)

        }

        sign_in_button.setSize(SignInButton.SIZE_WIDE)
        sign_in_button.setColorScheme(0)
    }

    private fun googleSignIn() {
        val signInIntent = app.googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun validateForm(): Boolean {
        var valid = true

        val email = txtEmail.text.toString()
        if (TextUtils.isEmpty(email)) {
            txtEmail.error = "Required."
            valid = false
        } else {
            txtEmail.error = null
        }

        val password = txtPassword.text.toString()
        if (TextUtils.isEmpty(password)) {
            txtPassword.error = "Required."
            valid = false
        } else {
            txtPassword.error = null
        }

        return valid
    }

    /*fun autoSignIn(){
        app.auth = FirebaseAuth.getInstance()

        var user = app.auth.currentUser?.uid
        if (user != null){
            startActivity<Home>()
        }
    }*/

    companion object {
        private const val TAG = "EmailPassword"
        private const val RC_SIGN_IN = 9001
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    fun writeNewUserStats(user: Account) {
        showLoader(loader, "Adding User to Firebase")

        val uid = app.auth.currentUser!!.uid
        val userValues = user.toMap()
        app.database.child("user-stats")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.hasChild(uid)){
                        val childUpdates = HashMap<String, Any>()
                        childUpdates["/user-stats/$uid"] = userValues

                        app.database.updateChildren(childUpdates)
                        hideLoader(loader)
                    }
                    app.database.child("user-stats")
                        .removeEventListener(this)
                    startActivity<Home>()
                }
            })
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)
        showLoader(loader, "Logging In with Google...")
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        app.auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user = app.auth.currentUser!!
                    val fname = user.displayName!!.substringBefore(" ")
                    val sname = user.displayName!!.substringAfter(" ")
                    writeNewUserStats(Account(id = UUID.randomUUID().toString(), Email = app.auth.currentUser!!.email.toString(), firstName = fname,
                        surname = sname,weight = 0.0, username = fname+"_"+sname, image = user.photoUrl.toString() , loginUsed = "google"))
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
                hideLoader(loader)
            }
    }

    private fun signIn(email: String, password: String) {
        showLoader(loader, "Logging In...")
        app.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = app.auth.currentUser
                    app.database = FirebaseDatabase.getInstance().reference
                    startActivity<Home>()

                } else {
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
                hideLoader(loader)
            }
    }

    override fun onBackPressed() {
        System.exit(0)
    }
}
