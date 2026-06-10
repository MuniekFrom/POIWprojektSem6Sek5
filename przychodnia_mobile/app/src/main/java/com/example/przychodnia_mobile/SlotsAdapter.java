package com.example.przychodnia_mobile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SlotsAdapter extends RecyclerView.Adapter<SlotsAdapter.SlotViewHolder> {

    private List<AppointmentSlotResponse> slots;
    private final OnSlotClickListener listener;

    public interface OnSlotClickListener {
        void onBookClick(AppointmentSlotResponse slot, String reason);
    }

    public SlotsAdapter(List<AppointmentSlotResponse> slots, OnSlotClickListener listener) {
        this.slots = slots;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slot, parent, false);
        return new SlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlotViewHolder holder, int position) {
        AppointmentSlotResponse slot = slots.get(position);
        Context context = holder.itemView.getContext();

        holder.tvSlotId.setText(context.getString(R.string.slot_id_label, slot.getId()));
        holder.tvSlotDoctor.setText(context.getString(R.string.slot_doctor_label, slot.getDoctorName() != null ? slot.getDoctorName() : "-"));
        holder.tvSlotSpecialization.setText(context.getString(R.string.slot_specialization_label, slot.getDoctorSpecialization() != null ? slot.getDoctorSpecialization() : "-"));
        holder.tvSlotStart.setText(context.getString(R.string.slot_start_label, slot.getStartTime()));
        holder.tvSlotEnd.setText(context.getString(R.string.slot_end_label, slot.getEndTime()));

        if (!slot.isAvailable()) {
            holder.tvSlotStatus.setText(R.string.slot_available);
            holder.tvSlotStatus.setBackgroundResource(R.drawable.bg_slots_badge);
            holder.tvSlotStatus.setTextColor(ContextCompat.getColor(context, R.color.green_text_slots));
            holder.btnBookSlot.setEnabled(true);
            holder.btnBookSlot.setAlpha(1.0f);
            holder.etVisitReason.setEnabled(true);
            holder.etVisitReason.setVisibility(View.VISIBLE);
        } else {
            holder.tvSlotStatus.setText(R.string.slot_taken);
            holder.tvSlotStatus.setBackgroundResource(R.drawable.bg_status_reserved);
            holder.tvSlotStatus.setTextColor(ContextCompat.getColor(context, R.color.status_blue_text));
            holder.btnBookSlot.setEnabled(false);
            holder.btnBookSlot.setAlpha(0.5f);
            holder.etVisitReason.setEnabled(false);
            holder.etVisitReason.setVisibility(View.GONE);
        }

        holder.btnBookSlot.setOnClickListener(v -> {
            String reason = holder.etVisitReason.getText().toString().trim();
            if (reason.isEmpty()) {
                Toast.makeText(context, context.getString(R.string.visit_reason_hint), Toast.LENGTH_SHORT).show();
            } else {
                listener.onBookClick(slot, reason);
            }
        });
    }

    @Override
    public int getItemCount() {
        return slots != null ? slots.size() : 0;
    }

    public void updateSlots(List<AppointmentSlotResponse> newSlots) {
        this.slots = newSlots;
        notifyDataSetChanged();
    }

    static class SlotViewHolder extends RecyclerView.ViewHolder {
        TextView tvSlotId, tvSlotDoctor, tvSlotSpecialization, tvSlotStart, tvSlotEnd, tvSlotStatus;
        EditText etVisitReason;
        Button btnBookSlot;

        SlotViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSlotId = itemView.findViewById(R.id.tvSlotId);
            tvSlotDoctor = itemView.findViewById(R.id.tvSlotDoctor);
            tvSlotSpecialization = itemView.findViewById(R.id.tvSlotSpecialization);
            tvSlotStart = itemView.findViewById(R.id.tvSlotStart);
            tvSlotEnd = itemView.findViewById(R.id.tvSlotEnd);
            tvSlotStatus = itemView.findViewById(R.id.tvSlotStatus);
            etVisitReason = itemView.findViewById(R.id.etVisitReason);
            btnBookSlot = itemView.findViewById(R.id.btnBookSlot);
        }
    }
}
