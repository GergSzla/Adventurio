package ie.wit.adventurio.fragments

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import ie.wit.adventurio.R
import ie.wit.adventurio.activities.Home
import ie.wit.adventurio.helpers.createLoader
import ie.wit.adventurio.helpers.hideLoader
import ie.wit.adventurio.helpers.readImageFromPath
import ie.wit.adventurio.helpers.showLoader
import ie.wit.adventurio.main.MainApp
import ie.wit.adventurio.models.Account
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.fragment_profile_edit.*
import kotlinx.android.synthetic.main.fragment_profile_edit.view.*
import java.io.IOException


class ProfileEditFragment : Fragment() {


    lateinit var app: MainApp
    //var user = Account()
    var userProfileEdit: Account? = null
    lateinit var loader : AlertDialog
    lateinit var root: View


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp

        arguments?.let {
            userProfileEdit = it.getParcelable("user-profile-edit")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_profile_edit, container, false)
        activity?.title = getString(R.string.editProf)
        val anim = AnimationUtils.loadAnimation(context,R.anim.swipe_lr)
        root.scrollView2.startAnimation(anim)

        root.editFirstName.setText(userProfileEdit!!.firstName)
        root.editSurname.setText(userProfileEdit!!.surname)
        root.editEmail.setText(userProfileEdit!!.Email)
        root.editEmail.isEnabled = false

        root.editDrivingDistanceGoal.setText(userProfileEdit!!.drivingDistanceGoal.toString())
        root.editCyclingDistanceGoal.setText(userProfileEdit!!.cyclingDistanceGoal.toString())

        root.editPhoneNo.setText(userProfileEdit!!.phoneNo)
        root.editUsername.setText(userProfileEdit!!.username)
        root.editStepsGoal.setText(userProfileEdit!!.stepsGoal.toString())
        root.editDistanceGoal.setText(userProfileEdit!!.distanceGoal.toString())
        root.editWeight.setText(userProfileEdit!!.weight.toString())

        if(app.auth.currentUser!!.photoUrl != null){
            Picasso.get().load(app.auth.currentUser!!.photoUrl)
                .fit()
                .centerCrop()
                .transform(CropCircleTransformation())
                .into(root.scrollView2.profImage)
        } else {
            Picasso.get().load(R.mipmap.ic_avatar)
                .fit()
                .centerCrop()
                .transform(CropCircleTransformation())
                .into(root.scrollView2.profImage)
        }

        if (userProfileEdit!!.loginUsed == "google"){
            root.passConst.isVisible = false
            root.btnResetPass.isVisible = false
        }


        root.btnLoginPass.setOnClickListener {
            if(root.btnLoginPass.text.toString().equals("Show")){
                root.editPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                root.btnLoginPass.text = "Hide"
            } else{
                root.editPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                root.btnLoginPass.text = "Show"
            }
        }


        root.updateProfileFab.setOnClickListener {
            if(userProfileEdit!!.loginUsed == "firebaseAuth"){
                validateForm()
                if(root.editPassword.text.toString() != ""){
                    reauthenticateUser(app.auth.currentUser!!.email.toString(),root.editPassword.text.toString())
                } else {
                    val toast =
                        Toast.makeText(
                            activity!!.applicationContext,
                            "Password Required!",
                            Toast.LENGTH_LONG
                        )
                    toast.show()
                }
            } else if (userProfileEdit!!.loginUsed == "google"){
                googleAccEdit()
            }
        }

        root.btnResetPass.setOnClickListener {
            resetPassword()
        }

