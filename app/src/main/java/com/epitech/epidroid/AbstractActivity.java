package com.epitech.epidroid;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;

public abstract class AbstractActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks{

    private CharSequence mTitle;


    public void restoreActionBar() {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(android.support.v7.app.ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }


    public void onSectionAttached(int number) {
        switch (number) {
            case 2:
                mTitle = getString(R.string.title_section1);
                if (this.getClass() == MainActivity.class)
                    break;
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                break;
            case 3:
                mTitle = getString(R.string.title_section6);
                if (this.getClass() == YearbookActivity.class)
                    break;
                Intent inte = new Intent(getApplicationContext(), YearbookActivity.class);
                startActivity(inte);
                break;
            case 4:
                mTitle = getString(R.string.title_section2);
                if (this.getClass() == CalendarActivity.class)
                    break;
                Intent intente = new Intent(getApplicationContext(), CalendarActivity.class);
                startActivity(intente);
                break;
            case 5:
                mTitle = getString(R.string.title_section3);
                if (this.getClass() == ModulesActivity.class)
                    break;
                Intent i = new Intent(getApplicationContext(), ModulesActivity.class);
                startActivity(i);
                break;
            case 6:
                mTitle = getString(R.string.title_section4);
                if (this.getClass() == ProjectsActivity.class)
                    break;
                Intent inten = new Intent(getApplicationContext(), ProjectsActivity.class);
                startActivity(inten);
                break;
            case 7:
                mTitle = getString(R.string.title_section5);
                Intent in = new Intent(getApplicationContext(), DisconnectActivity.class);
                startActivity(in);
                break;
        }
    }
}
