package bit.weilytw1.emergencyapp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class HomePageActivity extends ActionBarActivity
{
    Button btnNotifications;
    Button btnContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        btnNotifications = (Button) findViewById(R.id.btnNotifications);
        btnContacts = (Button) findViewById(R.id.btnContacts);

        btnNotifications.setOnClickListener(new clickHandlerNotifications());
        btnContacts.setOnClickListener(new clickHandlerContacts());
    }

    public class clickHandlerNotifications implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            Intent notificationsIntent;
            notificationsIntent = new Intent(HomePageActivity.this, NotificationsActivity.class);
            startActivity(notificationsIntent);
        }
    }

    public class clickHandlerContacts implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            Intent contactsIntent;
            contactsIntent = new Intent(HomePageActivity.this, ContactsActivity.class);
            startActivity(contactsIntent);
        }
    }

    ////////////////
    //OTHER METHODS
    ////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_page, menu);
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
