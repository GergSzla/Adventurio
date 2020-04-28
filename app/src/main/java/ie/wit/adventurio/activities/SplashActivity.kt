package ie.wit.adventurio.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import ie.wit.adventurio.R
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.android.synthetic.main.fragment_statistics.view.*

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.requestFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_splash)

        val animIn = AnimationUtils.loadAnimation(this,R.anim.fade_in)
        splashLayout.startAnimation(animIn) // 1 .. 2 ..

        Handler().postDelayed({
            val animOut = AnimationUtils.loadAnimation(this,R.anim.fade_out)
            splashLayout.startAnimation(animOut)
        },3000) // 3.. 4..

        Handler().postDelayed({
            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            finish()
        },4500) // 4.5 ..

    }
}
