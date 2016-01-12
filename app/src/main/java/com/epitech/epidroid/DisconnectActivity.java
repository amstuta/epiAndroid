package com.epitech.epidroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

public class DisconnectActivity extends ActionBarActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EpiContext context = (EpiContext)getApplication();
        context.token = null;

        Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT).show();

        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(i);
        finish();
    }
}
