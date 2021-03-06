package com.example.junkver.app

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.junkver.R
import com.example.junkver.fragments.TOPIC
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.dashboard_bar.*

class Dashboard:AppCompatActivity() {

    lateinit var fireStore : FirebaseFirestore


    var clicked = false

    var SID = ""
    var joinID = ""
    var num = 0
    lateinit var auth : FirebaseAuth
    var selectUri : Uri?= null

    lateinit var  swipephoto : CircleImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.dashboard)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        Log.d("finalI","oncreate")
        fireStore = FirebaseFirestore.getInstance()




        auth = FirebaseAuth.getInstance()
        toolbar.inflateMenu(R.menu.inside_menu)
        toolbar.menu.findItem(R.id.notify).setVisible(false)
        toolbar.menu.findItem(R.id.shareLink).setVisible(false)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
       val navView: NavigationView = findViewById(R.id.nav_view)


        val toggle = ActionBarDrawerToggle(
            this, drawerLayout,toolbar, 0, 0
        )


        drawerLayout.addDrawerListener(toggle)
       toggle.syncState()


        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
        var swipe = navView.getHeaderView(0)
        var swipename = swipe.findViewById<TextView>(R.id.swipename)
        swipephoto = swipe.findViewById<CircleImageView>(R.id.swipephoto)
        subscribeToDp()


        swipename.text = auth.currentUser?.displayName

        val navController = findNavController(R.id.nav_host_fragment)


        navView.setupWithNavController(navController)

    }


    override fun overridePendingTransition(enterAnim: Int, exitAnim: Int) {
        super.overridePendingTransition(enterAnim, R.anim.slide_out_left)
    }

    override fun onResume() {
        Log.d("finalI","onresume")
        super.onResume()
    }

    private var doubleBack : Boolean = false
    override fun onBackPressed() {
        if(num==0){
        if(doubleBack){

            finish()
            System.exit(0)
        }
        doubleBack = true
        Toast.makeText(this,"Press again to exit",Toast.LENGTH_SHORT).show()
        Handler().postDelayed(Runnable { doubleBack = false }, 2000)
    }
        else{
            if(clicked){
                super.onBackPressed()
                clicked = false

            }
            else {
                if(num == 1) {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.action_insideFragment_to_existing)
                    toolbar.menu.findItem(R.id.notify).setVisible(false)
                    toolbar.menu.findItem(R.id.shareLink).setVisible(false)
                    num -=1

                }
                else if (num ==2){
                    val bundle = Bundle().apply {
                        putString("joinID",joinID)
                        putString("SID",SID)
                    }
                    findNavController(R.id.nav_host_fragment).navigate(R.id.action_groupInfo_to_insideFragment,bundle)
                    num -=1

                }
                else{
                    super.onBackPressed()
                    num = 0

                }

            }

        }
    }















    private fun subscribeToDp(){
        fireStore.collection("persons").document(auth.uid.toString()).addSnapshotListener {
                documentSnapshot, firebaseFirestoreException ->

            firebaseFirestoreException?.let {

                Log.d("shameer",it.message)
                return@addSnapshotListener
            }

            documentSnapshot?.let {document->
                val data = document.data?.get("photoUri").toString()

                selectUri = Uri.parse(data)
                if(selectUri!= null){
                    Glide.with(this)
                        .load(selectUri)
                        .into(swipephoto)
                }




            }
        }

    }





}