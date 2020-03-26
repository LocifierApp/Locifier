package com.locifierapp.locifier;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        configureBackButton();
        configureOpenMapsButton();
    }

    private void configureBackButton(){

        Button backButton = (Button) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v){
                finish();
            }
        });
    }

    private void configureOpenMapsButton(){
        Button openMapsActivityButton = (Button) findViewById(R.id.open_maps_activity_button);

        openMapsActivityButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(TestActivity.this, MapsActivity.class));
            }
        });
    }
}
