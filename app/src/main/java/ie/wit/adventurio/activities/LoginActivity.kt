package ie.wit.adventurio.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import ie.wit.adventurio.R
import ie.wit.adventurio.fragments.StatisticsFragment
import ie.wit.adventurio.main.MainApp
import ie.wit.adventurio.models.Account
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.*
import java.util.ArrayList

class LoginActivity : AppCompatActivity(),AnkoLogger {


    lateinit var app: MainApp


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)



        info("Login Activity started..")

        app = application as MainApp

        val AccountList = app.users.getAllAccounts() as ArrayList<Account>

        btnGoToRegScreen.setOnClickListener{
            startActivity<RegisterActivity>()
        }
        btnLoginToAccount.setOnClickListener{
            if(!(txtEmail.text.toString() == "" &&
                        txtPassword.text.toString() == "")){
                var existingUser = AccountList.find { p -> p.Email.toLowerCase() == txtEmail.text.toString().toLowerCase() }
                if (existingUser != null){
                    if (existingUser.Email == txtEmail.text.toString().toLowerCase() && existingUser.Password == txtPassword.text.toString().toLowerCase()){
                        loginToAccount(existingUser)
                    }
                } else {
                    longToast("Error: The account ${txtEmail.text.toString()} does not exist!")
                }
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

    override fun onBackPressed() {
        finish();
        System.exit(0)
    }

    fun loginToAccount(user:Account){
        if(txtEmail.text.toString().contains("@") && txtEmail.text.toString().contains(".com")){

            startActivityForResult(intentFor<Home>().putExtra("userLoggedIn", user), 0)
            txtEmail.setText("")
            txtPassword.setText("")
        } else {
            toast("Error: Invalid Email Address")
        }
    }
}
