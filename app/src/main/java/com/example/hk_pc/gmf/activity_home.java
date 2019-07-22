package com.example.hk_pc.gmf;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class activity_home extends AppCompatActivity {

    GridLayout mainGrid;
    TextView helloText, welcomeText;
    Button profileButton, loginButton, logoutButton;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        welcomeText = findViewById(R.id.textView);
        sessionManager = new SessionManager(getApplicationContext());

        mainGrid = findViewById(R.id.mainGrid);

        helloText = findViewById(R.id.helloText);

        profileButton= findViewById(R.id.profileButton);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity_home.this,activity_myProfile.class));
            }
        });

        loginButton=findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity_home.this, activity_login.class));
            }
        });

        logoutButton = findViewById(R.id.logoutButton);

        if(sessionManager.isLoggedIn()){
            HashMap<String, String> user = sessionManager.getUserDetails();
            String name = user.get(sessionManager.KEY_NAME);
            welcomeText.setVisibility(View.GONE);
            loginButton.setVisibility(View.GONE);
            logoutButton.setVisibility(View.VISIBLE);
            helloText.setVisibility(View.VISIBLE);
            helloText.setText("Hello, "+name);
        }

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sessionManager.logoutUser();
            }
        });

        setClickEvent(mainGrid);
    }

    private void setClickEvent(GridLayout mainGrid){
        int i;
        for(i=0;i<mainGrid.getChildCount();i++){
            CardView cardView = (CardView)mainGrid.getChildAt(i);
            final int finalI=i;
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(finalI == 0){
                        startActivity(new Intent(activity_home.this,activity_listInfo.class));
                    }
                    else if(finalI == 1){
                        startActivity(new Intent(activity_home.this,activity_map.class));
                    }
                    else if(finalI == 2){
                        if(sessionManager.isLoggedIn()) {
                            startActivity(new Intent(activity_home.this, activity_pillReminder.class));
                        }
                        else{
                            startActivity(new Intent(activity_home.this, activity_login.class));
                        }
                    }
                    else if(finalI == 3){
                        if(sessionManager.isLoggedIn()){
                            startActivity(new Intent(activity_home.this,activity_prescription.class));
                        }
                        else{
                            startActivity(new Intent(activity_home.this,activity_login.class));
                        }
                    }
                }
            });
        }
    }
}
