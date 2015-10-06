package bit.weilytw1.sdhb_client;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

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
    String[] contactNames = new String[0];
    String[] contactNumbers = new String[0];
    Contact[] contacts;
    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        v = inflater.inflate(R.layout.personal_contacts_fragment, container, false);

        //Get access to shared preferences -Contact Info-
        sharedPreferences = this.getActivity().getSharedPreferences("ContactInfo", Context.MODE_PRIVATE);

        btnAddContact = (Button) v.findViewById(R.id.btnAddContact);
        lvContacts = (ListView) v.findViewById(R.id.lvContacts);

        //btnAddContact.setOnClickListener(new AddContactHandler());
        btnAddContact.setOnClickListener(new BtnAddContactHandler());

        loadContacts();

        return v;
    }

    //This method removes the contact associated with it
    public class RemoveContact implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            v.getParent();

            int index = v.getId();
            removeContact(index);
        }
    }

    public void removeContact(int index)
    {
        //Get contacts from shared preferences
        String contactNamesList = sharedPreferences.getString("contactNames", null);
        String contactNumbersList = sharedPreferences.getString("contactNumbers", null);

        //Split strings into string array
        String[] names = contactNamesList.split(",");
        String[] numbers = contactNumbersList.split(",");

        ArrayList<String> cNames = new ArrayList<String>();
        ArrayList<String> cNumbers = new ArrayList<String>();

        for(int i=0; i<names.length; i++)
        {
            cNames.add(names[i]);
            cNumbers.add(numbers[i]);
        }

        //Remove selected contact from list
        cNames.remove(index);
        cNumbers.remove(index);

        String newContactNames = "";
        String newContactNumbers = "";

        for(int n=0; n<cNames.size(); n++)
        {
            newContactNames = newContactNames + cNames.get(n) + ",";
            newContactNumbers = newContactNumbers + cNumbers.get(n) + ",";
        }

        //Repopulate array
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("contactNames", newContactNames);
        editor.putString("contactNumbers", newContactNumbers);
        editor.apply();

        loadContacts();
    }

    public void addContact(String contactName, String contactNumber)
    {
        //Add new contact to contact array
        String contactNamesList = sharedPreferences.getString("contactNames", null);
        String contactNumbersList = sharedPreferences.getString("contactNumbers", null);

        String newContactNames;
        String newContactNumbers;

        if(contactNamesList != null)
        {
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

    //Load saved contacts to listview
    public void loadContacts()
    {
        if(sharedPreferences.getString("contactNames", null) != null)
        {
            initialiseContacts();

            //Make custom adapter
            ContactArrayAdapter contactAdapter = new ContactArrayAdapter(getActivity(), R.layout.personal_contacts_fragment, contacts);
            ListView contactsListView = (ListView) v.findViewById(R.id.lvPersonalContacts);
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
            LayoutInflater inflater = LayoutInflater.from(getActivity());

            View v = inflater.inflate(R.layout.personal_listview, container, false);

            TextView name = (TextView) v.findViewById(R.id.tvContactName);
            TextView number = (TextView) v.findViewById(R.id.tvContactNumber);

            btnRemove = (Button) v.findViewById(R.id.btnRemove);
            btnRemove.setOnClickListener(new BtnRemoveContactHandler(position));

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

    public class BtnRemoveContactHandler implements View.OnClickListener
    {
        private int index;

        public BtnRemoveContactHandler(int index)
        {
            this.index = index;
        }

        @Override
        public void onClick(final View v)
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

            alertDialogBuilder
                    .setCancelable(true)
                    .setMessage("Are you sure you want to delete this contact?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            //Remove contact
                            removeContact(index);
                        }
                    })
                    .setNegativeButton("No",
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.cancel();
                            }
                        });
                    /*.setPositiveButton("Yes",
                            (dialog, id) -> {
                                //Remove contact
                                int index = (Integer) v.getTag();
                                removeContact(index);
                            });*/

            //Create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            //Show it
            alertDialog.show();
        }
    }
}
