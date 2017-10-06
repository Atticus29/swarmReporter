package fisherdynamic.swarmreporter1.models;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import fisherdynamic.swarmreporter1.R;

/**
 * Created by mf on 10/6/17.
 */

public class SwarmNotification {
    public SwarmNotification(String ticker,String title, String message, View view) {
        Context context = view.getContext();
        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0, intent,0);
        Notification notification = new Notification.Builder(context)
                .setTicker(ticker)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_announcement_black_24dp)
                .setContentIntent(pendingIntent).getNotification();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }
}
