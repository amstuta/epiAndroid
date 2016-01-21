package com.epitech.epidroid;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.Calendar;


public class ModulesActivity extends AbstractActivity implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {

    private EpiContext              appContext = null;
    private ArrayAdapter<String>    mAdapter;
    private ArrayList<String>       mArrayList = new ArrayList<String>();
    private ArrayAdapter<String>    vAdapter;
    private ArrayList<String>       vArrayList = new ArrayList<String>();
    private CharSequence            mTitle;
    private int                     selectedSemester = 1;
    private ArrayList<String>       activitiesArrayList = new ArrayList<String>();
    private ArrayAdapter<String>    activitiesAdapter;

    /* List of not registered modules */
    private JsonArray               modsNotRegistered = new JsonArray();
    private ArrayList<String>       modsNotRegList = new ArrayList<String>();
    private ArrayAdapter<String>    modsNotRegAdapter;

    /* Popup */
    private View                    mLoginFormView;
    private View                    mProgressView;
    private PopupWindow             pWIndow;
    private View                    pView;

    /* Spinner registered / not */
    private ArrayList<String>       spinnerChoices = new ArrayList<String>();
    private ArrayAdapter<String>    spinnerAdapter;


    /**
     * Loads the UI components and makes the request to get the list of modules.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modules);

        appContext = (EpiContext)getApplication();
        vAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, vArrayList);
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mArrayList);
        activitiesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, activitiesArrayList);
        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerChoices);
        modsNotRegAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, modsNotRegList);

        Spinner cSpinner = (Spinner)findViewById(R.id.spinner_registered);
        cSpinner.setAdapter(spinnerAdapter);
        spinnerChoices.add(getString(R.string.filter_all_modules));
        spinnerChoices.add(getString(R.string.filter_not_registered_modules));
        spinnerAdapter.notifyDataSetChanged();
        cSpinner.setOnItemSelectedListener(this);

        ListView mdsToRegister = (ListView)findViewById(R.id.modules_not_registered);
        mdsToRegister.setAdapter(modsNotRegAdapter);
        mdsToRegister.setOnItemClickListener(this);

        NavigationDrawerFragment mNavigationDrawerFragment = (NavigationDrawerFragment)getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout)findViewById(R.id.drawer_layout));

        if (appContext.token == null || appContext.userInfos == null) {
            Toast.makeText(this, getString(R.string.connect_fail), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Ion.with(getApplicationContext())
        .load(getString(R.string.request_method_get), getString(R.string.api_domain) + getString(R.string.domain_modules))
        .setBodyParameter(getString(R.string.token), appContext.token)
        .asJsonObject()
        .setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                modulesCallback(result);
            }
        });

        try {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR) - 1;

            JsonObject infos = appContext.userInfos.get(getString(R.string.domain_infos)).getAsJsonObject();
            String location = infos.get(getString(R.string.location)).getAsString();
            String course = infos.get(getString(R.string.course_code)).getAsString();

            Ion.with(getApplicationContext())
                    .load(getString(R.string.request_method_get), getString(R.string.api_domain) + getString(R.string.domain_allmodules))
                    .setBodyParameter(getString(R.string.token), appContext.token)
                    .setBodyParameter(getString(R.string.scolarYear), "" + year)
                    .setBodyParameter(getString(R.string.location), location)
                    .setBodyParameter(getString(R.string.course), course)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            try {
                                JsonArray items = result.get(getString(R.string.items)).getAsJsonArray();

                                for (int i=0; i < items.size(); ++i) {
                                    JsonObject tmp = items.get(i).getAsJsonObject();
                                    String status = tmp.get(getString(R.string.status)).getAsString();

                                    if (status.equals(getString(R.string.notregistered))) {
                                        modsNotRegistered.add(tmp);
                                        modsNotRegList.add(tmp.get(getString(R.string.title)).getAsString());
                                    }
                                }
                                modsNotRegAdapter.notifyDataSetChanged();
                            }
                            catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Callback for the modules request.
     * Tries to get the Modules list in "result" {@link JsonObject},
     * then displays theses modules and set the click listener on the list.
     * @param  result the result of the request in JSON format
     */
    public void modulesCallback(JsonObject result) {

        if (result == null) {
            Toast.makeText(this, getString(R.string.connect_fail), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        try {
            JsonArray mods = result.getAsJsonArray(getString(R.string.domain_modules));

            Spinner msgs = (Spinner)findViewById(R.id.modules);
            ListView lst = (ListView)findViewById(R.id.modules_title);
            final ArrayList<JsonObject>[] modules = getSortedModules(mods);

            msgs.setAdapter(vAdapter);
            lst.setAdapter(mAdapter);

            msgs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView parent, View view, int position, long id) {

                    Toast.makeText(getBaseContext(), "Semester " + ++position + " selected", Toast.LENGTH_SHORT).show();
                    selectedSemester = position;

                    if (position < modules.length && modules[position] != null) {
                        ArrayList<JsonObject> tmp = modules[position];

                        mArrayList.clear();
                        mAdapter.notifyDataSetChanged();

                        try {
                            for (int i = 0; i < tmp.size(); ++i) {
                                String modName = tmp.get(i).get(getString(R.string.title)).getAsString();

                                mArrayList.add(modName);
                                mAdapter.notifyDataSetChanged();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView view) {
                }
            });

            for (int i = 1; i < modules.length && modules[i] != null; ++i) {
                vArrayList.add("" + i);
                vAdapter.notifyDataSetChanged();
            }


            lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    ListView lv = (ListView) findViewById(R.id.modules_title);
                    String moduleName = (String) (lv.getItemAtPosition(position));
                    ArrayList<JsonObject> sem = modules[selectedSemester];
                    JsonObject selectedModule = sem.get(position).getAsJsonObject();

                    try {
                        String scolarYear = selectedModule.get(getString(R.string.scolarYear)).getAsString();
                        String codeModule = selectedModule.get(getString(R.string.codeModule)).getAsString();
                        String codeInstance = selectedModule.get(getString(R.string.codeInstance)).getAsString();

                        Toast.makeText(getApplicationContext(), moduleName, Toast.LENGTH_SHORT).show();
                        executeRequestModule(scolarYear, codeModule, codeInstance);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.connect_fail), Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    /**
     * Makes the request to get informations on the clicked module,
     * and displays a loading bar when executing request.
     * @param scolarYear The scholar year of the selected module
     * @param codeModule Code of the selected module
     * @param codeInstance Code of the instance of the selected module
     */
    protected void executeRequestModule(String scolarYear, String codeModule, String codeInstance) {
        LayoutInflater layoutInflater = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.popup_module, null);
        final PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        pWIndow = popupWindow;
        pView = popupView;

        Button btnDismiss = (Button) popupView.findViewById(R.id.dismiss);
        btnDismiss.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        pWIndow.showAtLocation(pView, Gravity.RIGHT, Gravity.CENTER, Gravity.CENTER);
        mLoginFormView = pView.findViewById(R.id.login_form);
        mProgressView = pView.findViewById(R.id.login_progress);
        showProgress(true);


        Ion.with(getApplicationContext())
        .load(getString(R.string.request_method_get), getString(R.string.api_domain) + getString(R.string.domain_module))
        .setBodyParameter(getString(R.string.token), appContext.token)
        .setBodyParameter(getString(R.string.scolarYear), scolarYear)
        .setBodyParameter(getString(R.string.codeModule), codeModule)
        .setBodyParameter(getString(R.string.codeInstance), codeInstance)
        .asJsonObject()
        .setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                requestCallback(result);
            }
        });
    }


