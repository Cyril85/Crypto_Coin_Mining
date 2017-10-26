package milanroxe.inc.snocoins.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import milanroxe.inc.snocoins.R;
import milanroxe.inc.snocoins.bitcoin.BitcoinActivity;

public class FillExpenseNotificationReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
    Intent notificationIntent = new Intent(context, BitcoinActivity.class);

    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
    stackBuilder.addParentStack(BitcoinActivity.class);
    stackBuilder.addNextIntent(notificationIntent);

    PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT);

    NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

    Notification notification = builder.setContentTitle("Buy Bitcoins Today")
      .setContentText("Buy and Sell coins With Higher Investment to get profit.")
      .setTicker("Every expense is important!")
      .setSmallIcon(R.mipmap.ic_launcher)
      .setAutoCancel(true)
      .setContentIntent(pendingIntent).build();

    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.notify(0, notification);
  }
}
