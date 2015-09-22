package bit.weilytw1.sdhb_client;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

/**
 * This class is for the personal contacts tab of Contacts Activity
 * Allows users to add contacts to the app
 */
public class PersonalContactsActivity extends ActionBarActivity
{
    //Class Properties
    SharedPreferences sharedPreferences;
    Button btnAddContact;
    ListView lvContacts;
    String[] contactNames = new String[0];
    String[] contactNumbers = new String[0];
    Contact[] contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_contacts);

        //Get access to shared preferences -Contact Info-
        sharedPreferences = getSharedPreferences(getResources().getString(R.string.contact_info), Context.MODE_PRIVATE);

        btnAddContact = (Button) findViewById(R.id.btnAddContact);
        lvContacts = (ListView) findViewById(R.id.lvPersonalContacts);

        //Hide the action bar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //btnAddContact.setOnClickListener(new AddContactHandler());
        btnAddContact.setOnClickListener(new BtnAddContactHandler());

        loadContacts();
    }

    /**
     * When the add contact button is clicked
     * Adds the new contact to shared preferences
     */
    public class AddContactHandler implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {

        }
    }

    public void addContact(String contactName, String contactNumber)
    {
        //Add new contact to contact array
        String contactNamesList = sharedPreferences.getString("contactNames", null);
        String contactNumbersList = sharedPreferences.getString("contactNumbers", null);

        String newContactNames;
        String newContactNumbers;

        if(contactNamesList != null) {
            //Add new contact to contacts
            newContactNames = contactNamesList + contactName + ",";
            newContactNumbers = contactNumbersList + contactNumber + ",";
        }
        else
        {
            newContactNames = contactName  + ",";
            newContactNumbers = contactNumber + ",";
        }
        contactNames = newContactNames.split(",");
        contactNumbers = newContactNumbers.split(",");

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("contactNames", newContactNames);
        editor.putString("contactNumbers", newContactNumbers);
        editor.apply();

        loadContacts();
    }

    //[TEST THIS] Prompts the user to add a contact on button click
    public class BtnAddContactHandler implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            //Get the view
            LayoutInflater layoutInflater = LayoutInflater.from(PersonalContactsActivity.this);
            View promptsView = layoutInflater.inflate(R.layout.add_contact_prompt, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PersonalContactsActivity.this);

            //Set xml file to alert dialog builder
            alertDialogBuilder.setView(promptsView);

            final EditText userName = (EditText) promptsView.findViewById(R.id.etName);
            final EditText userNumber = (EditText) promptsView.findViewById(R.id.etNumber);

            //Set up dialog fragment
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("Add",
                            new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int id)
                                {
                                    //Add contact here...
                                    addContact(userName.getText().toString(), userNumber.getText().toString());
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    dialog.cancel();
                                }
                            });

            //Create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            //Show it
            alertDialog.show();
        }
    }

    //Load saved contacts to listview
    public void loadContacts()
    {
        if(sharedPreferences.getString("contactNames", null) != null)
        {
            initialiseContacts();

            //Make custom adapter
            ContactArrayAdapter contactAdapter = new ContactArrayAdapter(this, R.layout.contacts_listview, contacts);
            ListView contactsListView = (ListView) findViewById(R.id.lvPersonalContacts);
            contactsListView.setAdapter(contactAdapter);
        }
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
            LayoutInflater inflater = LayoutInflater.from(PersonalContactsActivity.this);

            View v = inflater.inflate(R.layout.contacts_listview, container, false);

            TextView name = (TextView) v.findViewById(R.id.tvContactName);
            TextView number = (TextView) v.findViewById(R.id.tvContactNumber);

            Contact contact = getItem(position);

            name.setText(contact.getContactName());
            number.setText(contact.getContactNumber());

            return v;
        }
    }

    public void initialiseContacts()
    {
        contacts = new Contact[contactNames.length];

        for (int index=0; index<contactNames.length; index++)
        {
            String contactName = contactNames[index];
            String contactNumber = contactNumbers[index];

            Contact currentContact = new Contact(contactName, contactNumber);

            contacts[index] = currentContact;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_personal_contacts, menu);
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
