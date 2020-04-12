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
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ie.wit.adventurio.R
import ie.wit.adventurio.helpers.showImagePicker
import ie.wit.adventurio.main.MainApp
import ie.wit.adventurio.models.Account
import ie.wit.adventurio.models.Vehicle
import ie.wit.fragments.CarsListFragment
import kotlinx.android.synthetic.main.fragment_add_vehicle.view.*
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class AddVehicleFragment : Fragment() {

    var user = Account()
    var vehicle = Vehicle()
    lateinit var app: MainApp
    lateinit var root: View
    val IMAGE_REQUEST = 1
    lateinit var eventListener : ValueEventListener
    var autoId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_add_vehicle, container, false)
        activity?.title = getString(R.string.menu_add_car)


        //https://android--code.blogspot.com/2018/02/android-kotlin-spinner-example.html
        val categories = arrayOf("Petrol","Diesel","Electric")

        val adapter = ArrayAdapter(
            activity!!, // Context
            android.R.layout.simple_spinner_item, // Layout
            categories // Array
        )

        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)

        root.spinnerFuel.adapter = adapter;

        root.spinnerFuel.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent:AdapterView<*>, view: View, position: Int, id: Long){

            }

            override fun onNothingSelected(parent: AdapterView<*>){
                // Another interface callback
            }
        }

        getUser()

        root.addVehicleFab.setOnClickListener {
            validateForm()
            if(!(root.editName.text.toString() == "" ||
                        root.editOdo.text.toString() == "" ||
                        root.editReg.text.toString() == "" ||
                        root.editBrand.text.toString() == "" ||
                        root.editModel.text.toString() == "" ||
                        root.editYear.text.toString() == ""||
                        root.editTankCapac.text.toString() == "")) {

                vehicle.vehicleOwner = user.id
                vehicle.currentOdometer = root.editOdo.text.toString().toInt()
                vehicle.vehicleName = root.editName.text.toString()
                vehicle.vehicleReg = root.editReg.text.toString().toUpperCase()
                vehicle.vehicleBrand = root.editBrand.text.toString()
                vehicle.vehicleModel = root.editModel.text.toString()
                vehicle.vehicleYear = root.editYear.text.toString()
                vehicle.tankCapacity = root.editTankCapac.text.toString().toDouble()
                vehicle.fuelType = root.spinnerFuel.selectedItem.toString()
                generateDateID()
                vehicle.vehicleId = autoId

                user.vehicles.add(vehicle)
                vehicle.pos = (user.vehicles.size - 1).toString()
                user.vehicles.removeAt(user.vehicles.size - 1)
                user.vehicles.add(vehicle)

                updateUserProfile(app.auth.currentUser!!.uid,user)
            }

        }

        root.addImageAdd.setOnClickListener {
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
                    activity!!.findViewById<View>(R.id.vehicleImageAdd) as ImageView
                imageView.setImageBitmap(bitmap)
                vehicle!!.vehicleImage = data.data.toString()
                root.addImageAdd.setText(R.string.btnChangeImage)

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun validateForm(): Boolean {
        var valid = true

        //validate Name
        val carname = root.editName.text.toString()
        if (TextUtils.isEmpty(carname)) {
            root.editName.error = "Required."
            valid = false
        } else {
            root.editName.error = null
        }

        //validate Brand
        val brand = root.editBrand.text.toString()
        if (TextUtils.isEmpty(brand)) {
            root.editBrand.error = "Required (To Calculate Calories Burned!)."
            valid = false
        } else {
            root.editBrand.error = null
        }

        //validate model
        val model = root.editModel.text.toString()
        if (TextUtils.isEmpty(model)) {
            root.editModel.error = "Required."
            valid = false
        } else {
            root.editModel.error = null
        }

        //validate reg
        val reg = root.editReg.text.toString()
        if (TextUtils.isEmpty(reg)) {
            root.editReg.error = "Required."
            valid = false
        } else {
            root.editReg.error = null
        }

        //validate year
        val year = root.editYear.text.toString()
        if (TextUtils.isEmpty(year)) {
            root.editYear.error = "Required."
            valid = false
        } else {
            root.editYear.error = null
        }

        //validated tank
        val tank = root.editTankCapac.text.toString()
        if (TextUtils.isEmpty(tank)) {
            root.editTankCapac.error = "Required."
            valid = false
        } else {
            root.editTankCapac.error = null
        }

        //validated odo
        val odo = root.editOdo.text.toString()
        if (TextUtils.isEmpty(odo)) {
            root.editOdo.error = "Required."
            valid = false
        } else {
            root.editOdo.error = null
        }

        return valid
    }

    fun updateUserProfile(uid: String?, user: Account) {
        app.database.child("user-stats").child(uid!!)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.setValue(user)
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

    fun generateDateID(){
        var currentEndDateTime= LocalDateTime.now()
        var year = Calendar.getInstance().get(Calendar.YEAR).toString()
        var month = ""
        if (Calendar.getInstance().get(Calendar.MONTH)+1 < 10){
            month = "0"+(Calendar.getInstance().get(Calendar.MONTH)+1).toString()
        }else{
            month = (Calendar.getInstance().get(Calendar.MONTH)+1).toString()
        }
        var day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH).toString()
        var hour = currentEndDateTime.format(DateTimeFormatter.ofPattern("HH")).toString()
        var minutes = currentEndDateTime.format(DateTimeFormatter.ofPattern("mm")).toString()
        var seconds = currentEndDateTime.format(DateTimeFormatter.ofPattern("ss")).toString()


        autoId = year+month+day+"-"+vehicle.vehicleBrand.toUpperCase()+"-"+hour+minutes+seconds
    }




    fun getUser(){
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val rootRef = FirebaseDatabase.getInstance().reference
        val uidRef = rootRef.child("user-stats").child(uid)
        eventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                user = dataSnapshot.getValue(Account::class.java)!!
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        uidRef.addListenerForSingleValueEvent(eventListener)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            AddVehicleFragment().apply {
                arguments = Bundle().apply {
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
