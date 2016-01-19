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
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

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
        Button reg = (Button)findViewById(R.id.register_button);
        Button ret = (Button)findViewById(R.id.return_button);
        Button tok = (Button)findViewById(R.id.token_button);

        try {
            title.setText(activity.get(getString(R.string.activity_title)).getAsString());
            module.setText(activity.get(getString(R.string.title_module)).getAsString());

            String regist = activity.get(getString(R.string.event_registered)).getAsString();
            if (!regist.equals("false")) {
                registered = true;
                reg.setText(getString(R.string.unregister));
            }
            else
                reg.setText(getString(R.string.register));
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
        HashMap<String, String> netOpts = new HashMap<String, String>();
        HashMap<String, String> args = new HashMap<String, String>();
        RequestAPI reqHandler = new RequestAPI();

        netOpts.put(getString(R.string.domain), getString(R.string.domain_event));
        if (registered)
            netOpts.put(getString(R.string.request_method), getString(R.string.request_method_get));
        else
            netOpts.put(getString(R.string.request_method), getString(R.string.request_method_delete));
        netOpts.put(getString(R.string.callback), getString(R.string.callback_info));

        try {
            args.put(getString(R.string.token), appContext.token);
            args.put(getString(R.string.scolarYear), activity.get(getString(R.string.scolarYear)).getAsString());
            args.put(getString(R.string.codeModule), activity.get(getString(R.string.codeModule)).getAsString());
            args.put(getString(R.string.codeInstance), activity.get(getString(R.string.codeInstance)).getAsString());
            args.put(getString(R.string.codeActivity), activity.get(getString(R.string.codeActivity)).getAsString());
            args.put(getString(R.string.codeEvent), activity.get(getString(R.string.codeEvent)).getAsString());
        }
        catch (Exception e) {
            e.printStackTrace();
            return;
        }

        Toast.makeText(getApplicationContext(), getString(R.string.registered_success), Toast.LENGTH_SHORT).show();
        reqHandler.execute(this, netOpts, args);
    }


    private void validateToken(String token) {
        HashMap<String, String> netOpts = new HashMap<String, String>();
        HashMap<String, String> args = new HashMap<String, String>();
        RequestAPI reqHandler = new RequestAPI();

        netOpts.put(getString(R.string.domain), getString(R.string.domain_token));
        netOpts.put(getString(R.string.request_method), getString(R.string.request_method_post));
        netOpts.put(getString(R.string.callback), getString(R.string.callback_info));

        try {
            args.put(getString(R.string.token), appContext.token);
            args.put(getString(R.string.scolarYear), activity.get(getString(R.string.scolarYear)).getAsString());
            args.put(getString(R.string.codeModule), activity.get(getString(R.string.codeModule)).getAsString());
            args.put(getString(R.string.codeInstance), activity.get(getString(R.string.codeInstance)).getAsString());
            args.put(getString(R.string.codeActivity), activity.get(getString(R.string.codeActivity)).getAsString());
            args.put(getString(R.string.codeEvent), activity.get(getString(R.string.codeEvent)).getAsString());
            args.put(getString(R.string.tokenCode), token);
        }
        catch (Exception e) {
            e.printStackTrace();
            return;
        }
        Toast.makeText(getApplicationContext(), getString(R.string.token_validated), Toast.LENGTH_SHORT).show();

        reqHandler.execute(this, netOpts, args);
    }


    public void requestCallback(JSONObject result) {}
}