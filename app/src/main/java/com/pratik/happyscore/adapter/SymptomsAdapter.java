package com.pratik.happyscore.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.pratik.happyscore.R;
import com.pratik.happyscore.models.SymptomModel;

import java.util.ArrayList;

public class SymptomsAdapter extends RecyclerView.Adapter<SymptomsAdapter.ViewHolder> {

    Context activity;
    ArrayList<SymptomModel> symptomModelArrayList;

    public SymptomsAdapter(Context activity, ArrayList<SymptomModel> symptomModelArrayList) {
        this.activity = activity;
        this.symptomModelArrayList = symptomModelArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(activity).inflate(R.layout.symptom_item,parent,false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SymptomModel symptomModel = symptomModelArrayList.get(position);

        holder.title.setText(symptomModel.getName());
        holder.detail.setText(symptomModel.getDetail());
        holder.imageView.setImageDrawable(ContextCompat.getDrawable(activity,symptomModel.getImage()));
        holder.checkBox.setChecked(symptomModel.getSelected());
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //set your object's last status
                symptomModel.setSelected(isChecked);
            }
        });
    }


    @Override
    public int getItemCount() {
        return symptomModelArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        CheckBox checkBox ;
        TextView title , detail;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.symptom_image);
            title = itemView.findViewById(R.id.symptom_name);
            checkBox = itemView.findViewById(R.id.select);
            detail = itemView.findViewById(R.id.symptom_status);
        }
    }
}
