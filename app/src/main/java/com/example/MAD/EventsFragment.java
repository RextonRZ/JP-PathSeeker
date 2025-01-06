package com.example.MAD;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EventsFragment extends Fragment implements OnMapReadyCallback {
    private static final String TAG = "EventsFragment";
    private MapView mapView;
    private GoogleMap googleMap;
    private RecyclerView eventsRecyclerView;
    private EventsAdapter eventsAdapter;
    private final List<Event> eventsList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView called");
        View view = inflater.inflate(R.layout.fragment_events, container, false);

        // Initialize MapView
        mapView = view.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // Initialize RecyclerView
        eventsRecyclerView = view.findViewById(R.id.events_recycler_view);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        eventsRecyclerView.setHasFixedSize(true);

        // Initialize EventsAdapter and set event listeners
        initializeLocalEvents();
        eventsAdapter = new EventsAdapter(eventsList, new EventsAdapter.EventActionListener() {
            @Override
            public void onAddToCalendar(Event event) {
                addToGoogleCalendar(event);
            }

            @Override
            public void onNavigate(Event event) {
                navigateToLocation(event);
            }
        });
        eventsRecyclerView.setAdapter(eventsAdapter);

        return view;
    }

    private void initializeLocalEvents() {
        Log.e(TAG, "Initializing local events...");
        eventsList.add(new Event("1", "AI Open Day", "17/01/2025", "08:00 AM", "05:00 PM", "FCSIT, UM", 3.1209, 101.6538));
        eventsList.add(new Event("2", "AWS Community Day", "01/02/2025", "09:00 AM", "04:00 PM", "KLCC", 3.1579, 101.7114));
        eventsList.add(new Event("3", "DevFest 2025", "22/02/2025", "09:00 AM", "06:00 PM", "KLCC", 3.1579, 101.7114));
        Log.e(TAG, "Local events initialized: " + eventsList.size());
    }

    private void addToGoogleCalendar(Event event) {
        Log.e(TAG, "Adding event to Google Calendar: " + event.getTitle());
        Calendar beginTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();

        try {
            // Parse date (format: DD/MM/YYYY)
            String[] dateParts = event.getDate().split("/");
            int year = Integer.parseInt(dateParts[2]);
            int month = Integer.parseInt(dateParts[1]) - 1; // Months are zero-based in Calendar
            int day = Integer.parseInt(dateParts[0]);

            // Parse start time (format: HH:MM AM/PM)
            String[] startTimeParts = event.getStartTime().split(" ");
            String[] startHourMin = startTimeParts[0].split(":");
            int startHour = Integer.parseInt(startHourMin[0]) % 12; // Ensure 12-hour format
            if (startTimeParts[1].equalsIgnoreCase("PM")) startHour += 12;
            int startMinute = Integer.parseInt(startHourMin[1]);

            // Parse end time (format: HH:MM AM/PM)
            String[] endTimeParts = event.getEndTime().split(" ");
            String[] endHourMin = endTimeParts[0].split(":");
            int endHour = Integer.parseInt(endHourMin[0]) % 12;
            if (endTimeParts[1].equalsIgnoreCase("PM")) endHour += 12;
            int endMinute = Integer.parseInt(endHourMin[1]);

            // Set the event start and end times
            beginTime.set(year, month, day, startHour, startMinute);
            endTime.set(year, month, day, endHour, endMinute);
        } catch (Exception e) {
            Log.e(TAG, "Error parsing event date/time: " + e.getMessage());
            return;
        }

        // Create an intent to add the event to the calendar
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setData(CalendarContract.Events.CONTENT_URI);
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis());
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis());
        intent.putExtra(CalendarContract.Events.TITLE, event.getTitle());
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, event.getVenue());
        intent.putExtra(CalendarContract.Events.DESCRIPTION, "Event at " + event.getVenue());

        // Check if the calendar app can handle the intent
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.e(TAG, "No app available to add events to Google Calendar.");
        }
    }


    private void navigateToLocation(Event event) {
        Log.e(TAG, "Navigating to location: " + event.getVenue());
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + event.getLatitude() + "," + event.getLongitude());
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Log.e(TAG, "No app available to navigate to location.");
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        Log.e(TAG, "Map is ready");
        for (Event event : eventsList) {
            LatLng location = new LatLng(event.getLatitude(), event.getLongitude());
            googleMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title(event.getTitle())
                    .snippet(event.getVenue())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }
        LatLng defaultLocation = new LatLng(3.1209, 101.6538);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f));
    }

    // MapView lifecycle methods
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
