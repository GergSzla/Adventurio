package ie.wit.adventurio.fragments

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import ie.wit.adventurio.R
import ie.wit.adventurio.helpers.readImage
import ie.wit.adventurio.helpers.readImageFromPath
import ie.wit.adventurio.helpers.showImagePicker
import ie.wit.adventurio.main.MainApp
import ie.wit.adventurio.models.Account
import kotlinx.android.synthetic.main.fragment_profile_edit.*
import kotlinx.android.synthetic.main.fragment_profile_edit.view.*


class ProfileEditFragment : Fragment() {


    lateinit var app: MainApp
    var user = Account()
    val IMAGE_REQUEST = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_profile_edit, container, false)
        activity?.title = getString(R.string.editProf)

        val bundle = arguments
        if (bundle != null) {
            user = bundle.getParcelable("user_key")
        }

        root.editFirstName.setText(user.firstName)
        root.editSurname.setText(user.surname)
        root.editEmail.setText(user.Email)
        root.editPhoneNo.setText(user.phoneNo)
        root.editPassword.setText(user.Password)
        root.editUsername.setText(user.username)
        root.editStepsGoal.setText(user.stepsGoal.toString())
        root.editDistanceGoal.setText(user.distanceGoal.toString())
        root.profImage.setImageBitmap(readImageFromPath(this.requireContext(), user.image))
        if (user.image != "") {
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
            showImagePicker(this, IMAGE_REQUEST)
        }

        root.updateProfileFab.setOnClickListener {
            if(root.editPassword.text.toString() == root.editPasswordConf.text.toString()){
                user.firstName = root.editFirstName.text.toString()
                user.surname = root.editSurname.text.toString()
                user.username = root.editUsername.text.toString()
                user.Email = root.editEmail.text.toString()
                user.stepsGoal = (root.editStepsGoal.text.toString()).toInt()
                user.distanceGoal = (root.editDistanceGoal.text.toString()).toDouble()
                user.Password = root.editPassword.text.toString()
                user.phoneNo = root.editPhoneNo.text.toString()
                user.image
                app.users.updateAccount(user.copy())
                val toast =
                    Toast.makeText(
                        activity!!.applicationContext,
                        "Profile Updated!",
                        Toast.LENGTH_LONG
                    )
                toast.show()
                navigateTo(StatisticsFragment.newInstance(user))

            }else{
                val toast =
                    Toast.makeText(
                        activity!!.applicationContext,
                        "Passwords Don't Match!",
                        Toast.LENGTH_LONG
                    )
                toast.show()
            }
        }

        return root
    }




    companion object {
        @JvmStatic
        fun newInstance(account: Account) =
            ProfileEditFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("user_key", account)
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
