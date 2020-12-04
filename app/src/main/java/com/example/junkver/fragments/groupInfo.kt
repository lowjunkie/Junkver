package com.example.junkver.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.junkver.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_f_signup.*
import kotlinx.android.synthetic.main.fragment_group_info.*
import kotlinx.android.synthetic.main.fragment_server.*


class groupInfo : Fragment() {



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_group_info, container, false)
    }
    var selecturi : Uri?= null
    lateinit var storageRef : FirebaseStorage
    lateinit var fireStore : FirebaseFirestore
    lateinit var joinID : String
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        storageRef = FirebaseStorage.getInstance()

        hidebar()
        fireStore = FirebaseFirestore.getInstance()

        groupTV.isEnabled = false

        groupEnable.setOnClickListener {
            groupTV.isEnabled = true
        }

        groupInfoPhotoBtn.setOnClickListener {

            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.type = "image/*"
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            startActivityForResult(Intent.createChooser(intent, "Select image"),7);


        }
        joinID = arguments?.getString("joinID").toString()

        groupButton.setOnClickListener {
            val view = activity?.currentFocus
            view?.let { v ->
                val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(v.windowToken, 0)
            }
            if(groupTV.text.isNotEmpty()){
                showbar()
                updateGroupInfo(joinID,groupTV.text.toString())


            }


        }




    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if(requestCode == 7 && resultCode == Activity.RESULT_OK && data != null ){
            selecturi = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, selecturi)
            groupLogo.setImageBitmap(bitmap)

        }
    }

    private fun updateGroupInfo(server : String, servername : String){
        val  serverRef = fireStore.collection("servers").document(server)

        val imageRef = storageRef.reference.child(server)

        selecturi?.let {
            val ref = imageRef
            ref.putFile(it).addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener {download->
                        serverRef.update("serverUri",download.toString()).addOnSuccessListener {
                            serverRef.update("SID",servername).addOnSuccessListener {
                                hidebar()
                            }.addOnFailureListener {
                                hidebar()
                                Toast.makeText(activity,it.message,Toast.LENGTH_SHORT).show()

                            }

                        }.addOnFailureListener {
                            hidebar()
                            Toast.makeText(activity,"photo eh" + it.message,Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }

    }

    private fun showbar(){
        groupBar.visibility = View.VISIBLE
        groupButton.isEnabled = false

    }

    private fun hidebar(){
        groupBar.visibility = View.INVISIBLE
        groupButton.isEnabled = true
    }

    override fun onResume() {
        super.onResume()
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

    }

    override fun onPause() {
        super.onPause()
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        // Clear the systemUiVisibility flag
        activity?.window?.decorView?.systemUiVisibility = 0
    }





}