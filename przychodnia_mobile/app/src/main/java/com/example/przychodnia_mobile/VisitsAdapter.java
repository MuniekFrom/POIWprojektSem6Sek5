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

public class VisitsAdapter extends RecyclerView.Adapter<VisitsAdapter.VisitViewHolder> {

    private List<AppointmentResponse> appointments;
    private final OnAppointmentCancelListener cancelListener;

    public interface OnAppointmentCancelListener {
        void onCancel(Long appointmentId);
    }

    public VisitsAdapter(List<AppointmentResponse> appointments, OnAppointmentCancelListener cancelListener) {
        this.appointments = appointments;
        this.cancelListener = cancelListener;
    }

    @NonNull
    @Override
    public VisitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_visit, parent, false);
        return new VisitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VisitViewHolder holder, int position) {
        AppointmentResponse appointment = appointments.get(position);
        Context context = holder.itemView.getContext();
        
        holder.tvVisitId.setText(context.getString(R.string.visit_id, appointment.getId()));
        holder.tvDoctor.setText(context.getString(R.string.visit_doctor, appointment.getDoctorName()));
        holder.tvSpecialization.setText(context.getString(R.string.visit_specialization, appointment.getDoctorSpecialization()));
        holder.tvStart.setText(context.getString(R.string.visit_start, appointment.getStartTime()));
        holder.tvEnd.setText(context.getString(R.string.visit_end, appointment.getEndTime()));
        holder.tvReason.setText(context.getString(R.string.visit_reason, appointment.getReason()));
        holder.tvStatus.setText(appointment.getStatus());

        holder.btnCancelVisit.setOnClickListener(v -> {
            if (cancelListener != null) {
                cancelListener.onCancel(appointment.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return appointments != null ? appointments.size() : 0;
    }

    public void updateAppointments(List<AppointmentResponse> newAppointments) {
        this.appointments = newAppointments;
        notifyDataSetChanged();
    }

    static class VisitViewHolder extends RecyclerView.ViewHolder {
        TextView tvVisitId, tvDoctor, tvSpecialization, tvStart, tvEnd, tvReason, tvStatus;
        Button btnCancelVisit;

        VisitViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVisitId = itemView.findViewById(R.id.tvVisitId);
            tvDoctor = itemView.findViewById(R.id.tvDoctor);
            tvSpecialization = itemView.findViewById(R.id.tvSpecialization);
            tvStart = itemView.findViewById(R.id.tvStart);
            tvEnd = itemView.findViewById(R.id.tvEnd);
            tvReason = itemView.findViewById(R.id.tvReason);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnCancelVisit = itemView.findViewById(R.id.btnCancelVisit);
        }
    }
}
