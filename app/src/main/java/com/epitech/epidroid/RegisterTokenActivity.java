package com.epitech.epidroid;

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

import java.util.HashMap;

public class RegisterTokenActivity extends ActionBarActivity {


    private EpiContext  appContext;
    private JsonObject  activity;
    private Boolean     registered = false;


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

        try {
            title.setText(activity.get(getString(R.string.activity_title)).getAsString());
            module.setText(activity.get(getString(R.string.title_module)).getAsString());

            String regist = activity.get(getString(R.string.event_registered)).getAsString();
            if (!regist.equals("false")) {
                registered = true;
                reg.setBackgroundResource(R.drawable.ic_action_min);
            }
            else
                reg.setBackgroundResource(R.drawable.ic_action_register);
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