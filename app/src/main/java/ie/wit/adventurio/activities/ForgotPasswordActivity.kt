package ie.wit.adventurio.activities

import android.content.ContentValues
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import ie.wit.adventurio.R
import ie.wit.adventurio.fragments.StatisticsFragment
import ie.wit.adventurio.helpers.createLoader
import ie.wit.adventurio.helpers.hideLoader
import ie.wit.adventurio.helpers.showLoader
import ie.wit.adventurio.main.MainApp
import ie.wit.adventurio.models.Account
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_profile_edit.view.*
import org.jetbrains.anko.toast
import java.util.ArrayList

class ForgotPasswordActivity : AppCompatActivity() {

    lateinit var app: MainApp


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        app = application as MainApp
        //val AccountList = app.users.getAllAccounts() as ArrayList<Account>


        btnGetPassword.setOnClickListener{
            if (txtEmailPWRem.text.toString() != "") {
                resetPassword(txtEmailPWRem.text.toString())
            } else {
                toast("Email Field Is Empty!")

            }
        }

        btnBackToLogin.setOnClickListener {
            finish()
        }



    }

    private fun resetPassword(email : String){
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(ContentValues.TAG, "Email sent.")
                    resetStatus2.text = "Reset Email Sent!"
                    resetStatus2.setTextColor(Color.parseColor("#009705"))
                } else {
                    resetStatus2.text = "An Error Has Occurred!"
                    resetStatus2.setTextColor(Color.parseColor("#970000"))
                }
            }
    }
}
