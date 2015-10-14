package bit.weilytw1.sdhb_client;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.view.View;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.client.ClientProtocolException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by weilytw1 on 13/06/2015.
 * This is class will receive the GCM message & pass it to the GcmMessageHandler
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        executeAsync();
        //Explicitly specify that GcmMessageHandler will handle the intent
        ComponentName comp = new ComponentName(context.getPackageName(), GcmMessageHandler.class.getName());

        //Start the service, keeping the device awake while it is launching
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }

    public String successResult()
    {
        String urlString = "http://128.199.73.221:3000/emergency_notifications/countSuccess";
        String result = "1";

        try
        {
            URL postTestUrl = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) postTestUrl.openConnection();

            // Configure for POST
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            // Configure for receiving a response
            connection.setDoInput(true);

            // Make an output stream to the remote machine using your HttpURLConnection object
            OutputStream outputStream = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

            // Write out your post data. Separate multiple key=value pairs with &
            writer.write(result);

            // Tidy up
            writer.flush();
            writer.close();
            outputStream.close();


            // Read the response in the usual way
            int responseCode = connection.getResponseCode();

            if (responseCode == 200)
            {
                result = "Success";
            }
            else
            {
                result = "failed due to:" + responseCode;
            }
            connection.disconnect();

        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        catch (ClientProtocolException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    public void executeAsync()
    {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params)
            {
                String msg = "";
                    successResult();
                return msg;
            }
        }.execute(null, null, null);
    }
}
