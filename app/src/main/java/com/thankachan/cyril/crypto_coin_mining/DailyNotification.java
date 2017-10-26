package milanroxe.inc.snocoins;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by Codewix Dev on 7/21/2017.
 */

public class DailyNotification extends BroadcastReceiver {



    public static void registerAlarm(Context paramContext, int mHour, int mMin) {
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.HOUR_OF_DAY) >= mHour) {
            calendar.add(Calendar.DATE, 1);
        }
        calendar.set(Calendar.HOUR_OF_DAY, mHour);
        calendar.set(Calendar.MINUTE, mMin);
        calendar.set(Calendar.SECOND, 00);


        PendingIntent localPendingIntent = PendingIntent.getBroadcast(
                paramContext, 22341, new Intent(
                        "com.example.easynotification.DAILY_NOTIFICATION"),
                PendingIntent.FLAG_UPDATE_CURRENT);

        ((AlarmManager) paramContext.getSystemService(ALARM_SERVICE)).setRepeating(1,
                calendar.getTimeInMillis(), 24 * 60 * 60 * 1000,
                localPendingIntent);
        System.out.println("Alarm Registered on Hour :- "+mHour+" : Min "+mMin);
    }

      @Override
    public void onReceive(Context paramContext, Intent paramIntent) {
        if (paramIntent.getAction().equals("com.example.easynotification.DAILY_NOTIFICATION")){
             SharedPreferences sharedPreferences;
             SharedPreferences.Editor editor;
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(paramContext);
            editor = sharedPreferences.edit();
            editor.putString("alarm","True");
            editor.commit();
        }
    }
}