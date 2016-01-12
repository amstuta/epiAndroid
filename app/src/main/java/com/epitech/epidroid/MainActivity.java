package com.epitech.epidroid;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.text.TextWatcher;
import android.view.*;
import android.view.MenuInflater;
import 	android.support.v7.app.ActionBarActivity;
import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends ActionBarActivity {

    private RequestAPI reqHandler = new RequestAPI();
    private ImageRequest imgHandler = new ImageRequest();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EpiContext glob = (EpiContext)getApplication();
        if (glob.token == null) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);

            finish();
        }
        else {
            HashMap<String, String> netOptions = new HashMap<>();
            netOptions.put("requestMethod", "POST");
            netOptions.put("domain", "infos");

            HashMap<String, String> args = new HashMap<>();
            args.put("token", glob.token);

            reqHandler.execute(this, netOptions, args);
        }


        // On récupère toutes les vues dont on a besoin
        /*envoyer = (Button)findViewById(R.id.calcul);

        raz = (Button)findViewById(R.id.raz);

        taille = (EditText)findViewById(R.id.taille);
        poids = (EditText)findViewById(R.id.poids);

        mega = (CheckBox)findViewById(R.id.mega);

        group = (RadioGroup)findViewById(R.id.group);

        result = (TextView)findViewById(R.id.result);

        // On attribue un listener adapté aux vues qui en ont besoin
        envoyer.setOnClickListener(envoyerListener);
        raz.setOnClickListener(razListener);
        taille.addTextChangedListener(textWatcher);
        poids.addTextChangedListener(textWatcher);

        // Solution avec des onKey
        //taille.setOnKeyListener(modificationListener);
        //poids.setOnKeyListener(modificationListener);
        mega.setOnClickListener(checkedListener);*/
    }

    public void requestCallback(JSONObject result) {
        try {
            JSONObject infos = result.getJSONObject("infos");
            String picture = infos.getString("picture");

            imgHandler.execute(this, picture);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void imageCallback(Bitmap image) {
        ImageView img = (ImageView)findViewById(R.id.profileImg);
        img.setImageBitmap(image);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_refresh:
                Toast.makeText(this, "Refresh selected", Toast.LENGTH_SHORT).show();
                break;
            // action with ID action_settings was selected
            case R.id.action_settings:
                EpiContext context = (EpiContext)getApplication();

                if (context.token == null) {
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(i);
                }
                else {
                    Intent i = new Intent(getApplicationContext(), DisconnectActivity.class);
                    startActivity(i);
                }
                break;
            default:
                break;
        }

        return true;
    }

/*    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            result.setText(defaut);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    // Uniquement pour le bouton "envoyer"
    private OnClickListener envoyerListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!mega.isChecked()) {
                // Si la megafonction n'est pas activée
                // On récupère la taille
                String t = taille.getText().toString();
                // On récupère le poids
                String p = poids.getText().toString();

                if (p.equals("")) {
                    return;
                }
                float tValue = Float.valueOf(t);

                // Puis on vérifie que la taille est cohérente
                if(tValue == 0)
                    Toast.makeText(MainActivity.this, "Hého, tu es un Minipouce ou quoi ?", Toast.LENGTH_SHORT).show();
                else {
                    float pValue = Float.valueOf(p);
                    // Si l'utilisateur a indiqué que la taille était en centimètres
                    // On vérifie que la Checkbox sélectionnée est la deuxième à l'aide de son identifiant
                    if(group.getCheckedRadioButtonId() == R.id.radio2)
                        tValue = tValue / 100;

                    tValue = (float)Math.pow(tValue, 2);
                    float imc = pValue / tValue;
                    result.setText("Votre IMC est " + String.valueOf(imc));
                }
            } else
                result.setText(megaString);
        }
    };

    // Listener du bouton de remise à zéro
    private OnClickListener razListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            poids.getText().clear();
            taille.getText().clear();
            result.setText(defaut);
        }
    };

    // Listener du bouton de la megafonction.
    private OnClickListener checkedListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // On remet le texte par défaut si c'était le texte de la megafonction qui était écrit
            if(!((CheckBox)v).isChecked() && result.getText().equals(megaString))
                result.setText(defaut);
        }
    };*/

}
