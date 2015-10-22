package bit.weilytw1.sdhb_client;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class NotificationsActivity extends ActionBarActivity
{
    //Class Properties
    TextView eTitle;
    TextView eDescription;
    TextView eDate;
    ListView lvNotifications;

    String title;
    String description;
    String location;
    String date;

    List<eNotification> notifications; //Might need to be List<>
    NotificationArrayAdapter notificationAdapter;
    eNotification currNotification;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        //Get access to shared preferences -Contact Info-
        sharedPreferences = getSharedPreferences("NotificationInfo", Context.MODE_PRIVATE);

        //notifications = new List<eNotification>();

        /*try {
            fetchEmergencies();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        initialiseEmergencyArray();

        try {
            convertToJSON();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        lvNotifications = (ListView) findViewById(R.id.lvEmergencies);

        setupNotificationsListView();

        //Make custom adapter
        //notificationArrayAdapter adapter = new notificationArrayAdapter(this, R.layout.notification_custom_layout, notifications);
        //Get a reference to the NotificationsActivity ListView
        //ListView lvEmergencies = (ListView) findViewById(R.id.lvEmergencies);
        //Set the ListView's adapter
        //lvEmergencies.setAdapter(adapter);
    }

    private void initialiseEmergencyArray()
    {
        //Grab all the extras and populate notifications array
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        description = intent.getStringExtra("message");
        date = intent.getStringExtra("time");
        location = intent.getStringExtra("location");

        currNotification = new eNotification(title, description, location, date);
        notifications.add(currNotification);
    }

    public class NotificationArrayAdapter extends ArrayAdapter<eNotification>
    {
        public NotificationArrayAdapter(Context context, int resource, List<eNotification> objects)
        {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container)
        {
            LayoutInflater inflater = LayoutInflater.from(NotificationsActivity.this);

            View v = inflater.inflate(R.layout.notification_custom_layout, container, false);
            TextView title = (TextView) v.findViewById(R.id.txtTitle);
            TextView description = (TextView) v.findViewById(R.id.txtDescription);
            TextView date = (TextView) v.findViewById(R.id.txtTime);

            eNotification notification = getItem(position);

            title.setText(notification.getTitle());
            description.setText(notification.getDescription());
            date.setText(notification.getDate());

            return v;
        }
    }

    protected void setupNotificationsListView()
    {
        //Make custom adapter
        notificationAdapter = getNotificationsAdapter();

        lvNotifications.setAdapter(notificationAdapter);
    }

    //Fetch notifications from shared preferences
    protected NotificationArrayAdapter getNotificationsAdapter()
    {
        List<eNotification> notifications = new ArrayList<eNotification>();
        String notificationTitles = sharedPreferences.getString("eTitles", null);
        String notificationDescriptions = sharedPreferences.getString("eDescriptions", null);
        String notificationDates = sharedPreferences.getString("eDates", null);

        if(notificationTitles != null || notificationDescriptions != null || notificationDates != null)
        {
            String[] eTitles = notificationTitles.split(",");
            String[] eDescriptions = notificationDescriptions.split(",");
            String[] eDates = notificationDates.split(",");

            for(int index=0; index<eTitles.length; index++)
            {
                String currentTitle = eTitles[index];
                String currentDescription = eDescriptions[index];
                String currentDate = eDates[index];

                eNotification currentNotification = new eNotification(currentTitle, currentDescription, null, currentDate);
                notifications.add(currentNotification);
            }
        }
        return new NotificationArrayAdapter(NotificationsActivity.this, R.layout.notification_custom_layout, notifications);
    }

    /**
     * Fetch and read in JSON file and store emergencies in notifications array
     * @throws IOException
     * @throws JSONException
     */
    public void fetchEmergencies() throws IOException, JSONException {
        String JSONString = null;

        //Get input
        //InputStream inputStream = getResources().getAssets().open("eStorage.json");
        InputStream inputStream = openFileInput("eStorage.json"); //Trying to access raw/eStorage.json
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        //Read the input
        String responseString;
        StringBuilder stringBuilder = new StringBuilder();
        while((responseString = bufferedReader.readLine()) != null)
        {
            stringBuilder = stringBuilder.append(responseString);
        }

        //Get the string from stringBuilder
        JSONString = stringBuilder.toString();

        //Close connection
        inputStream.close();

        //Clear notifications array
        notifications.clear();

        //Convert fetched string to JSONObject
        JSONObject eObject = new JSONObject(JSONString);
        JSONArray eArray = eObject.getJSONArray("emergencies");
        int arrayLength = eArray.length();

        for(int index=0; index<arrayLength; index++)
        {
            //Access elements data
            JSONObject currentEmergency = eArray.getJSONObject(index);
            String title = currentEmergency.getString("title");
            String description = currentEmergency.getString("description");
            String date = currentEmergency.getString("date");

            eNotification currentNotification = new eNotification(title, description, null, date);
            notifications.add(currentNotification);
        }
    }

    /**
     * Convert all notifications to JSON and store the internally
     * @throws JSONException
     * @throws IOException
     */
    public void convertToJSON() throws JSONException, IOException {
        //Create a JSONObject and JSONArray to hold all data
        JSONObject baseObject = new JSONObject();
        JSONArray eArray = new JSONArray();

        int arrayLength = notifications.size();

        for(int index=0; index<arrayLength; index++)
        {
            JSONObject currentObject = new JSONObject();
            currentObject.put("title", notifications.get(index).getTitle());
            currentObject.put("description", notifications.get(index).getDescription());
            eArray.put(currentObject);
        }
        baseObject.put("emergencies", eArray);

        //STORE FILE
        String FILENAME = "eStorage.json";
        String JSONString = baseObject.toString();

        FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
        fos.write(JSONString.getBytes());
        fos.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notifications, menu);
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
