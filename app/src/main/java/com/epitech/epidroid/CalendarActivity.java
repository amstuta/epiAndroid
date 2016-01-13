package com.epitech.epidroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CalendarView.OnDateChangeListener;

import org.json.JSONObject;

import java.util.HashMap;

public class CalendarActivity extends ActionBarActivity {

    private EpiContext appContext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        appContext = (EpiContext)getApplication();
        final CalendarView calendar = (CalendarView)findViewById(R.id.calendar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            calendar.setShowWeekNumber(false);
            calendar.setFirstDayOfWeek(2);

            calendar.setOnDateChangeListener(new OnDateChangeListener() {

                @Override
                public void onSelectedDayChange(CalendarView view, int year, int month, int day) {
                    Toast.makeText(getApplicationContext(), day + "/" + month + "/" + year, Toast.LENGTH_LONG).show();

                    String date = "" + year + "-" + month + "-" + day;
                    RequestAPI reqHandler = new RequestAPI();

                    HashMap<String, String> netOpts = new HashMap<String, String>();
                    netOpts.put(getString(R.string.domain), getString(R.string.domain_planning));
                    netOpts.put(getString(R.string.request_method), getString(R.string.request_method_get));
                    netOpts.put(getString(R.string.callback), getString(R.string.callback_info));

                    HashMap<String, String> args = new HashMap<String, String>();
                    args.put(getString(R.string.token), appContext.token);
                    args.put(getString(R.string.planning_start), date);
                    args.put(getString(R.string.planning_end), date);

                    reqHandler.execute(this, netOpts, args); // Problème
                }
            });
        }

    }

    public void requestCallback(JSONObject result) {

        if (result == null) {
            Toast.makeText(this, getString(R.string.connect_fail), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        LayoutInflater layoutInflater
                = (LayoutInflater)getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.popup_calendar, null);
        final PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView tok = (TextView)findViewById(R.id.token);
        tok.setText(appContext.token);

        Button btnDismiss = (Button)popupView.findViewById(R.id.dismiss);
        btnDismiss.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_home:
                finish();
                break;

            case R.id.action_settings:
                if (appContext.token == null) {
                    Intent in = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(in);
                }
                else {
                    Intent in = new Intent(getApplicationContext(), DisconnectActivity.class);
                    startActivity(in);
                }
                break;

            case R.id.action_modules:
                Intent i = new Intent(getApplicationContext(), ModulesActivity.class);
                startActivity(i);
                break;

            default:
                break;
        }

        return true;
    }

}
