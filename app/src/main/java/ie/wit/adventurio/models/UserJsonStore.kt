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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAllAccounts(): List<Account> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateAccount(user: Account) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteAccount(user: Account) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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