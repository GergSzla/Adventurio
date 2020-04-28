package ie.wit.adventurio.helpers

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import com.squareup.picasso.Picasso
import ie.wit.adventurio.R
import ie.wit.adventurio.main.MainApp
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import java.io.ByteArrayOutputStream
import java.io.IOException

fun createLoader(activity: FragmentActivity) : AlertDialog {
 val loaderBuilder = AlertDialog.Builder(activity)
         .setCancelable(true) // 'false' if you want user to wait
         .setView(R.layout.loading)
 var loader = loaderBuilder.create()
 loader.setTitle(R.string.app_name)

 return loader
}

fun uploadImageView(app: MainApp, imageView: ImageView) {
 // Get the data from an ImageView as bytes
 val uid = app.auth.currentUser!!.uid
 val imageRef = app.storage.child("photos").child("${uid}.jpg")
 val bitmap = (imageView.drawable as BitmapDrawable).bitmap
 val baos = ByteArrayOutputStream()

 bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
 val data = baos.toByteArray()

 var uploadTask = imageRef.putBytes(data)
}

fun uploadVehicleImageView(app: MainApp, imageView: ImageView, vehID: String) {
 // Get the data from an ImageView as bytes
 val uid = app.auth.currentUser!!.uid
 val imageRef = app.storage.child("vehicleImages").child("${vehID}.jpg")
 val bitmap = (imageView.drawable as BitmapDrawable).bitmap
 val baos = ByteArrayOutputStream()

 bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
 val data = baos.toByteArray()

 var uploadTask = imageRef.putBytes(data)
}

fun readImageUri(resultCode: Int, data: Intent?): Uri? {
 var uri: Uri? = null
 if (resultCode == Activity.RESULT_OK && data != null && data.data != null) {
  try { uri = data.data }
  catch (e: IOException) {
   e.printStackTrace()
  }
 }
 return uri
}

fun showLoader(loader: AlertDialog, message: String) {
 if (!loader.isShowing()) {
  loader.setTitle(message)
  loader.show()
 }
}



fun hideLoader(loader: AlertDialog) {
 if (loader.isShowing())
  loader.dismiss()
}


