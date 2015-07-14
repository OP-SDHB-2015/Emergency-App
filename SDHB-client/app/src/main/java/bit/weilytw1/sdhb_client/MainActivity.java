package bit.weilytw1.sdhb_client;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;


public class MainActivity extends ActionBarActivity
{
    //Class Properties
    Button btnGetRegID;
    TextView txtDisplayRegID;
    GoogleCloudMessaging gcm;
    String regid;
    String PROJECT_NUMBER = "492814766742";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnGetRegID = (Button) findViewById(R.id.btnGetRegID);
        txtDisplayRegID = (TextView) findViewById(R.id.txtDisplayRegID);

        btnGetRegID.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getRegId();
            }
        });

        //Check if Google Play Services installed on users device...
    }

    /**
     * AsyncTask will register the app with GCM, receive registration id “RegID” & display it on EditText.
     * Here you need to use the Project Number when communicating with GCM
     */
    public void getRegId()
    {
        new AsyncTask<Void, Void, String>()
        {
            @Override
            protected String doInBackground(Void... params)
            {
                String msg = "";
                try
                {
                    if(gcm == null)
                    {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    regid = gcm.register(PROJECT_NUMBER);
                    msg = "Device registered, registration ID = " + regid;
                    Log.i("GCM", msg);
                    POSTregID(regid);
                }
                catch(IOException e)
                {
                    msg = "Error :" + e.getMessage();
                }
                return msg;
            }
            @Override
            protected void onPostExecute(String msg)
            {
                txtDisplayRegID.setText(msg + "\n");
            }
        }.execute(null, null, null);

    }

//----------------------------Sending Registered ID to Rails Server----------------------------------------------

    // Create  POST id Method
    public  String  POSTregID(String regID)
    {
        String urlString = "http://128.199.73.221:3000/emergency_notifications/registerID";
        String myJSONString = "";
        String userCredentials = "registrationID=" + regID;
        String result = "";
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
            writer.write(userCredentials);

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
                result = "failed cos " + responseCode;
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

    /*throws UnsupportedEncodingException
    {
        // Create data variable for sent values to server
        String ID = URLEncoder.encode("Registration_ID", "UTF-8")
                + "=" + URLEncoder.encode(regID, "UTF-8");

        String text = "";
        BufferedReader reader=null;

        // Send data
        try
        {

            // Defined URL  where to send data
            URL url = new URL("http://128.199.73.221:3000/registerID");

            // Send POST data request

            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write( ID );
            wr.flush();

            // Get the server response

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read Server Response
            while((line = reader.readLine()) != null)
            {
                // Append server response in string
                sb.append(line + "\n");
            }


            text = sb.toString();
        }
        catch(Exception ex)
        {

        }
        finally
        {
            try
            {

                reader.close();
            }

            catch(Exception ex) {}
        }*/

//---------------------------------------------------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
