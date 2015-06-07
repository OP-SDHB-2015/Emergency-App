package bit.weilytw1.emergencyapp;

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
import android.widget.TabHost;
import android.widget.TextView;


public class ContactsActivity extends ActionBarActivity
{
    Contact[] contacts;
    TabHost tabContacts;
    //ListView lstPersonal;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        contacts = new Contact[3];
        initialiseContacts();

        tabContacts = (TabHost) findViewById(R.id.tabContacts);
        TabHost.TabSpec personalTab = tabContacts.newTabSpec("Personal Tab");
        TabHost.TabSpec SDHBTab = tabContacts.newTabSpec("SDHB Tab");

        personalTab.setIndicator("Personal");
        personalTab.setContent(new Intent(this, NotificationDetailsActivity.class));
        //Set tab content eg. personalTab.setContent(new Intent(this, PersonalActivity.class));
        SDHBTab.setIndicator("SDHB");
        SDHBTab.setContent(new Intent(this, NotificationDetailsActivity.class));
        tabContacts.addTab(personalTab);
        tabContacts.addTab(SDHBTab);

        //Make contact adapter
        //PersonalContactAdapter adapter = new PersonalContactAdapter(this, R.layout.contact_layout, contacts);
        //lstPersonal = (ListView) findViewById(R.id.lstPersonal);
        //lstPersonal.setAdapter(adapter);
    }

    public class PersonalContactAdapter extends ArrayAdapter<Contact>
    {
        public PersonalContactAdapter(Context context, int resource, Contact[] objects)
        {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container)
        {
            // Get a LayoutInflater
            LayoutInflater inflater = LayoutInflater.from(ContactsActivity.this);

            // Inflate custom_list_view and store the returned View in a variable
            View customView = inflater.inflate(R.layout.contact_layout, container, false);

            TextView CName = (TextView) customView.findViewById(R.id.tvName);
            TextView CNumber = (TextView) customView.findViewById(R.id.tvNumber);

            Contact currentContact = contacts[position];

            CName.setText(currentContact.name);
            CNumber.setText(currentContact.number);

            return customView;
        }
    }

    public void initialiseContacts()
    {
        contacts[0] = new Contact("Sharon", "0279374859");
        contacts[1] = new Contact("David", "0224758976");
        contacts[2] = new Contact("Gary", "02176548776");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contacts, menu);
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
