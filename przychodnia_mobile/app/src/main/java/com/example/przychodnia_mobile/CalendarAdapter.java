package com.example.przychodnia_mobile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {

    private final List<CalendarDayResponse> days;
    private final OnDayClickListener listener;

    public interface OnDayClickListener {
        void onDayClick(CalendarDayResponse day);
    }

    public CalendarAdapter(List<CalendarDayResponse> days, OnDayClickListener listener) {
        this.days = days;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar_day, parent, false);
        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        CalendarDayResponse day = days.get(position);
        Context context = holder.itemView.getContext();
        
        holder.tvDate.setText(String.format("%s, %s", day.getDayName(), day.getDate()));
        
        int count = day.getSlotCount();
        String countText;
        if (count == 1) {
            countText = context.getString(R.string.slot_count, count);
        } else if (count >= 2 && count <= 4) {
            countText = context.getString(R.string.slot_count_plural, count);
        } else {
            countText = context.getString(R.string.slot_count_many, count);
        }
        holder.tvSlotCount.setText(countText);
        
        holder.itemView.setOnClickListener(v -> listener.onDayClick(day));
    }

    @Override
    public int getItemCount() {
        return days != null ? days.size() : 0;
    }

    static class CalendarViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvSlotCount;

        CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvSlotCount = itemView.findViewById(R.id.tvSlotCount);
        }
    }
}
