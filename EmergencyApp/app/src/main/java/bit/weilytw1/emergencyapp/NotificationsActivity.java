package bit.weilytw1.emergencyapp;

import android.app.Notification;
import android.content.Context;
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


public class NotificationsActivity extends ActionBarActivity
{
    Emergency[] notifications;
    ListView lstNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        notifications = new Emergency[3];

        initialiseEmergencies();

        //Make emergency adapter
        NotificationAdapter adapter = new NotificationAdapter(this, R.layout.emergency_layout, notifications);
        lstNotifications = (ListView) findViewById(R.id.lstNotifications);
        lstNotifications.setAdapter(adapter);
    }

    //Custom adapter to return layout for notification list
    public class NotificationAdapter extends ArrayAdapter<Emergency>
    {
        public NotificationAdapter(Context context, int resource, Emergency[] objects)
        {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container)
        {
            // Get a LayoutInflater
            LayoutInflater inflater = LayoutInflater.from(NotificationsActivity.this);

            // Inflate custom_list_view and store the returned View in a variable
            View customView = inflater.inflate(R.layout.emergency_layout, container, false);

            TextView ETitle = (TextView) customView.findViewById(R.id.tvETitle);
            TextView EDescription = (TextView) customView.findViewById(R.id.tvEDiscription);
            TextView ELocation = (TextView) customView.findViewById(R.id.tvELocation);

            Emergency currentEmergency = notifications[position];

            ETitle.setText(currentEmergency.title);
            EDescription.setText(currentEmergency.description);
            ELocation.setText(currentEmergency.location);

            return customView;
        }
    }

    public void initialiseEmergencies()
    {
        notifications[0] = new Emergency("Snow Day!","Heavy snow fall today, if you are unable to make it to the hospital please contact us","Dunedin, Otago");
        notifications[1] = new Emergency("Virus Outbreak!", "There is a virus outbreak at the Dunedin Hospital. Hospital is quarantined, no entry or release", "Dunedin, Otago");
        notifications[2] = new Emergency("Earthquake!", "There has been another earthquake", "Christchurch");
    }

    ////////////////
    //OTHER METHODS
    ////////////////
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
