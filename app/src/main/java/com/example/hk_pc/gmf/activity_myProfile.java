package com.example.hk_pc.gmf;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class activity_myProfile extends AppCompatActivity {

    EditText iptFName, iptLName, iptMM, iptEmail;
    String fnameHolder, lnameHolder, mobileHolder, emailHolder, passwordHolder , nameRegex, emailRegex, mobileRegex, id;
    Button btnSave, btnCancel;
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sessionManager = new SessionManager(getApplicationContext());

        iptFName = findViewById(R.id.input_first_name);
        iptLName = findViewById(R.id.input_last_name);
        iptMM = findViewById(R.id.input_mm);
        iptEmail = findViewById(R.id.input_email);

        btnCancel = findViewById(R.id.btnCancel);
        btnSave = findViewById(R.id.btnSave);

        if (sessionManager.isLoggedIn()) {
            HashMap<String, String> user = sessionManager.getUserDetails();
            id = user.get(SessionManager.KEY_ID);
            String method = "load";
            BackgroundTask backgroundTask = new BackgroundTask(this);
            backgroundTask.execute(method, id);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_policy, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch(id){
            case R.id.privacy_policy:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.freeprivacypolicy.com/privacy/view/3adab5a8e5cf7b80b46f391ae11a3549")));
                break;
        }
        return true;
    }

    public void cancelButton(View view){
        startActivity(new Intent(activity_myProfile.this,activity_home.class));
    }

    public void saveButton(View view) {
        fnameHolder = iptFName.getText().toString().trim();
        lnameHolder = iptLName.getText().toString().trim();
        mobileHolder = iptMM.getText().toString().trim();
        emailHolder = iptEmail.getText().toString().trim();

        if (TextUtils.isEmpty(fnameHolder) || TextUtils.isEmpty(lnameHolder) || TextUtils.isEmpty(emailHolder) || TextUtils.isEmpty(mobileHolder) || TextUtils.isEmpty(passwordHolder)) {
            Toast.makeText(activity_myProfile.this, "Please Fill up the form", Toast.LENGTH_SHORT).show();
        } else {
            nameRegex = "[A-Z][a-zA-Z]*";
            emailRegex = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$";
            mobileRegex = "^[7-9][0-9]{9}$";

            Pattern namePattern = Pattern.compile(nameRegex);
            Pattern emailPattern = Pattern.compile(emailRegex);
            Pattern mobilePattern = Pattern.compile(mobileRegex);

            Matcher fnameMatches = namePattern.matcher(fnameHolder);
            Matcher lnameMatches = namePattern.matcher(lnameHolder);
            Matcher emailMatches = emailPattern.matcher(emailHolder);
            Matcher mobileMatches = mobilePattern.matcher(mobileHolder);

            if (fnameMatches.matches() && lnameMatches.matches() && emailMatches.matches() && mobileMatches.matches()) {
                String method = "save";
                BackgroundTask backgroundTask = new BackgroundTask(this);
                backgroundTask.execute(method, id, fnameHolder, lnameHolder, mobileHolder, emailHolder, passwordHolder);
            }
            else{
                if(!fnameMatches.matches()){
                    Toast.makeText(activity_myProfile.this, "Please enter valid first name", Toast.LENGTH_SHORT).show();
                }
                else if(!lnameMatches.matches()){
                    Toast.makeText(activity_myProfile.this, "Please enter valid last name", Toast.LENGTH_SHORT).show();
                }
                else if(!emailMatches.matches()){
                    Toast.makeText(activity_myProfile.this, "Please enter valid email",Toast.LENGTH_SHORT).show();
                }
                else if(!mobileMatches.matches()){
                    Toast.makeText(activity_myProfile.this,"Please enter valid mobile number", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public class BackgroundTask extends AsyncTask<String, Void, String> {

        String fName, lName, mobile, email1, pass, method;
        Context ctx;
        BackgroundTask(Context ctx){
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute( ) {

        }

        @Override
        protected String doInBackground(String... params) {
            String myProfile_url1 = "http://sqli6.000webhostapp.com/myProfile1.php";
            String myProfile_url2 = "http://sqli6.000webhostapp.com/myProfile2.php";
            method = params[0];
            if(method.equals("load")){
                String id = params[1];
                try {
                    URL url = new URL(myProfile_url1);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream ops = httpURLConnection.getOutputStream();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ops));
                    String data = URLEncoder.encode("Id","UTF-8")+"="+URLEncoder.encode(id,"UTF-8");
                    bw.write(data);
                    bw.flush();
                    bw.close();
                    ops.close();

                    InputStream ips = httpURLConnection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(ips,"iso-8859-1"));
                    StringBuilder result= new StringBuilder();
                    String line;
                    while((line=br.readLine())!=null){
                        result.append(line+"\n");
                    }
                    br.close();
                    ips.close();
                    httpURLConnection.disconnect();

                    String json_string = result.toString().trim();
                    JSONObject jsonObject = new JSONObject(json_string);
                    JSONArray jsonArray = jsonObject.getJSONArray("server_response");
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jo = jsonArray.getJSONObject(i);
                        fName = jo.getString("First_Name");
                        lName = jo.getString("Last_Name");
                        mobile = jo.getString("Mobile_Num");
                        email1 = jo.getString("Email");
                    }

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
            else if(method.equals("save")){
                String id = params[1];
                String fname = params[2];
                String lname = params[3];
                String mobile = params[4];
                String email = params[5];

                try {
                    URL url = new URL(myProfile_url2);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream ops = httpURLConnection.getOutputStream();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
                    String data =URLEncoder.encode("Id","UTF-8")+"="+URLEncoder.encode(id,"UTF-8")+"&"+
                            URLEncoder.encode("fname","UTF-8")+"="+URLEncoder.encode(fname,"UTF-8")+"&"
                            +URLEncoder.encode("lname","UTF-8")+"="+URLEncoder.encode(lname,"UTF-8")+"&"
                            +URLEncoder.encode("mobile", "UTF-8")+"="+URLEncoder.encode(mobile,"UTF-8")+"&"+
                            URLEncoder.encode("email", "UTF-8")+"="+URLEncoder.encode(email,"UTF-8");
                    bw.write(data);
                    bw.flush();
                    bw.close();
                    ops.close();

                    InputStream ips = httpURLConnection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(ips,"iso-8859-1"));
                    StringBuilder result= new StringBuilder();
                    String line;
                    while((line=br.readLine())!=null){
                        result.append(line);
                    }
                    br.close();
                    ips.close();
                    httpURLConnection.disconnect();
                    return result.toString();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            if(method.equals("load")) {
                iptFName.setText(fName);
                iptLName.setText(lName);
                iptMM.setText(mobile);
                iptEmail.setText(email1);
            }
            else if(method.equals("save")) {
                if (result.trim().equalsIgnoreCase("Saved Successfully")) {
                    sessionManager.createLoginSession(id,fnameHolder,emailHolder);
                    Toast.makeText(activity_myProfile.this, result.trim(), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(activity_myProfile.this, activity_home.class));
                } else {
                    Toast.makeText(activity_myProfile.this, result.trim(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
