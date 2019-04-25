package prabhalab.client.location.job;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;

import prabhalab.client.location.LocationService;
import prabhalab.client.location.Utility;

public class RestartServiceBroadCastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (!Utility.isMyServiceRunning(context, LocationService.class)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context, LocationService.class));
            } else {
                context.startService(new Intent(context, LocationService.class));
            }
        }
        setAlaram(context);
    }

    private void setAlaram(Context context){

        AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, RestartServiceBroadCastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        alarms.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() +30000, pendingIntent);

    }

}
