package ie.wit.adventurio.main

import android.app.Application
import ie.wit.adventurio.models.UserJsonStore
import ie.wit.adventurio.models.UserStore
import org.jetbrains.anko.AnkoLogger

class MainApp : Application(), AnkoLogger {

    lateinit var users: UserStore
    //lateinit var questions: QuestionStore
    //lateinit var stats: StatsStore

    override fun onCreate() {
        super.onCreate()
        users = UserJsonStore(applicationContext)
        //questions = MTAJSONStore(applicationContext)
        //stats = StatsJSONStore(applicationContext)

        //info("MTA App started")
        //longToast("Hold card to edit." +
                //"\nTap to begin challenge.")
    }
}