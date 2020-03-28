package ie.wit.adventurio.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.NumberPicker
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ie.wit.adventurio.R
import ie.wit.adventurio.main.MainApp
import ie.wit.adventurio.models.Account
import ie.wit.adventurio.models.Trip
import ie.wit.adventurio.models.Vehicle
import ie.wit.fragments.TripsListFragment
import kotlinx.android.synthetic.main.fragment_add_vehicle.view.*
import kotlinx.android.synthetic.main.fragment_manual_trip.*
import kotlinx.android.synthetic.main.fragment_manual_trip.view.*
import kotlinx.android.synthetic.main.fragment_walking_trips_edit.view.amountPickerHours1
import kotlinx.android.synthetic.main.fragment_walking_trips_edit.view.amountPickerHours2
import kotlinx.android.synthetic.main.fragment_walking_trips_edit.view.amountPickerMinutes1
import kotlinx.android.synthetic.main.fragment_walking_trips_edit.view.amountPickerMinutes2
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class AddVehicleFragment : Fragment() {

    var user = Account()
    var vehicle = Vehicle()
    lateinit var app: MainApp
    lateinit var root: View
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

            //vehicle.vehicleImage  //TODO: FIX IMAGE
            updateUserProfile(app.auth.currentUser!!.uid,user)
        }

        return root
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
