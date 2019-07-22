package com.example.hk_pc.gmf;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.hk_pc.gmf.Reminder.AlertAlarm;
import com.example.hk_pc.gmf.Reminder.History;
import com.example.hk_pc.gmf.Reminder.Pill;
import com.example.hk_pc.gmf.Reminder.PillBox;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class activity_alert extends FragmentActivity {

    private AlarmManager alarmManager;
    private PendingIntent operation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /** Creating an Alert Dialog Window */
        AlertAlarm alert = new AlertAlarm();

        /** Opening the Alert Dialog Window. This will be opened when the alarm goes off */
        alert.show(getSupportFragmentManager(), "AlertAlarm");
    }

    // Snooze
   /* public void doNeutralClick(String pillName){
        final int _id = (int) System.currentTimeMillis();
        final long minute = 60000;
        long snoozeLength = 10;
        long currTime = System.currentTimeMillis();
        long min = currTime + minute * snoozeLength;

        Intent intent = new Intent(getBaseContext(), activity_alert.class);
        intent.putExtra("pill_name", pillName);

        operation = PendingIntent.getActivity(getBaseContext(), _id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        /** Getting a reference to the System Service ALARM_SERVICE
        alarmManager = (AlarmManager) getBaseContext().getSystemService(ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, min, operation);
        Toast.makeText(getBaseContext(), "Alarm for " + pillName + " was snoozed for 10 minutes", Toast.LENGTH_SHORT).show();

        finish();

    }*/

    // I took it
    public void doPositiveClick(String pillName){
        PillBox pillBox = new PillBox();
        Pill pill = pillBox.getPillByName(this, pillName);
        History history = new History();

        Calendar takeTime = Calendar.getInstance();
        Date date = takeTime.getTime();
        String dateString = new SimpleDateFormat("MMM d, yyyy").format(date);

        int hour = takeTime.get(Calendar.HOUR_OF_DAY);
        int minute = takeTime.get(Calendar.MINUTE);
        String am_pm = (hour < 12) ? "am" : "pm";

        history.setHourTaken(hour);
        history.setMinuteTaken(minute);
        history.setDateString(dateString);
        history.setPillName(pillName);

        pillBox.addToHistory(this, history);

        String stringMinute;
        if (minute < 10)
            stringMinute = "0" + minute;
        else
            stringMinute = "" + minute;

        int nonMilitaryHour = hour % 12;
        if (nonMilitaryHour == 0)
            nonMilitaryHour = 12;

        Toast.makeText(getBaseContext(),  pillName + " was taken at "+ nonMilitaryHour + ":" + stringMinute + " " + am_pm + ".", Toast.LENGTH_SHORT).show();

        /*Intent returnHistory = new Intent(getBaseContext(), activity_pillReminder.class);
        startActivity(returnHistory);*/
        Intent intent = new Intent(this, activity_pillReminder.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        startActivity(intent);
        finish();
    }

    // I won't take it
    public void doNegativeClick(){
        Intent intent = new Intent(this, activity_pillReminder.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        finish();
    }
}
