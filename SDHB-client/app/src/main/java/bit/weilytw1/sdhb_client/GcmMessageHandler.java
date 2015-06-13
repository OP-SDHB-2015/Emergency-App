package bit.weilytw1.sdhb_client;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.logging.Handler;
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
    String mes;
    private Handler handler;

    public GcmMessageHandler(){
        super("GcmMessageHandler");}

    @Override
    public void onCreate()
    {
        super.onCreate();
        handler = new Handler() {
            @Override
            public void close() {
            }

            @Override
            public void flush() {
            }

            @Override
            public void publish(LogRecord record) {
            }
        };
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
        showToast();
        Log.i("GCM", "Recieved : (" + messageType + ") " + extras.getString("title"));

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
}
