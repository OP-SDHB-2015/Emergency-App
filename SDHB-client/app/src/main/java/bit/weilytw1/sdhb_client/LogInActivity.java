package bit.weilytw1.sdhb_client;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.client.ClientProtocolException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;


public class LogInActivity extends ActionBarActivity
{
    //CLASS PROPERTIES
    ProgressBar pbLoad;
    GoogleCloudMessaging gcm;
    String regid;
    String PROJECT_NUMBER = "492814766742";
    EditText etStaffID;
    EditText etLastName;
    EditText etFirstName;
    Spinner spnRegion;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        //Setup
        etStaffID = (EditText) findViewById(R.id.etStaffID);
        etLastName = (EditText) findViewById(R.id.etLastName);
        etFirstName = (EditText) findViewById(R.id.etFirstName);
        spnRegion = (Spinner) findViewById(R.id.spnRegion);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        pbLoad = (ProgressBar) findViewById(R.id.pbLoad);

        btnRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getRegId();
            }
        });
    }

//---------------------------------------------------------------------------------------------------------------
    /**
     * AsyncTask will register the app with GCM, receive registration id �RegID� & display it on EditText.
     * Here you need to use the Project Number when communicating with GCM
     */
    public void getRegId()
    {
        pbLoad.setVisibility(View.VISIBLE);
        pbLoad.animate();
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
                    //msg = "Device registered, registration ID = " + regid;
                    msg = "Device registered. You phone is now ready to receive notifications.";
                    Log.i("GCM", msg);
                    POSTRegID(Integer.parseInt(etStaffID.getText().toString()), etLastName.getText().toString(), etFirstName.getText().toString(),spnRegion.getSelectedItem().toString(),regid);

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
                pbLoad.setVisibility(View.INVISIBLE);
            }
        }.execute(null, null, null);


    }

//----------------------------Sending Registered ID to Rails Server----------------------------------------------

    // Create  POST id Method
    public  String  POSTRegID(int staffID, String lastName, String firstName, String region, String regID)
    {
        String urlString = "http://128.199.73.221:3000/users/registerID";
        String myJSONString = "";
        String userCredentials = "staffID=" + staffID + "&lastName=" + lastName + "&firstName=" + firstName + "&region=" + region + "&registrationID=" + regID;
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
//----------------------------------------------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_log_in, menu);
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
