package cmps121.qwikax.Pulldown_Notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.view.View;


import cmps121.qwikax.MainActivity;
import cmps121.qwikax.R;

/**
 * Created by Michael Crane on 10/21/2017.
 */

public class PulldownNotification extends MainActivity {

    public void sendNotification(View view){
        //instance of service
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //set channel
        // id of the channel.
        String id = "my_channel_01";
        // name of the channel.
        CharSequence name = getString(R.string.channel_name);
        // description of the channel.
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel mChannel = new NotificationChannel(id, name, importance);
        // Configure the notification channel.
        mChannel.setDescription(description);
        mNotificationManager.createNotificationChannel(mChannel);


        //builds notification object with specifications
        NotificationCompat.Builder myBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("test notification")
                .setContentText("suprise!")
                .setChannel(id);


        //pass object to the system
        mNotificationManager.notify(1, myBuilder.build());
    }
    

}
