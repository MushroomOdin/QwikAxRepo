package cmps121.qwikax.Pulldown_Notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.RemoteViews;


import cmps121.qwikax.MainActivity;
import cmps121.qwikax.R;

/**
 * Created by Michael Crane on 10/21/2017.
 */

public class PulldownNotification extends ContextWrapper {

    private static final String Channel_ID = "channel_01";
    private static final String Channel_Name = "pulldown_channel";
    private NotificationManager manager;

    public PulldownNotification(Context base){
        super(base);
        createChannel();

    }

    public void createChannel(){
        NotificationChannel mChannel = new NotificationChannel(Channel_ID, Channel_Name, NotificationManager.IMPORTANCE_HIGH);
        //note: we can enable lights/vibration/etc. here
        mChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        getManager().createNotificationChannel(mChannel);
    }

    public NotificationManager getManager(){
        if(manager == null){
            manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    public Notification.Builder getmChannelNotification(String title, String body){

        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.custom_notification);

        return new Notification.Builder(getApplicationContext(),Channel_ID)
               // .setContentText(body)
               // .setContentTitle(title)

                .setCustomBigContentView(contentView)
               // .setSmallIcon(R.mipmap.ic_launcher_round)
                .setAutoCancel(true);
    }
    

}
