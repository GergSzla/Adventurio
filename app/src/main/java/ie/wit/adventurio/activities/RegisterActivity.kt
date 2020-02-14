package ie.wit.adventurio.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.EditText
import ie.wit.adventurio.R
import ie.wit.adventurio.main.MainApp
import ie.wit.adventurio.models.Account
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.longToast
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import java.util.*

class RegisterActivity : AppCompatActivity() {

    var account = Account()
    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)



        app = application as MainApp

        btnGoToLoginScreen.setOnClickListener{
            startActivity<LoginActivity>()
        }
        btnRegisterAccount.setOnClickListener{
            val AccountList = app.users.getAllAccounts() as ArrayList<Account>

            if(!(txtUsernameReg.text.toString() == "" ||
                    txtEmailReg.text.toString() == "" ||
                    txtFirstNameReg.text.toString() == "" ||
                    txtSurnameReg.text.toString() == "" ||
                    txtPasswordReg.text.toString() == "" ||
                    txtConfPasswordReg.text.toString() == "")){
                if(txtPasswordReg.text.toString() == txtConfPasswordReg.text.toString()){
                    var existingUser = AccountList.find { p -> p.Email.toLowerCase() == txtEmailReg.text.toString().toLowerCase() }
                    if (existingUser == null){
                        createAccount()
                    } else {
                        longToast("Error: The account already exists!")
                    }
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
    fun createAccount(){
        if(txtEmailReg.text.toString().contains("@") && txtEmailReg.text.toString().contains(".com")){
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
        startActivity<LoginActivity>()
    }

}
