package ie.wit.adventurio.fragments

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import ie.wit.adventurio.R
import ie.wit.adventurio.activities.LoginActivity
import ie.wit.adventurio.helpers.readImageFromPath
import ie.wit.adventurio.main.MainApp
import ie.wit.adventurio.models.Account
import kotlinx.android.synthetic.main.fragment_profile.view.*


class ProfileFragment : Fragment() {

    lateinit var app: MainApp
    var user = Account()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_profile, container, false)
        activity?.title = getString(R.string.menu_prof)


        val bundle = arguments
        if (bundle != null) {
            user = bundle.getParcelable("user_key")
        }

        root.txtNameProf.text = user.firstName + " " + user.surname
        root.txtEmailProf.text = user.Email
        root.txtUsernameProf.text = user.username
        root.txtStepsProf.text = user.stepsGoal.toString()
        root.txtPhoneProf.text = user.phoneNo
        root.txtDistanceProf.text = user.distanceGoal.toString()+"km"
        root.imageView.setImageBitmap(readImageFromPath(this.requireContext(), user.image))

        root.editProfileFab.setOnClickListener {
            navigateTo(ProfileEditFragment.newInstance(user))
        }

        root.deleteProfileFab.setOnClickListener {
            app.users.deleteAccount(user)
            val toast =
                Toast.makeText(
                    activity!!.applicationContext,
                    "Account Removed! Application Restarting . . .",
                    Toast.LENGTH_LONG
                )
            toast.show()
            restartApp()
        }

        return root
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
        fun newInstance(account:Account) =
            ProfileFragment().apply {
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
