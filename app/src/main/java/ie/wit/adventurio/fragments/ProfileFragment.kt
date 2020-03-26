package ie.wit.adventurio.fragments

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import ie.wit.adventurio.R
import ie.wit.adventurio.activities.LoginActivity
import ie.wit.adventurio.helpers.readImageFromPath
import ie.wit.adventurio.main.MainApp
import ie.wit.adventurio.models.Account
import kotlinx.android.synthetic.main.fragment_profile.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info


class ProfileFragment : Fragment(), AnkoLogger {

    lateinit var app: MainApp
    lateinit var eventListener : ValueEventListener
    var user = Account()
    var userProfile: Account? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp

        arguments?.let {
            userProfile = it.getParcelable("user-profile")
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_profile, container, false)
        activity?.title = getString(R.string.menu_prof)


        /*val bundle = arguments
        if (bundle != null) {
            user = bundle.getParcelable("user_key")
        }*/



        root.txtNameProf.text = userProfile!!.firstName + " " + userProfile!!.surname
        root.txtEmailProf.text = userProfile!!.Email
        root.txtUsernameProf.text = userProfile!!.username
        root.txtStepsProf.text = userProfile!!.stepsGoal.toString()
        root.txtPhoneProf.text = userProfile!!.phoneNo
        root.txtDistanceProf.text = userProfile!!.distanceGoal.toString()+"km"
        root.imageView.setImageBitmap(readImageFromPath(activity!!, userProfile!!.image))

        root.editProfileFab.setOnClickListener {
            navigateTo(ProfileEditFragment.newInstance(userProfile!!))
        }

        root.deleteProfileFab.setOnClickListener {
            showDialog()
        }

        return root
    }

    fun showDialog(){
        //https://www.tutorialkart.com/kotlin-android/android-alert-dialog-example/
        val dialogBuilder = AlertDialog.Builder(activity)
        dialogBuilder.setMessage("Are you sure you wish to delete your account and all of your Adventurio data?")
            .setCancelable(false)
            .setPositiveButton("Delete Account", DialogInterface.OnClickListener {
                    dialog, id -> deleteProfile()
            })
            .setNegativeButton("Cancel", DialogInterface.OnClickListener {
                    dialog, id -> dialog.cancel()
            })

        val alert = dialogBuilder.create()
        alert.setTitle("Warning")
        alert.show()
    }

    fun deleteProfile(){
        deleteUser(app.auth.currentUser!!.uid) //removes user stats from database
        deleteUserTrips(app.auth.currentUser!!.uid) //removes user's trips from db
        removeUserFirebaseAuth() //removes user auth
    }

    fun removeUserFirebaseAuth(){
        val user = FirebaseAuth.getInstance().currentUser
        user!!.delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val toast =
                        Toast.makeText(
                            activity!!.applicationContext,
                            "Account Removed! Application Restarting . . .",
                            Toast.LENGTH_LONG
                        )
                    toast.show()
                    restartApp()
                }
            }

    }

    fun deleteUser(uid: String?) {
        app.database.child("user-stats").child(uid!!)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.removeValue()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        info("Firebase User error : ${error.message}")
                    }
                })
    }

    fun deleteUserTrips(uid: String?) {
        app.database.child("user-trips").child(uid!!)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.removeValue()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        info("Firebase User error : ${error.message}")
                    }
                })
    }

    private fun restartApp() {
        val mStartActivity = Intent(context, LoginActivity::class.java)
        val mPendingIntentId = 123456
        val mPendingIntent = PendingIntent.getActivity(
            context,
            mPendingIntentId,
            mStartActivity,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        val mgr = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mgr[AlarmManager.RTC, System.currentTimeMillis() + 100] = mPendingIntent
        System.exit(0)
    }


    companion object {
        @JvmStatic
        fun newInstance(user: Account) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("user-profile",user)
                }
            }
    }

    private fun navigateTo(fragment: Fragment) {
        val fragmentManager: FragmentManager = activity!!.supportFragmentManager
        fragmentManager.beginTransaction()
            .replace(R.id.homeFrame, fragment)
            .addToBackStack(null)
            .commit()
    }
}
