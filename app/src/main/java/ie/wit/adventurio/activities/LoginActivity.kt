package ie.wit.adventurio.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ie.wit.adventurio.R
import ie.wit.adventurio.main.MainApp
import ie.wit.adventurio.models.Account
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.startActivity

class LoginActivity : AppCompatActivity(),AnkoLogger {


    lateinit var app: MainApp


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        info("Login Activity started..")

        app = application as MainApp

        var existingUsers = (app.users.getAllAccounts())
        existingUsers


        btnGoToRegScreen.setOnClickListener{
            startActivity<RegisterActivity>()
        }
    }
}
