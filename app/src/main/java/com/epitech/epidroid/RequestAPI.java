package com.epitech.epidroid;

import android.app.Activity;
import android.os.AsyncTask;

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
        @SuppressWarnings("unchecked")
        HashMap<String,String> netOptions = (HashMap<String, String>)objs[1];
        @SuppressWarnings("unchecked")
        HashMap<String, String> args = (HashMap<String,String>)objs[2];

        System.setProperty("http.keepAlive", "false");
        OutputStreamWriter writer;
        BufferedReader reader;
        String datas = "";

        try {
            methodCb = netOptions.get(((Activity)callback).getResources().getString(R.string.callback));

            for (Map.Entry<String, String> e : args.entrySet()) {
                if (datas.length() != 0)
                    datas += "&";
                datas += URLEncoder.encode(e.getKey(), "UTF-8") + "=" + URLEncoder.encode(e.getValue(), "UTF-8");
            }

            URL url = new URL(((Activity)callback).getResources().getString(R.string.api_domain) + netOptions.get(((Activity)callback).getResources().getString(R.string.domain)) + "?" + datas);
            HttpURLConnection urlCo = (HttpURLConnection)url.openConnection();

            urlCo.setRequestMethod(netOptions.get(((Activity) callback).getResources().getString(R.string.request_method)));
            if (netOptions.get("requestMethod").equals("POST"))
                urlCo.setDoOutput(true);
            urlCo.connect();

            if (netOptions.get("requestMethod").equals("POST")) {
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
                String fResult = "";

                while ((line = reader.readLine()) != null) {
                    fResult += line;
                }
                requestResult = new JSONObject(fResult);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
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
