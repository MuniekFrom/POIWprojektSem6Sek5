package com.example.przychodnia_mobile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DoctorsAdapter extends RecyclerView.Adapter<DoctorsAdapter.DoctorViewHolder> {

    private final List<DoctorResponse> doctors;
    private final OnDoctorClickListener listener;

    public interface OnDoctorClickListener {
        void onShowSlots(DoctorResponse doctor);
    }

    public DoctorsAdapter(List<DoctorResponse> doctors, OnDoctorClickListener listener) {
        this.doctors = doctors;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_doctor, parent, false);
        return new DoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        DoctorResponse doctor = doctors.get(position);
        Context context = holder.itemView.getContext();
        
        holder.tvDoctorName.setText(context.getString(R.string.doctor_name_format, doctor.getFirstName(), doctor.getLastName()));
        holder.tvDoctorSpecialization.setText(doctor.getSpecialization());
        holder.btnShowSlots.setOnClickListener(v -> listener.onShowSlots(doctor));
    }

    @Override
    public int getItemCount() {
        return doctors != null ? doctors.size() : 0;
    }

    static class DoctorViewHolder extends RecyclerView.ViewHolder {
        TextView tvDoctorName, tvDoctorSpecialization;
        Button btnShowSlots;

        DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDoctorName = itemView.findViewById(R.id.tvDoctorName);
            tvDoctorSpecialization = itemView.findViewById(R.id.tvDoctorSpecialization);
            btnShowSlots = itemView.findViewById(R.id.btnShowSlots);
        }
    }
}
