package com.epitech.epidroid;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;

public abstract class AbstractActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks{

    private CharSequence mTitle;

    public abstract void onSectionAttached(int stuff);

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
}
