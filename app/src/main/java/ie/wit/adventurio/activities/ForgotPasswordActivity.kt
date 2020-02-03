package ie.wit.adventurio.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ie.wit.adventurio.R
import ie.wit.adventurio.main.MainApp
import ie.wit.adventurio.models.Account
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.toast
import java.util.ArrayList

class ForgotPasswordActivity : AppCompatActivity() {

    lateinit var app: MainApp


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        app = application as MainApp
        val AccountList = app.users.getAllAccounts() as ArrayList<Account>


        btnGetPassword.setOnClickListener{
            var existingUser = AccountList.find { p -> p.Email.toLowerCase() == txtEmailPWRem.text.toString().toLowerCase() }
            if (existingUser != null) {
                if (existingUser.Email.toLowerCase() == txtEmailPWRem.text.toString().toLowerCase() ){
                    UserPasswordDisplay.setText("Your password: ${existingUser.Password.toString()}")
                } else {
                    toast("This user does not exist!")
                }
            } else {
                toast("This user does not exist!")
            }
        }

        btnBackToLogin.setOnClickListener {
            finish()
        }
    }
}
