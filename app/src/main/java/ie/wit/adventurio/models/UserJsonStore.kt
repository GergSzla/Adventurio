package ie.wit.adventurio.models

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import ie.wit.adventurio.helpers.exists
import ie.wit.adventurio.helpers.read
import ie.wit.adventurio.helpers.write
import org.jetbrains.anko.AnkoLogger
import java.util.ArrayList
import org.jetbrains.anko.toast

val USERS_JSON_FILE = "users.json"
val gsonBuilder = GsonBuilder().setPrettyPrinting().create()
val listType = object : TypeToken<ArrayList<Account>>() {}.type


class UserJsonStore : UserStore, AnkoLogger {

    val context: Context
    var users = mutableListOf<Account>()


    constructor (context: Context) {
        this.context = context
        if (exists(context, USERS_JSON_FILE)) {
            deserialize()
        }
    }

    override fun registerAccount(user: Account) {
        users.add(user)
        serialize()
    }

    override fun getAllAccounts(): List<Account> {
        return users
    }

    override fun updateAccount(user: Account) {
        val AccountList = getAllAccounts() as ArrayList<Account>
        var foundUser: Account? = AccountList.find { p -> p.id == user.id }
        if (foundUser != null){
            foundUser.Email = user.Email
            foundUser.Password = user.Password
            foundUser.firstName = user.firstName
            foundUser.surname = user.surname
            foundUser.username = user.username
            foundUser.secondaryPW = user.secondaryPW
            foundUser.secondaryPWType = user.secondaryPWType
        }
        serialize()
    }

    override fun deleteAccount(user: Account) {
        users.remove(user)
        serialize()
    }


    private fun serialize() {
        val jsonString = gsonBuilder.toJson(users, listType)
        write(context, USERS_JSON_FILE, jsonString)
    }


    private fun deserialize() {
        val jsonString = read(context, USERS_JSON_FILE)
        users = Gson().fromJson(jsonString, listType)
    }
}