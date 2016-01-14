package com.epitech.epidroid;

import android.support.v7.app.ActionBarActivity;

public abstract class AbstractActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks{
    public abstract void onSectionAttached(int stuff);
}
