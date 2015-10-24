package bit.weilytw1.sdhb_client;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
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

    protected SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        //Setup login form
        etStaffID = (EditText) findViewById(R.id.etStaffID);
        etLastName = (EditText) findViewById(R.id.etLastName);
        etFirstName = (EditText) findViewById(R.id.etFirstName);
        spnRegion = (Spinner) findViewById(R.id.spnRegion);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        pbLoad = (ProgressBar) findViewById(R.id.pbLoad);

        //Get access to shared preferences
        sharedPreferences = getSharedPreferences(getResources().getString(R.string.login_prefs), Context.MODE_PRIVATE);

        //Check to see if user has already registered
        if(sharedPreferences.getBoolean("loggedIn", false) == true)
        {
            //Redirect to main activity
            Intent intent = new Intent(LogInActivity.this, MainActivity.class);
            startActivity(intent);
        }

        //What should happen when Register button is clicked
        btnRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Check for internet connection
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                //If any field is left empty prompt user to complete form
                if(etFirstName.getText().toString().trim().length() == 0 || etLastName.getText().toString().trim().length() == 0 || etStaffID.getText().toString().trim().length() == 0)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LogInActivity.this);
                    builder.setTitle("Form incomplete");
                    builder.setMessage("Please complete the form and try again. All fields required.");
                    builder.setPositiveButton("Ok", null);
                    builder.show();
                } //Else if a network connection is available, if so register the users device to server
                else if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
                {
                    getRegId();
                } //Else if a connection is not available, prompt user to check their connection
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LogInActivity.this);
                    builder.setTitle("Unable to connect to server");
                    builder.setMessage("Please check your internet connection and try again.");
                    builder.setPositiveButton("Ok", null);
                    builder.show();
                }
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
                    POSTRegID(Integer.parseInt(etStaffID.getText().toString()), etLastName.getText().toString(), etFirstName.getText().toString(), spnRegion.getSelectedItem().toString(), regid);
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

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("loggedIn", true);
                editor.putString("userName", etFirstName.getText().toString());
                //String[] stuff = "hi,there,tom".split(",");
                editor.apply();

                //Change to main activity (Home Screen)
                Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                startActivity(intent);
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
