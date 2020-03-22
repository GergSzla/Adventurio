package ie.wit.adventurio.fragments

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import ie.wit.adventurio.R
import ie.wit.adventurio.activities.Home
import ie.wit.adventurio.helpers.createLoader
import ie.wit.adventurio.helpers.hideLoader
import ie.wit.adventurio.helpers.readImageFromPath
import ie.wit.adventurio.helpers.showLoader
import ie.wit.adventurio.main.MainApp
import ie.wit.adventurio.models.Account
import kotlinx.android.synthetic.main.fragment_profile_edit.*
import kotlinx.android.synthetic.main.fragment_profile_edit.view.*
import java.io.IOException


class ProfileEditFragment : Fragment() {


    lateinit var app: MainApp
    //var user = Account()
    val IMAGE_REQUEST = 1
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

        root.editFirstName.setText(userProfileEdit!!.firstName)
        root.editSurname.setText(userProfileEdit!!.surname)
        root.editEmail.setText(userProfileEdit!!.Email)
        root.editPhoneNo.setText(userProfileEdit!!.phoneNo)
        root.editUsername.setText(userProfileEdit!!.username)
        root.editStepsGoal.setText(userProfileEdit!!.stepsGoal.toString())
        root.editDistanceGoal.setText(userProfileEdit!!.distanceGoal.toString())
        root.profImage.setImageBitmap(readImageFromPath(this.requireContext(), userProfileEdit!!.image))
        if (userProfileEdit!!.image != "") {
            root.addImage.setText(R.string.btnChangeImage)
        }else{
            root.addImage.setText(R.string.btnAddImg)
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

        root.addImage.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_REQUEST)
        }

        root.updateProfileFab.setOnClickListener {
            reauthenticateUser(app.auth.currentUser!!.email.toString(),root.editPassword.text.toString())
        }

        root.btnResetPass.setOnClickListener {
            resetPassword()
        }

        return root
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (!(requestCode !== IMAGE_REQUEST || resultCode !== Activity.RESULT_OK || data == null || data.data == null)) {
            val uri: Uri = data.data
            try {
                val bitmap =
                    MediaStore.Images.Media.getBitmap(activity!!.contentResolver, uri)
                // Log.d(TAG, String.valueOf(bitmap));
                val imageView: ImageView =
                    activity!!.findViewById<View>(R.id.profImage) as ImageView
                imageView.setImageBitmap(bitmap)
                userProfileEdit!!.image = data.data.toString()
                addImage.setText(R.string.btnChangeImage)

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }



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