        return root
    }

    private fun validateForm(): Boolean {
        var valid = true

        val password = root.editPassword.text.toString()
        if (TextUtils.isEmpty(password)) {
            root.editPassword.error = "Required."
            valid = false
        } else {
            root.editPassword.error = null
        }

        return valid
    }

    private fun googleAccEdit(){
        userProfileEdit!!.firstName = root.editFirstName.text.toString()
        userProfileEdit!!.surname = root.editSurname.text.toString()
        userProfileEdit!!.username = root.editUsername.text.toString()
        userProfileEdit!!.Email = root.editEmail.text.toString()
        userProfileEdit!!.stepsGoal = (root.editStepsGoal.text.toString()).toInt()
        userProfileEdit!!.distanceGoal = (root.editDistanceGoal.text.toString()).toDouble()
        userProfileEdit!!.phoneNo = root.editPhoneNo.text.toString()
        userProfileEdit!!.weight = root.editWeight.text.toString().toDouble()
        userProfileEdit!!.drivingDistanceGoal = (root.editDrivingDistanceGoal.text.toString()).toDouble()
        userProfileEdit!!.cyclingDistanceGoal = (root.editCyclingDistanceGoal.text.toString()).toDouble()
        userProfileEdit!!.image

        //app.users.updateAccount(user.copy())
        updateUserProfile(app.auth.currentUser!!.uid, userProfileEdit!!)
    }



    private fun resetPassword(){
        loader = createLoader(activity!!)
        showLoader(loader, "Sending Reset Email . . . ")
        val user = FirebaseAuth.getInstance().currentUser
        FirebaseAuth.getInstance().sendPasswordResetEmail(user!!.email.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Email sent.")
                    root.resetStatus.text = "Reset Email Sent!"
                    root.resetStatus.setTextColor(Color.parseColor("#009705"))
                } else {
                    root.resetStatus.text = "An Error Has Occurred!"
                    root.resetStatus.setTextColor(Color.parseColor("#970000"))
                }
            }
        hideLoader(loader)
    }

    private fun reauthenticateUser(email: String, password: String) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val authCredential = EmailAuthProvider.getCredential(email, password)
        firebaseUser!!.reauthenticate(authCredential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    userProfileEdit!!.firstName = root.editFirstName.text.toString()
                    userProfileEdit!!.surname = root.editSurname.text.toString()
                    userProfileEdit!!.username = root.editUsername.text.toString()
                    userProfileEdit!!.Email = root.editEmail.text.toString()
                    userProfileEdit!!.stepsGoal = (root.editStepsGoal.text.toString()).toInt()
                    userProfileEdit!!.distanceGoal = (root.editDistanceGoal.text.toString()).toDouble()
                    userProfileEdit!!.phoneNo = root.editPhoneNo.text.toString()
                    userProfileEdit!!.weight = root.editWeight.text.toString().toDouble()
                    userProfileEdit!!.drivingDistanceGoal = (root.editDrivingDistanceGoal.text.toString()).toDouble()
                    userProfileEdit!!.cyclingDistanceGoal = (root.editCyclingDistanceGoal.text.toString()).toDouble()
                    userProfileEdit!!.image

                    //app.users.updateAccount(user.copy())
                    updateUserProfile(app.auth.currentUser!!.uid, userProfileEdit!!)
                    val toast =
                        Toast.makeText(
                            activity!!.applicationContext,
                            "Profile Updated!",
                            Toast.LENGTH_LONG
                        )
                    toast.show()
                    navigateTo(StatisticsFragment.newInstance(userProfileEdit!!))
                } else {
                    val toast =
                        Toast.makeText(
                            activity!!.applicationContext,
                            "Something Went Wrong: Please check your password and try again!",
                            Toast.LENGTH_LONG
                        )
                    toast.show()
                }
            }
    }

    val home = Home()



    //handle result of picked image




    fun updateUserProfile(uid: String?, user: Account) {
        app.database.child("user-stats").child(uid!!)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.setValue(user)
                        activity!!.supportFragmentManager.beginTransaction()
                            .replace(R.id.homeFrame, ProfileFragment.newInstance(user!!))
                            .addToBackStack(null)
                            .commit()
                        //hideLoader(loader)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        //info("Firebase Donation error : ${error.message}")
                    }
                })
    }

    companion object {
        @JvmStatic
        fun newInstance(user: Account) =
            ProfileEditFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("user-profile-edit",user)
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