    /**
     * Callback for the module informations request.
     * Gets the title, grade and projects for the selected module.
     * If possible, it displays the projects marks too.
     * @param  result the result of the request in JSON format
     */
    public void requestCallback(final JsonObject result) {
        if (result == null) {
            Toast.makeText(this, getString(R.string.connect_fail), Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String title = result.get(getString(R.string.title)).getAsString();
            String grade = result.get(getString(R.string.grade)).getAsString();

            TextView moduleName = (TextView)pView.findViewById(R.id.module_name);
            TextView moduleGrade = (TextView)pView.findViewById(R.id.module_grade);
            moduleName.setText(title);
            moduleGrade.setText(getString(R.string.dispGrade) + grade);

            Button unReg = (Button)pView.findViewById(R.id.button_unregister);
            unReg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    unregisterToModule(result);
                }
            });

            ListView moduleInfo = (ListView)pView.findViewById(R.id.module_infos);
            JsonArray activities = result.getAsJsonArray(getString(R.string.activites));

            activitiesArrayList.clear();
            moduleInfo.setAdapter(activitiesAdapter);

            for (int i=0; i < activities.size(); ++i) {
                JsonObject tmp = activities.get(i).getAsJsonObject();
                Boolean isProject = tmp.get(getString(R.string.isProject)).getAsBoolean();

                if (isProject) {
                    String infos = tmp.get(getString(R.string.title)).getAsString();

                    if (!tmp.get(getString(R.string.mark)).isJsonNull())
                        infos += "          " + tmp.get(getString(R.string.mark)).getAsString();

                    activitiesArrayList.add(infos);
                }
            }
            activitiesAdapter.notifyDataSetChanged();
            showProgress(false);
        }
        catch (Exception e) {
            showProgress(false);
            e.printStackTrace();
        }
    }


    private ArrayList<JsonObject>[] getSortedModules(JsonArray mods) {
        if (mods == null)
            return null;

        ArrayList<JsonObject>[] res = new ArrayList[11];

        for (int i = 0; i < mods.size(); ++i) {

            try {
                JsonObject tmp = mods.get(i).getAsJsonObject();
                int sem = tmp.get(getString(R.string.semester)).getAsInt();

                if (res[sem] == null) {
                    res[sem] = new ArrayList<JsonObject>();
                }
                res[sem].add(tmp);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return res;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner msgs = (Spinner)findViewById(R.id.modules);
        ListView mds = (ListView)findViewById(R.id.modules_title);
        ListView mdsToRegister = (ListView)findViewById(R.id.modules_not_registered);
        TextView title = (TextView)findViewById(R.id.module_title);

        if (position == 0) {

            msgs.setVisibility(View.VISIBLE);
            mds.setVisibility(View.VISIBLE);
            title.setVisibility(View.VISIBLE);
            mdsToRegister.setVisibility(View.GONE);
        }
        else {
            msgs.setVisibility(View.GONE);
            mds.setVisibility(View.GONE);
            title.setVisibility(View.GONE);
            mdsToRegister.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            JsonObject selected = modsNotRegistered.get(position).getAsJsonObject();
            String scolarYear = selected.get(getString(R.string.scolarYear)).getAsString();
            String codeModule = selected.get(getString(R.string.code)).getAsString();
            String codeInstance = selected.get(getString(R.string.codeInstance)).getAsString();

            Ion.with(getApplicationContext())
                    .load(getString(R.string.request_method_get), getString(R.string.api_domain) + getString(R.string.domain_module))
                    .setBodyParameter(getString(R.string.token), appContext.token)
                    .setBodyParameter(getString(R.string.scolarYear), scolarYear)
                    .setBodyParameter(getString(R.string.codeModule), codeModule)
                    .setBodyParameter(getString(R.string.codeInstance), codeInstance)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            displayPopupRegister(result);
                        }
                    });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void displayPopupRegister(final JsonObject result) {
        LayoutInflater layoutInflater = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.popup_module_registration, null);
        final PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        pWIndow = popupWindow;
        pView = popupView;

        Button btnDismiss = (Button)popupView.findViewById(R.id.dismiss);
        btnDismiss.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        Button register = (Button)popupView.findViewById(R.id.module_register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerToModule(result);
            }
        });

        try {
            String title = result.get(getString(R.string.title)).getAsString();
            String desc = result.get(getString(R.string.description)).getAsString();
            if (result.has(getString(R.string.end_register)) && !result.get(getString(R.string.end_register)).isJsonNull()) {
                String end = result.get(getString(R.string.end_register)).getAsString();
                TextView e = (TextView) popupView.findViewById(R.id.module_end_register);
                e.setText(getString(R.string.end_registration) + end);
            }

            TextView t = (TextView)popupView.findViewById(R.id.module_title);
            TextView d = (TextView)popupView.findViewById(R.id.module_description);

            t.setText(title);
            d.setText(desc);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        pWIndow.showAtLocation(pView, Gravity.RIGHT, Gravity.CENTER, Gravity.CENTER);
    }


    public void registerToModule(final JsonObject module) {
        try {
            String scolarYear = module.get(getString(R.string.scolarYear)).getAsString();
            String codeModule = module.get(getString(R.string.codeModule)).getAsString();
            String codeInstance = module.get(getString(R.string.codeInstance)).getAsString();

            Ion.with(getApplicationContext())
                    .load(getString(R.string.api_domain) + getString(R.string.domain_module))
                    .setBodyParameter(getString(R.string.token), appContext.token)
                    .setBodyParameter(getString(R.string.scolarYear), scolarYear)
                    .setBodyParameter(getString(R.string.codeModule), codeModule)
                    .setBodyParameter(getString(R.string.codeInstance), codeInstance)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (result != null && result.has(getString(R.string.domain_login)))
                                Toast.makeText(getApplicationContext(), getString(R.string.you_registered), Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(getApplicationContext(), getString(R.string.register_failed), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void unregisterToModule(final JsonObject module) {
        try {
            String scolarYear = module.get(getString(R.string.scolarYear)).getAsString();
            String codeModule = module.get(getString(R.string.codeModule)).getAsString();
            String codeInstance = module.get(getString(R.string.codeInstance)).getAsString();

            Ion.with(getApplicationContext())
                    .load(getString(R.string.request_method_delete), getString(R.string.api_domain) + getString(R.string.domain_module))
                    .setBodyParameter(getString(R.string.token), appContext.token)
                    .setBodyParameter(getString(R.string.scolarYear), scolarYear)
                    .setBodyParameter(getString(R.string.codeModule), codeModule)
                    .setBodyParameter(getString(R.string.codeInstance), codeInstance)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            Toast.makeText(getApplicationContext(), getString(R.string.you_unregistered), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}
