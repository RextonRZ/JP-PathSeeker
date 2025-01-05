package com.example.MAD;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder> {

    private List<String> timeSlots;
    private String bookedSlot;
    private FirebaseHelper firebaseHelper;
    private String mentorId;
    private String date;
    private String currentUser;
    private Context context;

    private static final String TAG = "TimeSlotAdapter";

    public TimeSlotAdapter(Context context, List<String> timeSlots, String bookedSlot, String mentorId, String date, String currentUser) {
        this.context = context;
        this.timeSlots = timeSlots;
        this.bookedSlot = bookedSlot;
        this.mentorId = mentorId;
        this.date = date;
        this.currentUser = currentUser;
        this.firebaseHelper = new FirebaseHelper();
    }

    @NonNull
    @Override
    public TimeSlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.timeslot_item, parent, false);
        return new TimeSlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeSlotViewHolder holder, int position) {
        String timeSlot = timeSlots.get(position);
        holder.timeSlotTextView.setText(timeSlot);

        boolean isPassedTime = isTimeSlotPassed(timeSlot);

        if (isPassedTime) {
            // Mark passed time slots
            holder.itemView.setBackgroundColor(Color.GRAY);
            holder.itemView.setEnabled(false);
            holder.timeSlotTextView.setTextColor(Color.WHITE);
            holder.itemView.setOnClickListener(v ->
                    Toast.makeText(context, "This time slot has passed", Toast.LENGTH_SHORT).show());
            return;
        }

        // Check if the time slot is already booked
        firebaseHelper.isSlotBooked(mentorId, date, timeSlot, new FirebaseHelper.DataCallback() {
            @Override
            public void onSuccess(List<Mentor> mentors) {
                boolean isBooked = mentors.size() > 0;
                if (isBooked) {
                    holder.itemView.setBackgroundColor(Color.GRAY);
                    holder.itemView.setEnabled(false);
                    holder.timeSlotTextView.setTextColor(Color.BLACK);
                    holder.itemView.setOnClickListener(v ->
                            Toast.makeText(context, "This slot is already booked", Toast.LENGTH_SHORT).show());
                } else {
                    holder.itemView.setBackgroundColor(Color.WHITE);
                    holder.itemView.setEnabled(true);
                    holder.timeSlotTextView.setTextColor(Color.BLACK);
                    setupBookingClickListener(holder, timeSlot);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(context, "Error checking booking status", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return timeSlots.size();
    }

    private boolean isTimeSlotPassed(String timeSlot) {
        try {
            // Get current date and time
            Calendar currentCalendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kuala_Lumpur"));

            // Parse the selected date
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
            Date selectedDate = date.isEmpty() ? new Date() : dateFormat.parse(date);

            // Parse the time slot
            SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.US);
            Date slotTime = timeFormat.parse(timeSlot);

            // Combine selected date with time slot
            Calendar slotCalendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kuala_Lumpur"));
            slotCalendar.setTime(selectedDate);

            Calendar timeCalendar = Calendar.getInstance();
            timeCalendar.setTime(slotTime);

            slotCalendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
            slotCalendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));

            // Check if the slot is in the past
            return slotCalendar.before(currentCalendar);

        } catch (ParseException e) {
            Log.e(TAG, "Error parsing date/time", e);
            return false;
        }
    }

    private void setupBookingClickListener(TimeSlotViewHolder holder, String timeSlot) {
        holder.itemView.setOnClickListener(v -> {
            if (date.isEmpty() || !date.matches("\\d{2}/\\d{2}/\\d{4}")) {
                Toast.makeText(context, "Please select a valid date first", Toast.LENGTH_SHORT).show();
                return;
            }

            firebaseHelper.bookSlot(mentorId, date, timeSlot, currentUser, new FirebaseHelper.DataCallback() {
                @Override
                public void onSuccess(List<Mentor> mentors) {
                    Toast.makeText(context, "Slot booked successfully!", Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(context, "Failed to book slot: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    public static class TimeSlotViewHolder extends RecyclerView.ViewHolder {
        TextView timeSlotTextView;

        public TimeSlotViewHolder(@NonNull View itemView) {
            super(itemView);
            timeSlotTextView = itemView.findViewById(R.id.timeSlotText);
        }
    }
}
