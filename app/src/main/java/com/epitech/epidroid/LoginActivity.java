package com.epitech.epidroid;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.support.v7.app.ActionBarActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

public class LoginActivity extends ActionBarActivity {

    //private UserLoginTask mAuthTask = null;
    private RequestAPI mAuthTask = null;

    private EditText mLoginView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private String token;
    private String projectsList = null;
    private ArrayList<HashMap<String, String>> projects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLoginView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mLoginView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mLoginView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mLoginView.setError(getString(R.string.error_field_required));
            focusView = mLoginView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mLoginView.setError(getString(R.string.error_invalid_email));
            focusView = mLoginView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.

            // TODO: Ici lacher un toast
            focusView.requestFocus();
        } else {
            showProgress(true);


            HashMap<String, String> netOptions = new HashMap<>();
            netOptions.put("requestMethod", "POST");
            netOptions.put("domain", "login");

            HashMap<String, String> args = new HashMap<>();
            args.put("login", email);
            args.put("password", password);

            //mAuthTask = new UserLoginTask(email, password);
            mAuthTask = new RequestAPI();
            mAuthTask.execute(this, netOptions, args);
        }
    }

    public void requestCallback(JSONObject result) {
        if (result == null) {
            return;
        }

        TextView projs = (TextView)findViewById(R.id.projects);
        try {
            token = result.getString("token");
            projs.setText("Connected succesfully : " + token);
            finish();
        }
        catch (JSONException e) {
            projs.setText("Connection failed.");
        }
        finally {
            mAuthTask = null;
            showProgress(false);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("_");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 7;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
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
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}



    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */

    /*
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mLogin;
        private final String mPassword;

        UserLoginTask(String login, String password) {
            mLogin = login;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            System.setProperty("http.keepAlive", "false");
            OutputStreamWriter writer;
            BufferedReader reader;

            try {

                String donnees = URLEncoder.encode("login", "UTF-8") + "=" + URLEncoder.encode(mLogin, "UTF-8");
                donnees += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(mPassword, "UTF-8");
                URL url = new URL("http://epitech-api.herokuapp.com/login");
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();

                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.connect();

                writer = new OutputStreamWriter(urlConnection.getOutputStream());
                writer.write(donnees);
                writer.flush();

                int responseCode = urlConnection.getResponseCode();
                if (responseCode >= 400) {
                    System.out.println(responseCode);
                }
                else {
                    reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                    String line;
                    String fResult = "";

                    while ((line = reader.readLine()) != null) {
                        fResult += line;
                    }

                    JSONObject myObject = new JSONObject(fResult);
                    token = myObject.getString("token");

                    System.out.println("Connected succesfully!");
                    getProjects();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        }

        private void getProjects() {
            OutputStreamWriter writer;
            BufferedReader reader;

            try {

                String donnees = URLEncoder.encode("token", "UTF-8") + "=" + URLEncoder.encode(token, "UTF-8");
                URL url = new URL("http://epitech-api.herokuapp.com/infos");
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.connect();

                writer = new OutputStreamWriter(urlConnection.getOutputStream());
                writer.write(donnees);
                writer.flush();

                int responseCode = urlConnection.getResponseCode();
                if (responseCode >= 400) {
                    System.out.println(responseCode);
                }
                else {
                    reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                    String line;
                    String fResult = "";

                    while ((line = reader.readLine()) != null) {
                        fResult += line;
                    }

                    JSONObject myObject = new JSONObject(fResult);
                    JSONObject projs = myObject.getJSONObject("board");
                    JSONArray p = projs.getJSONArray("projets");
                    String projects = "";

                    for (int i=0; i < p.length(); ++i) {
                        JSONObject proj = p.getJSONObject(i);

                        projects += proj.getString("title") + " - ";
                        projects += proj.getString("timeline_end") + "\n";
                    }

                    projectsList = projects;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                //finish();
                // TODO: call une autre activité avec la liste des projets & le token
                TextView projs = (TextView)findViewById(R.id.projects);
                projs.setText(projectsList);
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}
*/

