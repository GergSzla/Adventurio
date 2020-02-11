package ie.wit.adventurio.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ie.wit.adventurio.R
import ie.wit.adventurio.helpers.readImageFromPath
import ie.wit.adventurio.main.MainApp
import ie.wit.adventurio.models.Account
import kotlinx.android.synthetic.main.fragment_profile.view.*


class ProfileFragment : Fragment() {

    lateinit var app: MainApp
    //var intent = Intent(activity, LoginActivity::class.java)
    var user = Account()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp

        val bundle = arguments
        if (bundle != null) {
            user = bundle.getParcelable("user_key")
            user
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_profile, container, false)
        activity?.title = getString(R.string.menu_prof)

        //if (intent.hasExtra("userLoggedIn")) {
            //user = intent.extras.getParcelable<Account>("userLoggedIn")
        root.txtNameProf.text = user.firstName + " " + user.surname
        root.txtEmailProf.text = user.Email
        root.txtUsernameProf.text = user.username
        root.imageView.setImageBitmap(readImageFromPath(this.requireContext(), user.image))
        //}

        /*btnDelAccount.setOnClickListener {
            app.users.deleteAccount(user)
        }*/

        return root
    }



    companion object {
        @JvmStatic
        fun newInstance() =
            ProfileFragment().apply {
                arguments = Bundle().apply { }
            }
    }
}
