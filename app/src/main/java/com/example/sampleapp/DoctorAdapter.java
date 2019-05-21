package com.example.sampleapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.DocViewHolder> {

    private ArrayList<QueryDocumentSnapshot> doctorsList;

    /** Initialise an adapter with a dataset. */
    public DoctorAdapter(ArrayList<QueryDocumentSnapshot> list) {
        doctorsList = list;
    }

    public static class DocViewHolder extends RecyclerView.ViewHolder {

        public TextView tv;
        public DocViewHolder(TextView text) {
            super(text);
            tv = text;
        }

    }

    /** Sets the view to display contents */
    public DoctorAdapter.DocViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.doctor_row, parent, false);
        TextView text = view.findViewById(R.id.doctorName);
        return new DocViewHolder(text);

    }

    public void onBindViewHolder(DocViewHolder holder, int position) {

        QueryDocumentSnapshot doc = doctorsList.get(position);
        Doctor doctor = doc.toObject(Doctor.class);
        holder.tv.setText(doctor.name);

    }

    public int getItemCount() {
        return doctorsList.size();
    }

}
