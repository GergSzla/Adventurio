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
import android.widget.ArrayAdapter
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
import ie.wit.adventurio.models.Trip
import ie.wit.adventurio.models.Vehicle
import ie.wit.fragments.CarsListFragment
import kotlinx.android.synthetic.main.fragment_add_vehicle.view.*
import kotlinx.android.synthetic.main.fragment_add_vehicle.view.vehicleImage
import kotlinx.android.synthetic.main.fragment_car_edit.view.*
import kotlinx.android.synthetic.main.fragment_profile_edit.*
import kotlinx.android.synthetic.main.fragment_profile_edit.view.*
import kotlinx.android.synthetic.main.fragment_profile_edit.view.addImage
import java.io.IOException


class CarEditFragment : Fragment() {


    lateinit var app: MainApp
    //var user = Account()
    var vehicle = Vehicle()
    val IMAGE_REQUEST = 1
    var userProfileEdit: Account? = null
    lateinit var loader : AlertDialog
    lateinit var root: View


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp

        val bundle = arguments
        if (bundle != null) {
            vehicle = bundle.getParcelable("vehicle_key")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_car_edit, container, false)
        activity?.title = getString(R.string.editCar)

        val categories = arrayOf("Petrol","Diesel","Electric")

        val adapter = ArrayAdapter(
            activity!!, // Context
            android.R.layout.simple_spinner_item, // Layout
            categories // Array
        )

        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)

        root.spinnerVehFuel.adapter = adapter;

        root.editVehName.setText(vehicle.vehicleName)
        root.editVehBrand.setText(vehicle.vehicleBrand)
        root.editVehModel.setText(vehicle.vehicleModel)
        root.editVehReg.setText(vehicle.vehicleReg)
        root.editVehYear.setText(vehicle.vehicleYear)
        root.editVehTankCapac.setText(vehicle.tankCapacity.toString())
        root.editVehOdo.setText(vehicle.currentOdometer.toString())

        root.vehicleImage.setImageBitmap(readImageFromPath(this.requireContext(), vehicle!!.vehicleImage))
        if (vehicle!!.vehicleImage != "") {
            root.addImage.setText(R.string.btnChangeImage)
        }else{
            root.addImage.setText(R.string.btnAddImg)
        }

        root.editVehicleFab.setOnClickListener {
            vehicle.vehicleName = root.editVehName.text.toString()
            vehicle.vehicleBrand = root.editVehBrand.text.toString()
            vehicle.vehicleModel = root.editVehModel.text.toString()
            vehicle.vehicleReg = root.editVehReg.text.toString()
            vehicle.fuelType = root.spinnerVehFuel.selectedItem.toString()
            vehicle.vehicleYear = root.editVehYear.text.toString()
            vehicle.tankCapacity = root.editVehTankCapac.text.toString().toDouble()
            vehicle.currentOdometer = root.editVehOdo.text.toString().toInt()
            updateVehicle(app.auth.currentUser!!.uid)
        }

        return root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (!(requestCode !== IMAGE_REQUEST || resultCode !== Activity.RESULT_OK || data == null || data.data == null)) {
            val uri: Uri = data.data!!
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



    fun updateVehicle(uid: String?) {
        app.database.child("user-stats").child(uid!!).child("vehicles").child(vehicle.pos)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.setValue(vehicle)
                        activity!!.supportFragmentManager.beginTransaction()
                            .replace(R.id.homeFrame, CarsListFragment.newInstance())
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
        fun newInstance(vehicle: Vehicle) =
            CarEditFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("vehicle_key",vehicle)
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

