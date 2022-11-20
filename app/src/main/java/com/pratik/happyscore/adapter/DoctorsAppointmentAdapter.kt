package com.pratik.happyscore.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.pratik.happyscore.appointment.DoctorAppointment
import com.pratik.happyscore.R
import com.pratik.happyscore.mainFragments.MriReports


class DoctorsAppointmentAdapter(var appointmentList: ArrayList<DoctorAppointment>) : RecyclerView.Adapter<DoctorsAppointmentAdapter.DoctorAppointmentViewHolder>() {

    lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorAppointmentViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.appointment_list,parent,false)
        context = parent.context
        return DoctorAppointmentViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DoctorAppointmentViewHolder, position: Int) {

        val currentItem = appointmentList[position]
        val DoctorUid = currentItem.DoctorUID

        if (currentItem.PatientPhone == "" || currentItem.PatientPhone!!.isEmpty()) {
            holder.name.text = currentItem.PatientName
        } else {
            holder.name.text = currentItem.PatientName + " (" + currentItem.PatientPhone + ")"
        }
        holder.disease.text = currentItem.Disease + " - " + currentItem.PatientCondition
        holder.button.setOnClickListener {
            val intent = Intent(holder.button.context, MriReports::class.java)
            intent.putExtra("DoctorUID",currentItem.DoctorUID.toString())
            intent.putExtra("PatientUID",currentItem.PatientUID.toString())

            intent.putExtra("Date",currentItem.Date.toString())
            holder.button.context.startActivity(intent)

        }
    }

    override fun getItemCount(): Int {
        return appointmentList.size
    }
    class DoctorAppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val name: TextView = itemView.findViewById(R.id.nameDisplay)
        val disease: TextView = itemView.findViewById(R.id.diseaseDisplay)
        val button: ImageView = itemView.findViewById(R.id.downloadPrescription)
    }
}