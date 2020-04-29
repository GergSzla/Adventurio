package ie.wit.adventurio.fragments

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import ie.wit.adventurio.R
import ie.wit.adventurio.activities.LoginActivity
import ie.wit.adventurio.helpers.readImageFromPath
import ie.wit.adventurio.main.MainApp
import ie.wit.adventurio.models.Account
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.fragment_profile.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.io.File


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
        val animltr = AnimationUtils.loadAnimation(context,R.anim.swipe_lr)
        root.profLinear.startAnimation(animltr)

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
        root.txtDrivingDistanceProf.text = userProfile!!.drivingDistanceGoal.toString()+"km"
        root.txtCyclingDistanceProf.text = userProfile!!.cyclingDistanceGoal.toString()+"km"
        root.txtWeight.text = userProfile!!.weight.toString()+"kg"



        if(app.auth.currentUser!!.photoUrl != null){
            Picasso.get().load(app.auth.currentUser!!.photoUrl)
                .fit()
                .centerCrop()
                .transform(RoundedCornersTransformation(50,0))
                .into(root.linearview.imageView)
        } else {
            Picasso.get().load(R.mipmap.ic_avatar)
                .fit()
                .centerCrop()
                .transform(RoundedCornersTransformation(50,0))
                .into(root.linearview.imageView)
        }


        root.editProfileFab.setOnClickListener {
            val animrtl = AnimationUtils.loadAnimation(context,R.anim.swipe_rl)
            root.profLinear.startAnimation(animrtl)

            Handler().postDelayed({
                navigateTo(ProfileEditFragment.newInstance(userProfile!!))
            },500)

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
        restartApp()
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
                }
            }

    }

    fun deleteUser(uid: String?) {
        app.database.child("user-stats").child(uid!!)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.removeValue()
                        deleteUserTrips(app.auth.currentUser!!.uid) //removes user's trips from db
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
                        removeUserFirebaseAuth() //removes user auth
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
        deleteUser(app.auth.currentUser!!.uid) //removes user stats from database
        //System.exit(0)
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
