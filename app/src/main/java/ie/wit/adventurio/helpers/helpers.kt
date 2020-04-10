package ie.wit.adventurio.helpers

import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import ie.wit.adventurio.R

fun createLoader(activity: FragmentActivity) : AlertDialog {
 val loaderBuilder = AlertDialog.Builder(activity)
         .setCancelable(true) // 'false' if you want user to wait
         .setView(R.layout.loading)
 var loader = loaderBuilder.create()
 loader.setTitle(R.string.app_name)

 return loader
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


