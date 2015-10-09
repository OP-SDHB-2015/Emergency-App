package bit.weilytw1.sdhb_client;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import android.os.Handler;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.LogRecord;

/**
 * Created by weilytw1 on 13/06/2015.
 * This class defines what to do with the received message.
 * The received message will contain in its data part “as we will see later in GCM server step”
 * two attributes title & message, we will extract “title”
 * value from the intent extras & display it on a Toast
 */
public class GcmMessageHandler extends IntentService
{
    //Class properties
    //mes stands for message
    String mes;
    //des stands for description
    String des;
    //loc stands for location
    String loc;
    //time is the time of emergency
    Date time;
    //Bundle to hold incoming emergency data for NotificationsActivity
    Bundle bundle;

    private Handler handler;
    //Array to hold all notifications received on device
    ArrayList<eNotification> notifications;

    public GcmMessageHandler()
    {
        super("GcmMessageHandler");
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        handler = new Handler();
        bundle = new Bundle();
        //Initialise notifications array
        notifications = new ArrayList<eNotification>();
        //Clear the array
        notifications.clear();

        //Set default values for all fields
        mes = "Null";
        des = "Null";
        loc = "Null";
        time = new Date();
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Bundle extras = intent.getExtras();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        mes = extras.getString("title");
        des = extras.getString("message");
        loc = extras.getString("location"); //Returns null. Requires server field?

        bundle.putString("title", mes);
        bundle.putString("message", des);
        bundle.putString("location", loc);

        //Create a notification to store latest emergency
        //eNotification n = new eNotification(mes, des, loc, time);
        //add that notification to the array
        //notifications.add(0, n);

        //showToast();
        pushNotification();
        Log.i("GCM", "Received : (" + messageType + ") " + extras.getString("title"));

        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    public void showToast()
    {
        handler.post(new Runnable()
        {
            public void run()
            {
                Toast.makeText(getApplicationContext(), mes, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void pushNotification()
    {

        //Modify phones volume when notification received
        AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION); //Get current volume setting to revert back to after push notification

        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, maxVolume, AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND);
        //Finish increasing volume

        PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, NotificationsActivity.class).putExtras(bundle), PendingIntent.FLAG_UPDATE_CURRENT);
        Resources r = getResources();

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle(mes)
                .setContentText(des)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);

        //Revert phone audio volume back
        audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, currentVolume, AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND);


//        //For push notifications
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
//                .setContentTitle(mes)
//                .setContentText(des);
//        //Creates an explicit intent for the Notifications activity
//        Intent notificationIntent = new Intent(this, NotificationsActivity.class);
//        /**The stackBuilder object contains an
//         * artificial back stack for the started activity
//         *This ensures that navigating backward from the
//         * activity leads out of the application to the home screen**/
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//        //Adds the back stack for the Intent (But not the Intent itself)
//        stackBuilder.addParentStack(HomePageActivity.class);
//        //Adds the Intent that starts the Activity to the top of the stack
//        stackBuilder.addNextIntent(notificationIntent);
//        PendingIntent notificationPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//        mBuilder.setContentIntent(notificationPendingIntent);
//        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        mNotificationManager.notify(0, mBuilder.build());
    }
}
