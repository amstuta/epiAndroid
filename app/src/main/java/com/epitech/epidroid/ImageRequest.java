package com.epitech.epidroid;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.InputStream;

public class ImageRequest extends AsyncTask<Object, Void, Bitmap>{

    private Activity caller = null;

    @Override
    protected Bitmap doInBackground(Object... urls)
    {
        caller = (Activity)urls[0];
        String urlDisplay = caller.getResources().getString(R.string.api_photos) + urls[1];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urlDisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mIcon11;
    }

    @Override
    protected void onPostExecute(final Bitmap image) {
        try {
            caller.getClass().getMethod("imageCallback", Bitmap.class).invoke(caller, image);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
