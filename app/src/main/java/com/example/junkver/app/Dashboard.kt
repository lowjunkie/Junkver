package com.example.junkver.app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.junkver.R
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView

class Dashboard:AppCompatActivity() {
    private lateinit var fullscreenContent: TextView
    private lateinit var fullscreenContentControls: LinearLayout
    private val hideHandler = Handler()
    @SuppressLint("InlinedApi")
    private val hidePart2Runnable = Runnable {

        fullscreenContent.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LOW_PROFILE or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }
    private val showPart2Runnable = Runnable {
        supportActionBar?.show()
        fullscreenContentControls.visibility = View.VISIBLE
    }
    private var isFullscreen: Boolean = false
    private var hideRunnable = Runnable { hide() }



    lateinit var auth : FirebaseAuth

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("tab","out dash")

        setContentView(R.layout.dashboard)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        isFullscreen = true

        fullscreenContent = findViewById(R.id.fullscreen_content)

        fullscreenContentControls = findViewById(R.id.fullscreen_content_controls)

        auth = FirebaseAuth.getInstance()

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
       val navView: NavigationView = findViewById(R.id.nav_view)


        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, 0, 0
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
//        verifyUser(auth)
        var swipe = navView.getHeaderView(0)
        var swipename = swipe.findViewById<TextView>(R.id.swipename)
        var swipephoto = swipe.findViewById<CircleImageView>(R.id.swipephoto)
        val selecturi = auth.currentUser?.photoUrl

        swipename.text = auth.currentUser?.displayName
        Glide.with(this).load(selecturi).into(swipephoto)
        val navController = findNavController(R.id.nav_host_fragment)


        navView.setupWithNavController(navController)




    }

    override fun onBackPressed() {
        super.onBackPressed()
        moveTaskToBack(true)
    }
    override fun onResume() {
        super.onResume()
        delayedHide(100)
    }










    private fun verifyUser(auth : FirebaseAuth){
        val uid = auth.uid
        if(uid == null){
            val intent = Intent(this, Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }





    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)


        delayedHide(100)
    }



    private fun hide() {
        supportActionBar?.hide()
        fullscreenContentControls.visibility = View.GONE
        isFullscreen = false
        Log.d("Lj","hide")

        hideHandler.removeCallbacks(showPart2Runnable)
        hideHandler.postDelayed(hidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }




    private fun delayedHide(delayMillis: Int) {
        hideHandler.removeCallbacks(hideRunnable)
        hideHandler.postDelayed(hideRunnable, delayMillis.toLong())
    }

    companion object {


        private const val UI_ANIMATION_DELAY = 300
    }
}