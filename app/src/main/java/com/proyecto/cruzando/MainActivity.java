package com.example.myapplication2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity {

    SharedPreferences preferencias;
    SharedPreferences.Editor pref_editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferencias = getSharedPreferences("cw_preferences", Context.MODE_PRIVATE);
        pref_editor = preferencias.edit();

        Resources.Theme tema = getTheme();
        if(preferencias.getString("Tema", "Dark").equals("Dark")){
            tema.applyStyle(R.style.DarkTheme,true);
        }else{
            tema.applyStyle(R.style.LightTheme,true);
        }

        setContentView(R.layout.activity_main);

        Button btn_levelSelector = (Button) findViewById(R.id.btn_levelSelector);
        Button btn_options = (Button) findViewById(R.id.btn_options);
        Button btn_exit = (Button) findViewById(R.id.btn_exit);

        btn_levelSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent levelSelectorScreen = new Intent(getApplicationContext(), LevelSelectorActivity.class);
                startActivity(levelSelectorScreen);
            }
        });

        btn_options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent optScreen = new Intent(getApplicationContext(), OptionsActivity.class);
                startActivity(optScreen);
            }
        });

        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        reload();
    }

    private void reload(){
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }
}