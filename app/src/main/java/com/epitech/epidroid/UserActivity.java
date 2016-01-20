package com.epitech.epidroid;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.w3c.dom.Text;

public class UserActivity extends AbstractActivity{

    private NavigationDrawerFragment    mNavigationDrawerFragment;
    private CharSequence                mTitle;
    private EpiContext appContext;
    private JsonObject userInfos = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        appContext = (EpiContext)getApplication();

        mNavigationDrawerFragment = (NavigationDrawerFragment)getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
        final TextView call = (TextView)findViewById(R.id.phone_number);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + call.getText().toString()));
                startActivity(intent);
            }
        });
        final TextView mail = (TextView)findViewById(R.id.mail_value);
        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{mail.getText().toString()});
                startActivity(Intent.createChooser(intent, "Send mail"));
            }
        });
        try {
            Ion.with(getApplicationContext())
                    .load(getString(R.string.request_method_get), getString(R.string.api_domain) + getString(R.string.domain_user))
                    .setBodyParameter(getString(R.string.token), appContext.token)
                    .setBodyParameter(getString(R.string.domain_user),
                            getIntent().getExtras().getString(getString(R.string.prompt_login)))
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            userCallback(result);
                        }
                    });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public void userCallback(JsonObject result) {
        try {
            System.out.println(result);
            TextView name = (TextView)findViewById(R.id.title_year);
            TextView gpa = (TextView) findViewById(R.id.gpa_year);
            TextView credits = (TextView)findViewById(R.id.credits_year);
            TextView logTime = (TextView)findViewById(R.id.logTime_year);
            name.setText(result.get("title").getAsString());
            gpa.setText("GPA Bachelor: " + result.get("gpa").getAsJsonArray().get(0).getAsJsonObject().get("gpa").getAsString());
            credits.setText("Credits:" + result.get("credits").getAsString());
            String logged = !result.has(getString(R.string.ns_stat))? "0" : result.get(getString(R.string.ns_stat)).getAsJsonObject().get("active").getAsString();
            logTime.setText(logged);
            ImageView img = (ImageView) findViewById(R.id.profileImg_year);
            System.out.println(getIntent().getExtras().getString("picture"));
            String url = getString(R.string.api_photos) + getIntent().getExtras().getString(getString(R.string.prompt_login)) + ".bmp";
            Ion.with(img).load(url);
            TextView email = (TextView)findViewById(R.id.mail_value);
            email.setText(result.get("internal_email").getAsString());
            if (result.get("userinfo").getAsJsonObject().has("telephone")) {
                TextView call = (TextView)findViewById(R.id.phone_number);
                call.setText(result.get("userinfo").getAsJsonObject().get("telephone").getAsJsonObject().get("value").getAsString());
            }

        }
        catch (Exception e) {
            System.out.println("you're gonna have a bad time.");
            e.printStackTrace();
        }

    }

}
