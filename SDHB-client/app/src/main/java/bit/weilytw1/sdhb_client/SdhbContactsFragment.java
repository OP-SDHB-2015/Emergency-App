package bit.weilytw1.sdhb_client;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by weilytw1 on 22/09/2015.
 */

public class SdhbContactsFragment extends Fragment
{
    //Class Properties
    ListView lvContacts;
    Contact[] contacts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.sdhb_contacts_fragment, container, false);

        lvContacts = (ListView) view.findViewById(R.id.lvContacts);

        loadContacts();

        //Make custom adapter
        ContactArrayAdapter adapter = new ContactArrayAdapter(getActivity(), R.layout.contacts_listview, contacts);
        lvContacts.setAdapter(adapter);
        lvContacts.setOnItemClickListener(new callContactHandler());
        return view;
    }

    public class callContactHandler implements AdapterView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            //Get selected contacts name and number
            Contact currentContact = contacts[position];
            String cName = currentContact.getContactName();
            final String cNumber = currentContact.getContactNumber();

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Would you like to call " + cName + "?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            //Call contact here
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + cNumber));
                            startActivity(callIntent);
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
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
            LayoutInflater inflater = LayoutInflater.from(getActivity());

            View v = inflater.inflate(R.layout.contacts_listview, container, false);

            TextView name = (TextView) v.findViewById(R.id.tvContactName);
            TextView number = (TextView) v.findViewById(R.id.tvContactNumber);

            Contact contact = getItem(position);

            name.setText(contact.getContactName());
            number.setText(contact.getContactNumber());

            return v;
        }
    }
}
