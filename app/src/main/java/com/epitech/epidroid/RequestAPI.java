package com.epitech.epidroid;

import android.app.Activity;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


public class RequestAPI extends AsyncTask<Object, Void, Boolean> {

    private JSONObject  requestResult = null;
    private Object      callback = null;
    private String      methodCb = null;

    @Override
    protected Boolean doInBackground(Object... objs) {

        callback = objs[0];
        Activity act = (Activity)callback;
        @SuppressWarnings("unchecked")
        HashMap<String,String> netOptions = (HashMap<String, String>)objs[1];
        @SuppressWarnings("unchecked")
        HashMap<String, String> args = (HashMap<String,String>)objs[2];

        System.setProperty("http.keepAlive", "false");
        OutputStreamWriter writer;
        BufferedReader reader;
        String datas = "";
        String fResult = "";

        try {
            methodCb = netOptions.get(act.getResources().getString(R.string.callback));

            for (Map.Entry<String, String> e : args.entrySet()) {
                if (datas.length() != 0)
                    datas += "&";
                datas += URLEncoder.encode(e.getKey(), "UTF-8") + "=" + URLEncoder.encode(e.getValue(), "UTF-8");
            }

            URL url = new URL(act.getResources().getString(R.string.api_domain) + netOptions.get(act.getResources().getString(R.string.domain)) + "?" + datas);
            HttpURLConnection urlCo = (HttpURLConnection)url.openConnection();

            urlCo.setRequestMethod(netOptions.get(act.getResources().getString(R.string.request_method)));
            if (netOptions.get(act.getResources().getString(R.string.request_method)).equals(act.getResources().getString(R.string.request_method_post)))
                urlCo.setDoOutput(true);
            urlCo.connect();

            if (netOptions.get(act.getResources().getString(R.string.request_method)).equals(act.getResources().getString(R.string.request_method_post))) {
                writer = new OutputStreamWriter(urlCo.getOutputStream());
                writer.write(datas);
                writer.flush();
            }

            int responseCode = urlCo.getResponseCode();
            if (responseCode >= 400) {
                System.out.println(responseCode);
                return false;
            }
            else {
                reader = new BufferedReader(new InputStreamReader(urlCo.getInputStream()));

                String line;

                while ((line = reader.readLine()) != null) {
                    fResult += line;
                }

                if (fResult.length() == 0)
                    return true;

                requestResult = new JSONObject(fResult);
            }
        }
        catch (Exception e) {

            try {
                JSONArray res = new JSONArray(fResult);
                JSONObject finale = new JSONObject();
                finale.put(((Activity)callback).getString(R.string.response), res);

                requestResult = finale;
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return true;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        if (callback != null && methodCb != null) {
            try {
                callback.getClass().getMethod(methodCb, JSONObject.class).invoke(callback, requestResult);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
