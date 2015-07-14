package bit.weilytw1.sdhb_client;

import android.content.Context;
import android.content.Intent;
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


public class NotificationsActivity extends ActionBarActivity
{
    //Class Properties
    TextView eTitle;
    TextView eDescription;

    String title;
    String description;
    String location;

    ArrayList<eNotification> notifications;
    eNotification currNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        notifications = new ArrayList<eNotification>();

        try {
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
        }

        //Make custom adapter
        notificationArrayAdapter adapter = new notificationArrayAdapter(this, R.layout.notification_custom_layout, notifications);
        //Get a reference to the NotificationsActivity ListView
        ListView lvEmergencies = (ListView) findViewById(R.id.lvEmergencies);
        //Set the ListView's adapter
        lvEmergencies.setAdapter(adapter);
    }

    //Custom adapter to return layout with two TextViews
    public class notificationArrayAdapter extends ArrayAdapter<eNotification>
    {

        public notificationArrayAdapter(Context context, int resource, ArrayList<eNotification> objects)
        {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container)
        {
            //Get a layout inflater
            LayoutInflater inflater = LayoutInflater.from(NotificationsActivity.this);

            //Inflate notification_custom_layout and store the returned View in a variable
            View customView = inflater.inflate(R.layout.notification_custom_layout, container, false);

            //Get references to the controls in the notification_custom_layout
            eTitle = (TextView) customView.findViewById(R.id.txtTitle);
            eDescription = (TextView) customView.findViewById(R.id.txtDescription);

            //Get the current eNotification instance. Use the Adapter base class's getItem command
            eNotification cn = getItem(getCount() - position - 1);

            //Use the data fields of the current eNotification instance to initialise the View controls correctly
            eTitle.setText(cn.getTitle());
            eDescription.setText(cn.getDescription());

            //Return the customView
            return customView;
        }
    }

    private void initialiseEmergencyArray()
    {
        //Grab all the extras and populate notifications array
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        description = intent.getStringExtra("message");
        location = intent.getStringExtra("location");

        currNotification = new eNotification(title, description, location, new Date());
        notifications.add(currNotification);
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

            eNotification currentNotification = new eNotification(title, description, null, new Date());
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
