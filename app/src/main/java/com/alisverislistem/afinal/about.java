package com.alisverislistem.afinal;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;


public class about extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
