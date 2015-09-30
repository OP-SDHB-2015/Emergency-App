package bit.weilytw1.sdhb_client;

import android.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;

import java.lang.reflect.Type;
import java.util.zip.Inflater;


public class ContactsActivity extends FragmentActivity
{
    //Class Properties
    //TabHost tabHost;
    FragmentTabHost fTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        fTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        fTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        fTabHost.addTab(fTabHost.newTabSpec("personal").setIndicator("Personal"),
                        PersonalContactsFragment.class, null);
        fTabHost.addTab(fTabHost.newTabSpec("sdhb").setIndicator("SDHB"),
                        SdhbContactsFragment.class, null);

        /**
        tabHost = (TabHost)findViewById(android.R.id.tabhost);

        TabHost.TabSpec tab1 = tabHost.newTabSpec("tabPersonal");
        TabHost.TabSpec tab2 = tabHost.newTabSpec("tabSDHB");

        tab1.setIndicator("Personal");
        tab1.setContent(new Intent(this, PersonalContactsActivity.class));
        tab2.setIndicator("SDHB");
        View v = this.getLayoutInflater().inflate(R.layout.sdhb_contacts_fragment, null);
        tab2.setContent(v.getId());

        tabHost.addTab(tab1);
        tabHost.addTab(tab2);
         **/
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
