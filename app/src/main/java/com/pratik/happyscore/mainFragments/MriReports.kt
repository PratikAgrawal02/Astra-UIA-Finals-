package com.pratik.happyscore.mainFragments

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.ThumbnailUtils
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.faskn.lib.ClickablePieChart
import com.faskn.lib.Slice
import com.faskn.lib.buildChart
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pratik.happyscore.R
import com.pratik.happyscore.ml.Model
import com.squareup.picasso.Picasso
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*
import kotlin.collections.ArrayList


class MriReports : AppCompatActivity() {

    lateinit var chart:ClickablePieChart
    lateinit var legendlayout:FrameLayout
    var imageSize = 150
    lateinit var auth: FirebaseAuth
    lateinit var database: FirebaseDatabase
    lateinit var patientuid:String
    lateinit var doctoruid:String
    lateinit var date: String
    lateinit var totalarrow : ImageView
    lateinit var totalp : TextView
    lateinit var gilomap : TextView
    lateinit var meninginomap : TextView
    lateinit var pitutaryp : TextView
    lateinit var gilomaarrow : ImageView
    lateinit var meningnomarrow: ImageView
    var contri = 0f
    lateinit var ll1: LinearLayout
    lateinit var ll3 : LinearLayout
    lateinit var pitutaryarrow :ImageView
    lateinit var mriimage : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mri_reports)

        hook()


        patientuid = intent.getStringExtra("PatientUID").toString()
        doctoruid = intent.getStringExtra("DoctorUID").toString()
        date = intent.getStringExtra("Date").toString()

        if (patientuid == auth.uid.toString()){
         ll1.visibility = View.GONE
         ll3.visibility = View.GONE
         }



        val ref = database.getReference("Users").child(patientuid)
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
               // Toast.makeText(this@MriReports, snapshot.child("Prescription").value.toString(),Toast.LENGTH_SHORT).show()
                val url = snapshot.child("prescription").value.toString()
                if (url=="false"){
                    ll3.visibility = View.VISIBLE
                    ll1.visibility = View.INVISIBLE
                }
                else {
                    ll1.visibility = View.VISIBLE
                    ll3.visibility = View.INVISIBLE


                    Picasso.get().load(url).into(object : com.squareup.picasso.Target {
                        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                            // loaded bitmap is here (bitmap)
                            if (bitmap != null) {
                                var image:Bitmap = bitmap
                                val dimension = Math.min(image.width, image.height)
                                image = ThumbnailUtils.extractThumbnail(image, dimension, dimension)

                                //SET MRI IMAGE
                                mriimage.setImageBitmap(image)

                                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false)
                                classifyImage(image)
                            }
                            else{
                                Toast.makeText(this@MriReports,"Your MRI REPORTS ARE MISSING!",Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}

                        override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {}
                    })
                }
                var slicearray : ArrayList<Slice> = ArrayList<Slice>()

                if (snapshot.child("contributions").child("mri").exists())
                slicearray.add(Slice(
                    snapshot.child("contributions").child("mri").value.toString().toFloat(),
                    R.color.light_blue,
                    "MRI SCAN"
                ))
                if (snapshot.child("contributions").child("symptomcontri").exists())
                    slicearray.add(Slice(
                        snapshot.child("contributions").child("symptomcontri").value.toString().toFloat(),
                        R.color.dark_blue,
                        "Symptoms"
                    ))


                if (slicearray.size!=0) {
                    val pieChartDSL = buildChart {
                        slices { slicearray }
                        sliceWidth { 80f }
                        sliceStartPoint { 0f }
                        clickListener { angle, index ->

                        }
                    }

                    chart.setPieChart(pieChartDSL)
                    chart.showLegend(legendlayout)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }

    private fun hook() {
        chart = findViewById(R.id.chart2)
        legendlayout = findViewById(R.id.legendLayout)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        totalp = findViewById(R.id.totalpercent)
        gilomap = findViewById(R.id.gilomapercent)
        meninginomap = findViewById(R.id.meningomapercent)
        pitutaryp = findViewById(R.id.pituitarypercent)

        totalarrow = findViewById(R.id.Totalarrow)
        meningnomarrow = findViewById(R.id.meningomaarrow)
        pitutaryarrow  = findViewById(R.id.pituitaryarrow)
        gilomaarrow = findViewById(R.id.gilomaarrow)
        mriimage = findViewById(R.id.mriimage)

        ll1 = findViewById(R.id.ll1)
        ll3 = findViewById(R.id.ll3)

    }

    private fun provideSlices(): ArrayList<Slice> {
        val ref = database.getReference("Users").child(patientuid)
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
        return arrayListOf(
            Slice(
                10f,
                R.color.green,
                "Google"
            ),
            Slice(
                30f,
                R.color.black,
                "Facebook"
            ),
            Slice(
                50f,
                R.color.green,
                "Twitter"
            ),
            Slice(
                17f,
                R.color.light_blue,
                "Other"
            )
        )
    }

    fun SendForRetest(view: View) {
        val ref = database.getReference("Users").child(patientuid)
        ref.child("retest").setValue(true)
        Toast.makeText(this@MriReports,"User has been sent to diagnostic Centre for Retesting MRI Test",Toast.LENGTH_SHORT).show()
        finish()
    }
    fun Esign(view: View) {
        val esigncard = view as CardView
        esigncard.setCardBackgroundColor(ContextCompat.getColorStateList(this@MriReports, R.color.green))
        val ref = database.getReference("Users").child(patientuid)
        ref.child("esigned").setValue(true)
        ref.child("contributions").child("mri").setValue(contri)
        Toast.makeText(this@MriReports,"MRI Report has been E-signed",Toast.LENGTH_SHORT).show()
        finish()
    }

    @SuppressLint("SetTextI18n")
    fun classifyImage(image: Bitmap) {
        try {
            val model: Model = Model.newInstance(applicationContext)

            // Creates inputs for reference.
            val inputFeature0 = TensorBuffer.createFixedSize(
                intArrayOf(1, imageSize, imageSize, 3),
                DataType.FLOAT32
            )
            val byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3)
            byteBuffer.order(ByteOrder.nativeOrder())
            val intValues = IntArray(imageSize * imageSize)
            image.getPixels(intValues, 0, image.width, 0, 0, image.width, image.height)
            var pixel = 0
            //iterate over each pixel and extract R, G, and B values. Add those values individually to the byte buffer.
            for (i in 0 until imageSize) {
                for (j in 0 until imageSize) {
                    val `val` = intValues[pixel++] // RGB
                    byteBuffer.putFloat((`val` shr 16 and 0xFF) * (1f / 1))
                    byteBuffer.putFloat((`val` shr 8 and 0xFF) * (1f / 1))
                    byteBuffer.putFloat((`val` and 0xFF) * (1f / 1))
                }
            }
            inputFeature0.loadBuffer(byteBuffer)

            // Runs model inference and gets result.
            val outputs: Model.Outputs = model.process(inputFeature0)
            val outputFeature0: TensorBuffer = outputs.outputFeature0AsTensorBuffer
            val confidences = outputFeature0.floatArray
            // find the index of the class with the biggest confidence.
            gilomap.text = "${"%.2f".format(confidences[0]*100f)}%"
            if (confidences[0]>0.5f)gilomaarrow.setImageDrawable(ContextCompat.getDrawable(this@MriReports,R.drawable.up_arrow))
            else gilomaarrow.setImageDrawable(ContextCompat.getDrawable(this@MriReports,R.drawable.down_arrow))
            if (confidences[2]>0.5f)meningnomarrow.setImageDrawable(ContextCompat.getDrawable(this@MriReports,R.drawable.up_arrow))
            else meningnomarrow.setImageDrawable(ContextCompat.getDrawable(this@MriReports,R.drawable.down_arrow))
            if (confidences[3]>0.5f)pitutaryarrow.setImageDrawable(ContextCompat.getDrawable(this@MriReports,R.drawable.up_arrow))
            else pitutaryarrow.setImageDrawable(ContextCompat.getDrawable(this@MriReports,R.drawable.down_arrow))
            if (confidences[1]<0.5f)totalarrow.setImageDrawable(ContextCompat.getDrawable(this@MriReports,R.drawable.up_arrow))
            else totalarrow.setImageDrawable(ContextCompat.getDrawable(this@MriReports,R.drawable.down_arrow))


            totalp.text = "${"%.2f".format((1-confidences[1])*100f)}%"
            meninginomap.text = "${"%.2f".format(confidences[2]*100f)}%"
            pitutaryp.text = "${"%.2f".format(confidences[3]*100f)}%"

            contri = ((1-confidences[1])*100f)/2

//            var maxPos = 0
//            var maxConfidence = 0f
//            for (i in confidences.indices) {
//                if (confidences[i] > maxConfidence) {
//                    maxConfidence = confidences[i]
//                    maxPos = i
//                }
//
//            }
            val classes = arrayOf("glioma_tumor", "no_tumor", "meningioma_tumor", "pituitary_tumor")

            model.close()
        } catch (e: IOException) {
            // TODO Handle the exception
        }
    }

    fun takemri(view: View) {
        val ref = database.getReference("Users").child(patientuid)
        ref.child("takemri").setValue(true)
        ref.child("retest").setValue(false)
        Toast.makeText(this@MriReports,"User has been sent to diagnostic Centre for MRI Test",Toast.LENGTH_SHORT).show()
        finish()
    }


}