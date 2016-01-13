package com.epitech.epidroid;

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

    private RequestAPI      reqHandler = new RequestAPI();
    private ImageRequest    imgHandler = new ImageRequest();
    private EpiContext      appContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appContext = (EpiContext)getApplication();

        if (appContext.token == null) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);

            finish();
        }
        else {
            HashMap<String, String> netOptions = new HashMap<String, String>();
            netOptions.put(getString(R.string.request_method), getString(R.string.request_method_post));
            netOptions.put(getString(R.string.domain), getString(R.string.domain_infos));

            HashMap<String, String> args = new HashMap<String, String>();
            args.put(getString(R.string.token), appContext.token);

            reqHandler.execute(this, netOptions, args);
        }
    }

    public void requestCallback(JSONObject result) {

        if (result != null)
            appContext.userInfos = result;

        try {
            JSONObject infos = result.getJSONObject(getString(R.string.domain_infos));
            String picture = infos.getString(getString(R.string.picture));

            imgHandler.execute(this, picture);
        }
        catch (Exception e) {
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

            case R.id.action_refresh:
                break;

            case R.id.action_settings:
                if (appContext.token == null) {
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
