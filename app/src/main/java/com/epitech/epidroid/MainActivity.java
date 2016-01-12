package com.epitech.epidroid;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.*;
import 	android.support.v7.app.ActionBarActivity;
import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends ActionBarActivity {

    private RequestAPI reqHandler = new RequestAPI();
    private ImageRequest imgHandler = new ImageRequest();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EpiContext glob = (EpiContext)getApplication();
        if (glob.token == null) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);

            finish();
        }
        else {
            HashMap<String, String> netOptions = new HashMap<String, String>();
            netOptions.put("requestMethod", "POST");
            netOptions.put("domain", "infos");

            HashMap<String, String> args = new HashMap<String, String>();
            args.put("token", glob.token);

            reqHandler.execute(this, netOptions, args);
        }
    }

    public void requestCallback(JSONObject result) {
        try {
            JSONObject infos = result.getJSONObject("infos");
            String picture = infos.getString("picture");

            imgHandler.execute(this, picture);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void imageCallback(Bitmap image) {
        ImageView img = (ImageView)findViewById(R.id.profileImg);
        img.setImageBitmap(image);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_refresh:
                Toast.makeText(this, "Refresh selected", Toast.LENGTH_SHORT).show();
                break;
            // action with ID action_settings was selected
            case R.id.action_settings:
                EpiContext context = (EpiContext)getApplication();

                if (context.token == null) {
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(i);
                }
                else {
                    Intent i = new Intent(getApplicationContext(), DisconnectActivity.class);
                    startActivity(i);
                }
                break;
            default:
                break;
        }

        return true;
    }
}
