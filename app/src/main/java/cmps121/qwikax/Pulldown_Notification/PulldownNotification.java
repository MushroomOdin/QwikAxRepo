package cmps121.qwikax.Pulldown_Notification;

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
        //instance of notificationManager
        NotificationCompat.Builder myBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("test notification")
                .setContentText("suprise!");
        //instance of service
        NotificationManager myNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);



        //001 is the notifcation ID
        myNotificationManager.notify(001, myBuilder.build());
    }
    

}
