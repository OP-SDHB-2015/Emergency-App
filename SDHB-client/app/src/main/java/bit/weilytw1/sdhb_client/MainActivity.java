package bit.weilytw1.sdhb_client;

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
import android.widget.TextView;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;


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

//    public class GCMSetup extends IntentService
//    {
//        /**
//         * Creates an IntentService.  Invoked by your subclass's constructor.
//         *
//         * @param name Used to name the worker thread, important only for debugging.
//         */
//        public GCMSetup(String name) {
//            super(name);
//        }
//
//        @Override
//        protected void onHandleIntent(Intent intent)
//        {
//            //Obtain registration token
//            InstanceID instanceID = InstanceID.getInstance(this);
//            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
//        }
//
//        @Override
//        public void onTokenRefresh()
//        {
//            // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
//            Intent intent = new Intent(this, RegistrationIntentService.class);
//            startService(intent);
//        }
//    }

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
