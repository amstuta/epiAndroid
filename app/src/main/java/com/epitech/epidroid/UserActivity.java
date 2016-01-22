package com.epitech.epidroid;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.w3c.dom.Text;

public class UserActivity extends AbstractActivity implements View.OnClickListener{


    private NavigationDrawerFragment    mNavigationDrawerFragment;
    private CharSequence                mTitle;
    private EpiContext                  appContext;
    private JsonObject                  userInfos = null;


    /**
     * Automatically called whenever this activity is started.
     * will pre-set the fields and retrieves the selected user's infos and picture.
     * @param savedInstanceState bundle sent from previous activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        appContext = (EpiContext)getApplication();

        mNavigationDrawerFragment = (NavigationDrawerFragment)getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

        ImageView addImg = (ImageView)findViewById(R.id.action_contact);
        addImg.setOnClickListener(this);


        final TextView mail = (TextView)findViewById(R.id.mail_value);
        ImageView mailImg = (ImageView)findViewById(R.id.action_mail);
        mailImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{mail.getText().toString()});
                startActivity(Intent.createChooser(intent, "Send mail"));
            }
        });

        final TextView call = (TextView)findViewById(R.id.phone_number);
        ImageView callImg = (ImageView)findViewById(R.id.action_call);
        callImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + call.getText().toString()));
                startActivity(intent);
            }
        });
        ImageView msgImg = (ImageView)findViewById(R.id.action_message);
        msgImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.setData(Uri.parse("sms:" + call.getText().toString()));
                startActivity(smsIntent);
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
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void userCallback(JsonObject result) {
        try {
            System.out.println(result);
            TextView name = (TextView)findViewById(R.id.title_year);
            TextView gpa = (TextView) findViewById(R.id.gpa_year);
            TextView credits = (TextView)findViewById(R.id.credits_year);
            TextView logTime = (TextView)findViewById(R.id.logTime_year);
            TextView gpaM = (TextView)findViewById(R.id.gpa_master);

            name.setText(result.get("title").getAsString());
            gpa.setText("GPA Bachelor: " + result.get(getString(R.string.gpa)).getAsJsonArray().get(0).getAsJsonObject().get(getString(R.string.gpa)).getAsString());
            if (result.get(getString(R.string.gpa)).getAsJsonArray().size() > 1) {
                gpaM.setText("GPA Master: " + result.get(getString(R.string.gpa)).getAsJsonArray().get(1).getAsJsonObject().get(getString(R.string.gpa)).getAsString());
                gpaM.setVisibility(View.VISIBLE);
            }
            credits.setText("Credits:" + result.get("credits").getAsString());

            String logged = !result.has(getString(R.string.ns_stat))? "0" : result.get(getString(R.string.ns_stat)).getAsJsonObject().get("active").getAsString();
            logTime.setText(getString(R.string.active_time) + logged);

            ImageView img = (ImageView) findViewById(R.id.profileImg_year);
            System.out.println(getIntent().getExtras().getString("picture"));
            String url = getString(R.string.api_photos) + getIntent().getExtras().getString(getString(R.string.prompt_login)) + ".bmp";
            Ion.with(img).load(url);


            TextView email = (TextView)findViewById(R.id.mail_value);
            email.setText(result.get("internal_email").getAsString());
            TextView call = (TextView)findViewById(R.id.phone_number);
            if (result.get("userinfo").getAsJsonObject().has("telephone")) {
                ImageView callImg = (ImageView)findViewById(R.id.action_call);
                ImageView smsImg = (ImageView)findViewById(R.id.action_message);

                callImg.setVisibility(View.VISIBLE);
                smsImg.setVisibility(View.VISIBLE);
                call.setText(result.get("userinfo").getAsJsonObject().get("telephone").getAsJsonObject().get("value").getAsString());
            }
            else{
                call.setClickable(false);
                call.setTextColor(Color.BLACK);
            }


        }
        catch (Exception e) {
            System.out.println("you're gonna have a bad time.");
            e.printStackTrace();
        }

    }

    /**
     * Listener called whenever the "add contact" button is pressed.
     * will open a contact-adding window with the name, the email and phone number pre-filled.
     * @param v parent view.
     */
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
        intent.putExtra(ContactsContract.Intents.Insert.EMAIL, ((TextView)findViewById(R.id.mail_value)).getText())
                .putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK);
        if (!(((TextView) findViewById(R.id.phone_number)).getText().equals("N/A"))) {
            intent.putExtra(ContactsContract.Intents.Insert.PHONE, ((TextView) findViewById(R.id.phone_number)).getText())
                    .putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        }
                intent.putExtra(ContactsContract.Intents.Insert.NAME, ((TextView) findViewById(R.id.title_year)).getText());
        startActivity(intent);
    }
}
