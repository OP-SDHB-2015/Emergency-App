package bit.weilytw1.sdhb_client;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by weilytw1 on 29/09/2015.
 */
public class PersonalContactsFragment extends Fragment
{
    //Class Properties
    SharedPreferences sharedPreferences;
    android.support.v7.app.ActionBar actionBar;
    Button btnAddContact;
    Button btnRemove;
    ListView lvContacts;
    List<Contact> contacts;
    ContactArrayAdapter contactAdapter;
    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        v = inflater.inflate(R.layout.personal_contacts_fragment, container, false);

        //Get access to shared preferences -Contact Info-
        sharedPreferences = this.getActivity().getSharedPreferences("ContactInfo", Context.MODE_PRIVATE);

        btnAddContact = (Button) v.findViewById(R.id.btnAddContact);

        lvContacts = (ListView) v.findViewById(R.id.lvPersonalContacts);
        setupContactsListView();

        //btnAddContact.setOnClickListener(new AddContactHandler());
        btnAddContact.setOnClickListener(new BtnAddContactHandler());

        return v;
    }

    protected void setupContactsListView()
    {
        //Make custom adapter
        contactAdapter = getContactsAdapter();

        //ListView contactsListView = (ListView) v.findViewById(R.id.lvPersonalContacts);
        lvContacts.setAdapter(contactAdapter);

        lvContacts.setOnItemLongClickListener(new ContactItemClickHandler());
        lvContacts.setOnItemClickListener(new ContactItemClickHandler());
    }

    protected ContactArrayAdapter getContactsAdapter()
    {
        List<Contact> contacts = new ArrayList<Contact>();
            String contactNamesList = sharedPreferences.getString("contactNames", null);
            String contactNumbersList = sharedPreferences.getString("contactNumbers", null);

        if(contactNamesList != null || contactNumbersList != null) {
            String[] contactNames = contactNamesList.split(",");
            String[] contactNumbers = contactNumbersList.split(",");


            for (int index = 0; index < contactNames.length; index++) {
                String contactName = contactNames[index];
                String contactNumber = contactNumbers[index];

                Contact currentContact = new Contact(contactName, contactNumber);

                contacts.add(currentContact);
            }
        }

        return new ContactArrayAdapter(getActivity(), R.layout.personal_contacts_fragment, contacts);
    }

    protected void saveContacts(ContactArrayAdapter contactsAdapter)
    {
        String newContactNames = "";
        String newContactNumbers = "";

        for(int i = 0; i < contactsAdapter.getCount(); i++)
        {
            Contact currentContact =  contactsAdapter.getItem(i);
            newContactNames += currentContact.getContactName() + ",";
            newContactNumbers += currentContact.getContactNumber() + ",";
        }

        //Repopulate array
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("contactNames", newContactNames);
        editor.putString("contactNumbers", newContactNumbers);
        editor.apply();
    }

    public class ContactArrayAdapter extends ArrayAdapter<Contact>
    {
        public ContactArrayAdapter(Context context, int resource, List<Contact> objects)
        {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container)
        {
            LayoutInflater inflater = LayoutInflater.from(getActivity());

            View v = inflater.inflate(R.layout.personal_listview, container, false);
            TextView name = (TextView) v.findViewById(R.id.tvContactName);
            TextView number = (TextView) v.findViewById(R.id.tvContactNumber);

            Contact contact = getItem(position);

            name.setText(contact.getContactName());
            number.setText(contact.getContactNumber());

            return v;
        }
    }



    public class ContactItemClickHandler implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener
    {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
            adb.setTitle("Contact Options");
            final int positionToRemove = position;
            adb.setItems(new CharSequence[]{"Dial", "Edit", "Delete"}, new AlertDialog.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    switch (which)
                    {
                        case 0:
                            Contact selectedContact = contactAdapter.getItem(position);
                            String phoneNumber = selectedContact.getContactNumber();
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + phoneNumber));
                            startActivity(callIntent);
                            break;
                        case 1:
                            //Get the view
                            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                            View promptsView = layoutInflater.inflate(R.layout.add_contact_prompt, null);

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

                            //Set xml file to alert dialog builder
                            alertDialogBuilder.setView(promptsView);

                            final EditText userName = (EditText) promptsView.findViewById(R.id.etName);
                            final EditText userNumber = (EditText) promptsView.findViewById(R.id.etNumber);

                            //Set up dialog fragment
                            alertDialogBuilder
                                    .setCancelable(false)
                                    .setPositiveButton("Edit",
                                            new DialogInterface.OnClickListener()
                                            {
                                                public void onClick(DialogInterface dialog, int id)
                                                {
                                                    //Edit contact here...
                                                    Contact currentContact = contactAdapter.getItem(position);
                                                    currentContact.setContactName(userName.getText().toString());
                                                    currentContact.setContactNumber(userNumber.getText().toString());
                                                    saveContacts(contactAdapter);
                                                }
                                            })
                                    .setNegativeButton("Cancel", null);

                            //Create alert dialog
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            //Show it
                            alertDialog.show();
                            break;
                        case 2:
                            removeContact(position);
                            break;
                    }
                }});
            adb.setNegativeButton("Cancel", null);

            adb.show();
        }

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            return false;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.contacts_context_menu, menu);

        //Create context menu for personal contacts
        if(v.getId() == R.id.lvPersonalContacts) //Check this
        {
            ListView lv = (ListView) v;
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            String[] menuItems = {"Dial", "Delete"};
            menu.setHeaderTitle("What would you like to do?");

            for(int i=0; i<menuItems.length; i++)
            {
                menu.add(Menu.NONE,i, i, menuItems[i]);
            }
        }
    }

    public void removeContact(int index)
    {
        contactAdapter.remove(contactAdapter.getItem(index));
        saveContacts(contactAdapter);
        contactAdapter.notifyDataSetChanged();
    }

    public void addContact(String contactName, String contactNumber)
    {
        Contact newContact = new Contact(contactName, contactNumber);
        contactAdapter.add(newContact);
        saveContacts(contactAdapter);
        contactAdapter.notifyDataSetChanged();
    }


    //[TEST THIS] Prompts the user to add a contact on button click
    public class BtnAddContactHandler implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            //Get the view
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View promptsView = layoutInflater.inflate(R.layout.add_contact_prompt, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

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
}
