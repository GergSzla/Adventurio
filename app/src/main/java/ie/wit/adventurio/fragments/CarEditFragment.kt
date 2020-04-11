package ie.wit.adventurio.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import ie.wit.adventurio.R
import ie.wit.adventurio.helpers.*
import ie.wit.adventurio.main.MainApp
import ie.wit.adventurio.models.Account
import ie.wit.adventurio.models.Vehicle
import ie.wit.fragments.CarsListFragment
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.card_vehicle.view.*
import kotlinx.android.synthetic.main.fragment_add_vehicle.view.*
import kotlinx.android.synthetic.main.fragment_add_vehicle.view.vehicleImage
import kotlinx.android.synthetic.main.fragment_car_edit.view.*
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

        root.vehicleImage.setImageBitmap(readImageFromPath(activity!!, vehicle!!.vehicleImage))

        if (vehicle.vehicleImage == ""){
            root.vehicleImage.setImageBitmap(readImageFromPath(activity!!, vehicle!!.vehicleImage))
        } else {
            Picasso.get().load(vehicle.vehicleImage)
                .fit()
                .centerInside()
                .transform(RoundedCornersTransformation(50,0))
                .into(root.vehicleImage)
        }

        root.editVehicleFab.setOnClickListener {
            validateForm()
            if(!(root.editVehOdo.text.toString() == "" ||
                        root.editVehName.text.toString() == "" ||
                        root.editVehBrand.text.toString() == "" ||
                        root.editVehModel.text.toString() == "" ||
                        root.editVehReg.text.toString() == "" ||
                        root.editVehYear.text.toString() == ""||
                        root.editVehTankCapac.text.toString() == "")) {

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

        }

        root.addCarImage.setOnClickListener {
            showImagePicker(this, IMAGE_REQUEST)
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
                val imageView: ImageView =
                    activity!!.findViewById<View>(R.id.vehicleImage) as ImageView
                imageView.setImageBitmap(bitmap)
                vehicle!!.vehicleImage = data.data.toString()
                root.addCarImage.setText(R.string.btnChangeImage)

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun validateForm(): Boolean {
        var valid = true

        //validate Name
        val carname = root.editVehName.text.toString()
        if (TextUtils.isEmpty(carname)) {
            root.editVehName.error = "Required."
            valid = false
        } else {
            root.editVehName.error = null
        }

        //validate Brand
        val brand = root.editVehBrand.text.toString()
        if (TextUtils.isEmpty(brand)) {
            root.editVehBrand.error = "Required (To Calculate Calories Burned!)."
            valid = false
        } else {
            root.editVehBrand.error = null
        }

        //validate model
        val model = root.editVehModel.text.toString()
        if (TextUtils.isEmpty(model)) {
            root.editVehModel.error = "Required."
            valid = false
        } else {
            root.editVehModel.error = null
        }

        //validate reg
        val reg = root.editVehReg.text.toString()
        if (TextUtils.isEmpty(reg)) {
            root.editVehReg.error = "Required."
            valid = false
        } else {
            root.editVehReg.error = null
        }

        //validate year
        val year = root.editVehYear.text.toString()
        if (TextUtils.isEmpty(year)) {
            root.editVehYear.error = "Required."
            valid = false
        } else {
            root.editVehYear.error = null
        }

        //validated tank
        val tank = root.editVehTankCapac.text.toString()
        if (TextUtils.isEmpty(tank)) {
            root.editVehTankCapac.error = "Required."
            valid = false
        } else {
            root.editVehTankCapac.error = null
        }

        //validated odo
        val odo = root.editVehOdo.text.toString()
        if (TextUtils.isEmpty(odo)) {
            root.editVehOdo.error = "Required."
            valid = false
        } else {
            root.editVehOdo.error = null
        }
        return valid
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

