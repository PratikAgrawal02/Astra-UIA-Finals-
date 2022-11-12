package com.pratik.happyscore.mainFragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.firebase.FirebaseError
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.ncorti.slidetoact.SlideToActView
import com.pratik.happyscore.RemoveCountryCode
import com.pratik.happyscore.appointment.AppointmentBooking
import com.pratik.happyscore.databinding.FragmentHomeBinding
import java.text.DecimalFormat
import java.util.*


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
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

    //Searched doctor's data
    private lateinit var searchedName : String
    private lateinit var searchedEmail : String
    private lateinit var searchedPhone : String
    private lateinit var searchedData : String
    private lateinit var searchedUID : String
    private lateinit var searchedType: String

    private lateinit var sharedPreference : SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        TSS()

        firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth.currentUser

        db = FirebaseDatabase.getInstance().reference
        sharedPreference = requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE)

        getDataFromSharedPreference()

        binding.doctorData.setOnEditorActionListener { _, i, _ ->
            if (i == EditorInfo.IME_ACTION_DONE) {
                // Call your code here
                searchedData = binding.doctorData.text.toString().trim()
                if (searchedData.isNotEmpty()) {
                    if (RemoveCountryCode.remove(searchedData) == userPhone || searchedData == userPhone || searchedData == userEmail || isSameName(searchedData, userName)) {
                        Toast.makeText(requireActivity(), "Stop searching yourself", Toast.LENGTH_SHORT).show()
                        binding.cardView.isVisible = false
                        binding.slider.isVisible = false
                    }else {
                        doctorIsPresent()
                    }
                } else {
                    Toast.makeText(requireActivity(), "Enter doctor's email / phone", Toast.LENGTH_SHORT).show()
                }
                true
            }
            false
        }


        binding.slider.animDuration = 150
        binding.slider.onSlideCompleteListener = object : SlideToActView.OnSlideCompleteListener {
            override fun onSlideComplete(view: SlideToActView) {
                if (userPrescription != "false")
                {
                    val intent =  Intent(requireActivity(), AppointmentBooking::class.java)
                    intent.putExtra("Duid", searchedUID)
                    intent.putExtra("Dname", searchedName)
                    intent.putExtra("Demail", searchedEmail)
                    intent.putExtra("Dphone", searchedPhone)
                    intent.putExtra("Dtype", searchedType)
                    startActivity(intent)
                    binding.slider.resetSlider()
                } else {
                    Toast.makeText(requireActivity(), "Please upload your prescription in settings tab", Toast.LENGTH_SHORT).show()
                    binding.slider.resetSlider()
                }

            }
        }


        return binding.root
    }

    private fun TSS() {
        val pg= 6.9
        binding.circularProgressBar.apply {
            setProgressWithAnimation(pg.toFloat(), 1000)
            progressMax = 10f

            roundBorder = true
        }
        binding.circularProgressBar.onProgressChangeListener = {
            val df = DecimalFormat()
            df.maximumFractionDigits = 1
            binding.textprogress.text = "Total Social\nScore\n(${df.format(it)}/10)"
            // Do something
        }
    }

    private fun doctorIsPresent() {

        db.child("Doctor").addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (child in dataSnapshot.children) {
                    val map = child.value as HashMap<*, *>
                    val sName = map["name"].toString().trim()
                    val sType = map["specialist"].toString().trim()
                    val sEmail = map["email"].toString().trim()
                    val sPhone = map["phone"].toString().trim()
                    val sUid = map["uid"].toString().trim()
                    if (searchedData == sEmail || searchedData == sPhone || searchedData.trim() == sName || isSameName(searchedData,sName) || RemoveCountryCode.remove(searchedData) == sPhone) {
                        searchedName = sName
                        searchedEmail = sEmail
                        searchedPhone = sPhone
                        searchedUID = sUid
                        searchedType = sType
                        binding.textView3.isVisible = false
                        binding.cardView.isVisible = true
                        binding.slider.isVisible = true
                        binding.doctorName.text = "Name: Dr. $sName"
                        binding.doctortype.text = "Specialization: $sType"
                        binding.doctorEmail.text = "Email: $sEmail"
                        binding.doctorPhone.text = "Phone: $sPhone"
                        return
                    } else
                        binding.textView3.isVisible = true
                }
            }

            override fun onCancelled(error: DatabaseError) {}
            fun onCancelled(firebaseError: FirebaseError?) {}
        })
    }
    private fun isSameName(searchedName: String, dbNAME: String): Boolean {
        val modSearched: String = searchedName.replace(" ", "").toString().trim()
        val modDB: String = dbNAME.replace(" ", "").toString().trim()
        return modSearched == modDB;
    }

    override fun onStart() {
        super.onStart()
        Handler().postDelayed({
            getDataFromSharedPreference()
        }, 1000)
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
            val intent:Intent = Intent(requireActivity(),UserCard::class.java)
            intent.putExtra("uid",firebaseAuth.uid.toString())
            intent.putExtra("name",userName)
            startActivity(intent)
        }

    }
    @Throws(WriterException::class)
    fun generateQrCode(value: String): Bitmap {
        val hintMap = Hashtable<EncodeHintType, ErrorCorrectionLevel>()
        hintMap[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H

        val qrCodeWriter = QRCodeWriter()
        val size = 512
        val bitMatrix = qrCodeWriter.encode(value, BarcodeFormat.QR_CODE, size, size)
        val width = bitMatrix.width
        val bmp = Bitmap.createBitmap(width, width, Bitmap.Config.RGB_565)
        for (x in 0 until width)
            for (y in 0 until width)
                bmp.setPixel(y, x, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)

        return bmp
    }

}