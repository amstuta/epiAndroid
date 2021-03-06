package com.epitech.epidroid;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class RegisterTokenActivity extends ActionBarActivity {


    private EpiContext  appContext;
    private JsonObject  activity;
    private Boolean     registered = false;


    /**
     * Automatically called whenever this activity is started.
     * will pre-set the fields and retrieves the selected user's infos and picture.
     * @param savedInstanceState bundle sent from previous activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_token);

        appContext = (EpiContext)getApplication();

        if (appContext.activity == null) {
            Toast.makeText(getApplicationContext(), getString(R.string.connect_fail), Toast.LENGTH_SHORT).show();
            finish();
        }
        activity = appContext.activity;

        TextView title = (TextView)findViewById(R.id.activity_title);
        TextView module = (TextView)findViewById(R.id.activity_module);
        ImageView reg = (ImageView)findViewById(R.id.register_button);
        ImageView ret = (ImageView)findViewById(R.id.return_button);
        ImageView tok = (ImageView)findViewById(R.id.token_button);
        ImageView add = (ImageView)findViewById(R.id.add_to_calendar_button);
        TextView time = (TextView)findViewById(R.id.activity_time);
        TextView date = (TextView)findViewById(R.id.activity_date);

        try {
            // Activity infos
            final String acti = activity.get(getString(R.string.activity_title)).getAsString();
            title.setText(acti);
            module.setText(activity.get(getString(R.string.title_module)).getAsString());

            // Display date
            final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            final Date dat = format.parse(activity.get(getString(R.string.planning_start)).getAsString());
            final Date tim = format.parse(activity.get(getString(R.string.planning_end)).getAsString());
            date.setText(new SimpleDateFormat("dd MMM yyyy").format(dat));
            time.setText(new SimpleDateFormat("HH:mm").format(dat) + " - " + new SimpleDateFormat("HH:mm").format(tim));

            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Calendar cal = Calendar.getInstance();
                    Intent intent = new Intent(Intent.ACTION_EDIT);
                    intent.setType("vnd.android.cursor.item/event");
                    intent.putExtra("beginTime", dat.getTime());
                    intent.putExtra("allDay", false);
                    //intent.putExtra("rrule", "FREQ=DAILY");
                    intent.putExtra("endTime", tim.getTime());
                    intent.putExtra("title", acti);
                    startActivity(intent);

                }
            });

            // Registration status
            String regist = activity.get(getString(R.string.event_registered)).getAsString();
            if (!regist.equals("false")) {
                registered = true;
                reg.setBackgroundResource(R.drawable.ic_action_min);
            }
            else
                reg.setBackgroundResource(R.drawable.ic_action_register);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
            finish();
        }

        ret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerToActivity();
            }
        });


        tok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = layoutInflater.inflate(R.layout.popup_token, null);
                final PopupWindow popupWindow = new PopupWindow(popupView,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, true);
                Button validate = (Button) popupView.findViewById(R.id.validate_token);
                Button close = (Button) popupView.findViewById(R.id.dismiss);
                final EditText tokenCode = (EditText) popupView.findViewById(R.id.token_code);

                tokenCode.requestFocus();

                validate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String token = tokenCode.getText().toString();

                        if (TextUtils.isEmpty(token) || token.length() != 8) {
                            Toast.makeText(getApplicationContext(), getString(R.string.error_field_required), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        validateToken(token);
                    }
                });

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });

                popupWindow.showAtLocation(popupView, Gravity.CENTER, Gravity.CENTER, Gravity.CENTER);
            }
        });
    }


    private void registerToActivity() {
        String reqMethod;

        if (registered)
            reqMethod = getString(R.string.request_method_get);
        else
            reqMethod = getString(R.string.request_method_delete);

        try {
            Ion.with(getApplicationContext())
                    .load(reqMethod, getString(R.string.api_domain) + getString(R.string.domain_event))
                    .setBodyParameter(getString(R.string.token), appContext.token)
                    .setBodyParameter(getString(R.string.scolarYear), activity.get(getString(R.string.scolarYear)).getAsString())
                    .setBodyParameter(getString(R.string.codeModule), activity.get(getString(R.string.codeModule)).getAsString())
                    .setBodyParameter(getString(R.string.codeInstance), activity.get(getString(R.string.codeInstance)).getAsString())
                    .setBodyParameter(getString(R.string.codeActivity), activity.get(getString(R.string.codeActivity)).getAsString())
                    .setBodyParameter(getString(R.string.codeEvent), activity.get(getString(R.string.codeEvent)).getAsString())
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                        }
                    });
        }
        catch (Exception e) {
            e.printStackTrace();
            return;
        }

        Toast.makeText(getApplicationContext(), getString(R.string.registered_success), Toast.LENGTH_SHORT).show();
    }


    private void validateToken(String token) {
        try {
            Ion.with(getApplicationContext())
                    .load(getString(R.string.api_domain) + getString(R.string.domain_token))
                    .setBodyParameter(getString(R.string.token), appContext.token)
                    .setBodyParameter(getString(R.string.scolarYear), activity.get(getString(R.string.scolarYear)).getAsString())
                    .setBodyParameter(getString(R.string.codeModule), activity.get(getString(R.string.codeModule)).getAsString())
                    .setBodyParameter(getString(R.string.codeInstance), activity.get(getString(R.string.codeInstance)).getAsString())
                    .setBodyParameter(getString(R.string.codeActivity), activity.get(getString(R.string.codeActivity)).getAsString())
                    .setBodyParameter(getString(R.string.codeEvent), activity.get(getString(R.string.codeEvent)).getAsString())
                    .setBodyParameter(getString(R.string.tokenCode), token)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                        }
                    });
        }
        catch (Exception e) {
            e.printStackTrace();
            return;
        }
        Toast.makeText(getApplicationContext(), getString(R.string.token_validated), Toast.LENGTH_SHORT).show();
    }
}