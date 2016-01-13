package com.epitech.epidroid;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.widget.ImageView;
import android.view.*;
import 	android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Iterator;


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
            displayInfos();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void imageCallback(Bitmap image) {
        ImageView img = (ImageView)findViewById(R.id.profileImg);
        img.setImageBitmap(image);
    }


    private void displayInfos() {
        JSONObject infos = appContext.userInfos;

        if (infos == null)
            return;

        try {

            String messages = "";
            JSONArray history = infos.getJSONArray("history");

            for (int i=0; i < history.length(); ++i) {
                System.out.println(history.getJSONObject(i));

                messages += Html.fromHtml(history.getJSONObject(i).getString("title") + "<br/>");
            }
            TextView msgs = (TextView)findViewById(R.id.messages);
            msgs.setText(messages);

            JSONObject inf = infos.getJSONObject(getString(R.string.domain_infos));
            String log = inf.getString(getString(R.string.logTime));
            TextView msgsN = (TextView)findViewById(R.id.logTime);
            msgsN.setText(log != "null"? log : "0");


            /*
            Iterator<?> keys = infos.keys();

            while( keys.hasNext() ) {
                String key = (String)keys.next();

                System.out.println(key);
                System.out.println(infos.get(key));

            }*/

            //JSONArray current = infos.getJSONArray(getString(R.string.current));

            /*JSONObject history = infos.getJSONObject("history");
            JSONObject cur = history.getJSONObject("current");

            System.out.println(cur);*/
            /*for (int i = 0; i < current.length(); ++i) {
                System.out.println(current.get(i));
            }*/

            //String logTime = current.getString(getString(R.string.logTime));

            //TextView log = (TextView)findViewById(R.id.logTime);
            //log.setText("Log time: " + logTime);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
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
