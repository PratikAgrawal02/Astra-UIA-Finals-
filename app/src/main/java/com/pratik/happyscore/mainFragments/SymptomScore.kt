package com.pratik.happyscore.mainFragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.pratik.happyscore.R
import com.pratik.happyscore.databinding.FragmentSymptomScoreBinding


class SymptomScore : Fragment() {

    private var _binding: FragmentSymptomScoreBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: DatabaseReference
    //Current User's data
    private lateinit var userName : String
    private lateinit var userEmail : String
    private lateinit var userPhone : String
    private lateinit var userPosition: String
    private lateinit var userType: String
    private lateinit var userID: String
    private var userPrescription: String = "false"
    var possiblity:Float = 0f
    private lateinit var sharedPreference : SharedPreferences

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSymptomScoreBinding.inflate(inflater,container,false)

        firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth.currentUser
        db = FirebaseDatabase.getInstance().reference
        sharedPreference = requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE)

        getDataFromSharedPreference()
        updatescore()
        binding.updatesymptoms.setOnClickListener {
            startActivity(Intent(requireActivity(),PickSymptoms::class.java))
        }

       // db.child("Users").child(firebaseAuth.uid.toString()).child("contributions").child("symptomcontri").setValue(0)
        db.child("Users").child(firebaseAuth.uid.toString()).child("contributions").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                possiblity = 0f
                for (datasnapshot : DataSnapshot in snapshot.children){
                    possiblity += datasnapshot.value.toString().toFloat()
                }
                updatescore()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        binding.viewreport.setOnClickListener {

        }

        return binding.root
    }

    private fun updatescore() {
        binding.yespercenttext.text = "${"%.2f".format(possiblity)}%"
        binding.nopercenttext.text = "${"%.2f".format(100f - possiblity)}%"
        if (possiblity<25f){
            binding.tumorstatus.text = getString(R.string.No_Tumor)
            binding.tumorstatus.setTextColor(ContextCompat.getColorStateList(requireContext(),R.color.green))
        }
        else if (possiblity<50f){
            binding.tumorstatus.text = getString(R.string.see_a_doctor)
            binding.tumorstatus.setTextColor(ContextCompat.getColorStateList(requireContext(),
                android.R.color.holo_green_dark
            ))
        }
        else if (possiblity<75f){
            binding.tumorstatus.text = getString(R.string.dangerous_tumor)
            binding.tumorstatus.setTextColor(ContextCompat.getColorStateList(requireContext(),R.color.orange))
        }
        else{
            binding.tumorstatus.text = getString(R.string.tumor_detected)
            binding.tumorstatus.setTextColor(ContextCompat.getColorStateList(requireContext(),R.color.red))
        }
    }

    @SuppressLint("SetTextI18n", "CommitPrefEdits")
    private fun getDataFromSharedPreference() {
        userID = sharedPreference.getString("uid","Not found").toString()
        userName = sharedPreference.getString("name","Not found").toString()
        userEmail = sharedPreference.getString("email","Not found").toString()
        userPhone = sharedPreference.getString("phone","Not found").toString()
        userPosition = sharedPreference.getString("isDoctor", "Not fount").toString()
        userPrescription = sharedPreference.getString("prescription", "false").toString()


        if (userPosition == "Doctor")
            binding.namePreview.text = "Dr. $userName"
        else
            binding.namePreview.text = userName

        binding.Usercode.setOnClickListener {
            val intent: Intent = Intent(requireActivity(),UserCard::class.java)
            intent.putExtra("uid",firebaseAuth.uid.toString())
            intent.putExtra("name",userName)
            startActivity(intent)
        }

    }


}