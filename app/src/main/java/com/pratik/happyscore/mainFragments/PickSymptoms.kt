package com.pratik.happyscore.mainFragments

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.pratik.happyscore.R
import com.pratik.happyscore.adapter.SymptomsAdapter
import com.pratik.happyscore.models.SymptomModel

class PickSymptoms : AppCompatActivity() {


    lateinit var mainSymptomRecyclerView: RecyclerView
    lateinit var adapter:SymptomsAdapter
    lateinit var symptomArrayList: ArrayList<SymptomModel>
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: DatabaseReference
    var totalcontri = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pick_symptoms)
        mainSymptomRecyclerView = findViewById(R.id.symptomrecycler)
        symptomArrayList = ArrayList()
        symptomArrayList.clear()

        getsymptoms()




        firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth.currentUser

        db = FirebaseDatabase.getInstance().reference

        mainSymptomRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = SymptomsAdapter(this@PickSymptoms,symptomArrayList)
        mainSymptomRecyclerView.adapter = adapter
    }

    private fun getsymptoms() {
        symptomArrayList.clear()
        symptomArrayList.add(SymptomModel("Headaches","A brain tumor increases the pressure inside the skull, which can lead to inflammation and tissue damage. Severe, persistent headaches can be symptom of brain tumors.",R.drawable.headache,false,25f))
        symptomArrayList.add(SymptomModel("Weight Loss/Gain"," It may be the first visible sign of the disease.",R.drawable.weightloss,false,2f))
        symptomArrayList.add(SymptomModel("Weakness","It is commonly caused by brain tumors located in the frontal lobes or the brainstem. Weakness can also be caused by treatment-associated swelling or an injury to the brain.",R.drawable.weakness,false,2f))
        symptomArrayList.add(SymptomModel("Nausea & Vomiting","These symptoms can be due to a brain tumor causing increased pressure inside the brain.",R.drawable.vomit,false,12f))
        symptomArrayList.add(SymptomModel("Dizziness","Some tumors can trigger headaches and bouts of nausea and vomiting that may be associated with a dizzy feeling.",R.drawable.dizzy,false,12f))
        symptomArrayList.add(SymptomModel("Seizures","They often represent the first clinical sign of a brain tumor and count as a favorable prognostic factor, although reappearance or worsening of seizures may indicate tumor recurrence.",R.drawable.seizure,false,11f))
        symptomArrayList.add(SymptomModel("Frequent change in mood","This happens most often when the tumour is in the frontal lobe of their brain.",R.drawable.mood,false,6f))
        symptomArrayList.add(SymptomModel("Change in speech","Aphasia (sometimes called dysphasia) is the most common communication difficulty experienced by people with brain tumours.",R.drawable.changeinspeech,false,6f))
        symptomArrayList.add(SymptomModel("Sensory Changes","Multimodal sensory changes occurred in patients with brain tumors, manifesting as hyper- or hyposensitivity.",R.drawable.sensorychanges,false,10f))
        symptomArrayList.add(SymptomModel("Loss in Balance","As the tumor grows, it creates pressure on and changes the function of surrounding brain tissue, which causes signs and symptoms such as headaches, nausea and balance problems.",R.drawable.balance,false,5f))
        symptomArrayList.add(SymptomModel("Change in Pulse/Breathing","as brain tumors are usually asymptomatic or oligosymptomatic in the early stages, it makes diagnosis difficult.",R.drawable.heartbeat,false,2f))
        symptomArrayList.add(SymptomModel("Vision Loss","Vision changes, including loss of part of the vision or double vision can be from a tumor in the temporal lobe, occipital lobe, or brain stem.",R.drawable.visiobloss,false,7f))


    }

    fun closepage(view: View) {
        finish()
    }
    fun Start(view: View) {
        for (i in 0..11){
            val x = symptomArrayList[i]
            if (x.selected){
                Log.d("contri",x.name+" "+x.contri.toString())
                totalcontri+=x.contri
            }
        }
        //Toast.makeText(this@PickSymptoms,totalcontri.toString(),Toast.LENGTH_SHORT).show()
        var previouscontri = 0f
        db.child("Users").child(firebaseAuth.uid.toString()).child("contributions").get().addOnSuccessListener {
            previouscontri = it.child("symptomcontri").value.toString().toFloat()
            totalcontri *= 0.3f
            db.child("Users").child(firebaseAuth.uid.toString()).child("contributions").child("symptomcontri").setValue(totalcontri)
            if (totalcontri>previouscontri)Toast.makeText(this@PickSymptoms,"Your chances of tumor has increased by ${totalcontri-previouscontri}%",Toast.LENGTH_SHORT).show()
            else if(totalcontri<previouscontri)Toast.makeText(this@PickSymptoms,"Your chances of tumor has decreased by ${-totalcontri+previouscontri}%",Toast.LENGTH_SHORT).show()
            else Toast.makeText(this@PickSymptoms,"Your chances of tumor has remains same",Toast.LENGTH_SHORT).show()
            totalcontri = 0f
            finish()
        }
    }
}