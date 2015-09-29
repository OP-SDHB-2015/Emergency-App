package bit.weilytw1.sdhb_client;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Created by weilytw1 on 29/09/2015.
 */
public class PersonalContactsFragment extends Fragment
{
    //Class Properties
    ListView lvContacts;
    Contact[] contacts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.personal_contacts_fragment, container, false);

        return view;
    }
}
