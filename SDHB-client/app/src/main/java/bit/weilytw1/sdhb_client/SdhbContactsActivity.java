package bit.weilytw1.sdhb_client;

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


public class SdhbContactsActivity extends ActionBarActivity
{
    //Class Properties
    ListView lvContacts;
    Contact[] contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdhb_contacts);

        lvContacts = (ListView) findViewById(R.id.lvContacts);

        //Hide the action bar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        loadContacts();

        //Make custom adapter
        ContactArrayAdapter adapter = new ContactArrayAdapter(this, R.layout.contacts_listview, contacts);
        lvContacts.setAdapter(adapter);
    }

    public void loadContacts()
    {
        //Contacts
        //Southern District Health Board main number
        Contact sdhb = new Contact("SDHB", "(03) 474 00999");
        //Hospitals
        Contact southland = new Contact("Southland Hospital", "(03) 218 1949");
        Contact lakes = new Contact("Lakes District Hostpital", "(03) 441 0015");
        Contact dunedin = new Contact("Dunedin Hospital", "(03) 474 0999");
        Contact wakari = new Contact("Wakari Hospital", "(03) 476 2191");
        //Departments
        Contact compD = new Contact("Complements & Complaints (Otago)", "(03) 470 9534");
        Contact compS = new Contact("Complements & Complaints (Southland)", "(03) 214 5738");

        contacts = new Contact[]{sdhb, dunedin, southland, lakes, wakari, compD, compS};
    }

    public class ContactArrayAdapter extends ArrayAdapter<Contact>
    {

        public ContactArrayAdapter(Context context, int resource, Contact[] objects)
        {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container)
        {
            LayoutInflater inflater = LayoutInflater.from(SdhbContactsActivity.this);

            View v = inflater.inflate(R.layout.contacts_listview, container, false);

            TextView name = (TextView) v.findViewById(R.id.tvContactName);
            TextView number = (TextView) v.findViewById(R.id.tvContactNumber);

            Contact contact = getItem(position);

            name.setText(contact.getContactName());
            number.setText(contact.getContactNumber());

            return v;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sdhb_contacts, menu);
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
