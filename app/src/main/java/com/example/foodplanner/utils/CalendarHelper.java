package com.example.foodplanner.utils;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.CalendarContract;

import androidx.core.content.ContextCompat;

import com.example.foodplanner.data.model.Meal;

import java.util.Calendar;
import java.util.TimeZone;


public class CalendarHelper {
    
    private static final String TAG = "CalendarHelper";
    

    public static boolean hasCalendarPermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) 
                == PackageManager.PERMISSION_GRANTED &&
               ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) 
                == PackageManager.PERMISSION_GRANTED;
    }
    
   
    public static boolean addMealToCalendar(Context context, Meal meal, int year, int month, int day) {
        if (!hasCalendarPermission(context)) {
            return false;
        }
        
        try {
            ContentResolver cr = context.getContentResolver();
            
           
            long calendarId = getPrimaryCalendarId(context);
            if (calendarId == -1) {
                // No calendar found, try to use calendar ID 1 as fallback
                calendarId = 1;
            }
            
            Calendar startTime = Calendar.getInstance();
            startTime.set(year, month, day, 12, 0, 0);
            startTime.set(Calendar.MILLISECOND, 0);
            
            Calendar endTime = Calendar.getInstance();
            endTime.set(year, month, day, 13, 0, 0);
            endTime.set(Calendar.MILLISECOND, 0);
            
            // Build event description
            StringBuilder description = new StringBuilder();
            description.append("Category: ").append(meal.getCategory()).append("\n");
            description.append("Origin: ").append(meal.getArea()).append("\n\n");
            
            // Add ingredients
            description.append("Ingredients:\n");
            for (int i = 0; i < meal.getIngredientsList().size(); i++) {
                String ingredient = meal.getIngredientsList().get(i);
                String measure = i < meal.getMeasuresList().size() ? meal.getMeasuresList().get(i) : "";
                description.append("• ").append(measure).append(" ").append(ingredient).append("\n");
            }
            
            description.append("\nInstructions:\n");
            description.append(meal.getInstructions());
            
            // Create the event
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Events.DTSTART, startTime.getTimeInMillis());
            values.put(CalendarContract.Events.DTEND, endTime.getTimeInMillis());
            values.put(CalendarContract.Events.TITLE, "🍽️ " + meal.getName());
            values.put(CalendarContract.Events.DESCRIPTION, description.toString());
            values.put(CalendarContract.Events.CALENDAR_ID, calendarId);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
            values.put(CalendarContract.Events.HAS_ALARM, 1);
            
            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
            
            if (uri != null) {
                // Add a reminder 1 hour before
                long eventId = Long.parseLong(uri.getLastPathSegment());
                addReminder(cr, eventId, 60); // 60 minutes before
                return true;
            }
            
            return false;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    

    private static long getPrimaryCalendarId(Context context) {
        String[] projection = {CalendarContract.Calendars._ID, CalendarContract.Calendars.IS_PRIMARY};
        
        try (android.database.Cursor cursor = context.getContentResolver().query(
                CalendarContract.Calendars.CONTENT_URI,
                projection,
                null,
                null,
                null)) {
            
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int idIndex = cursor.getColumnIndex(CalendarContract.Calendars._ID);
                    int primaryIndex = cursor.getColumnIndex(CalendarContract.Calendars.IS_PRIMARY);
                    
                    if (idIndex >= 0) {
                        long id = cursor.getLong(idIndex);
                        boolean isPrimary = primaryIndex >= 0 && cursor.getInt(primaryIndex) == 1;
                        
                        if (isPrimary) {
                            return id;
                        }
                    }
                } while (cursor.moveToNext());
                
                // If no primary calendar, return the first one
                cursor.moveToFirst();
                int idIndex = cursor.getColumnIndex(CalendarContract.Calendars._ID);
                if (idIndex >= 0) {
                    return cursor.getLong(idIndex);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return -1;
    }
    

    private static void addReminder(ContentResolver cr, long eventId, int minutesBefore) {
        try {
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Reminders.EVENT_ID, eventId);
            values.put(CalendarContract.Reminders.MINUTES, minutesBefore);
            values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
            cr.insert(CalendarContract.Reminders.CONTENT_URI, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
