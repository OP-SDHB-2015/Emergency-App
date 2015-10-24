package bit.weilytw1.sdhb_client;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import android.os.Handler;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.text.SimpleDateFormat;
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
    String date;
    //Bundle to hold incoming emergency data for NotificationsActivity
    Bundle bundle;

    private Handler handler;
    //Array to hold all notifications received on device
    ArrayList<eNotification> notifications;
    SharedPreferences sharedPreferences;

    int currentVolume;

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

        sharedPreferences = getSharedPreferences("NotificationInfo", Context.MODE_PRIVATE);

        //Set default values for all fields
        mes = "Null";
        des = "Null";
        loc = "Null";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");
        date = sdf.format(new Date());
    }

    public void addEmergency(String title, String description, String date)
    {
        String notificationTitles = sharedPreferences.getString("eTitles", null);
        String notificationDescriptions = sharedPreferences.getString("eDescriptions", null);
        String notificationDates = sharedPreferences.getString("eDates", null);

        String net;
        String ned;
        String neds;

        if(notificationTitles != null)
        {
            //Add new notification
            net = notificationTitles + title + ",";
            ned = notificationDescriptions + description + ",";
            neds = notificationDates + date + ",";
        }
        else
        {
            net = title + ",";
            ned = description + ",";
            neds = date + ",";
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("eTitles", net);
        editor.putString("eDescriptions", ned);
        editor.putString("eDates", neds);
        editor.apply();
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
        //Get current volume setting to revert back to after push notification
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);

        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, maxVolume, AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND);
        //Finish increasing volume

        //Pending activity that opens Notifications activity and passes data when push notification clicked
        PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, NotificationsActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        Resources r = getResources();

        addEmergency(mes, des, date);

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

        //Sleep for 3 second then set volume back to normal
        Thread timerThread = new Thread()
        {
            public void run()
            {
                try
                {
                    sleep(3000);
                }
                catch(InterruptedException e)
                {
                    e.printStackTrace();
                }finally
                {
                    //Modify phones volume when notification received
                    AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                    //Revert phone audio volume back
                    audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, currentVolume, AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND);
                }
            }
        };
        timerThread.start();
    }
}
