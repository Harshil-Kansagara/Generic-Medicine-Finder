package com.example.hk_pc.gmf;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.JsonArray;
import com.google.gson.annotations.JsonAdapter;


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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class activity_login extends AppCompatActivity {

    EditText email, pass;
    TextView signup, forgetPass;
    String emailHolder, passHolder, emailregex;
    Button loginButton;

    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sessionManager = new SessionManager(getApplicationContext());

        email = (EditText)findViewById(R.id.input_email);
        pass = (EditText)findViewById(R.id.input_password);

        forgetPass = (TextView)findViewById(R.id.link_forgetPassword);
        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity_login.this,activity_forgetPassword.class));
            }
        });

        signup = (TextView)findViewById(R.id.link_signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity_login.this, activity_register.class);
                startActivity(intent);
            }
        });

        loginButton = (Button)findViewById(R.id.btn_login);
    }

    public void loginButton(View view){

        emailHolder = email.getText().toString().trim();
        passHolder = pass.getText().toString().trim();

        if(TextUtils.isEmpty(emailHolder) || TextUtils.isEmpty(passHolder)){
            Toast.makeText(activity_login.this, "Please Fill Detail", Toast.LENGTH_SHORT).show();
        }
        else{
            emailregex = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$";
            Pattern pattern = Pattern.compile(emailregex);
            Matcher emailMatcher= pattern.matcher(emailHolder);
            if(emailMatcher.matches()){
                String method = "login";
                BackgroundTask backgroundTask  = new BackgroundTask(this);
                backgroundTask.execute(method, emailHolder, passHolder);
            }
            else {
                Toast.makeText(activity_login.this, "Invalid email address", Toast.LENGTH_SHORT).show();

            }
        }
    }

    public class BackgroundTask extends AsyncTask<String, Void, String> {

        String name,email1,id;
        ProgressDialog progressDialog = new ProgressDialog(activity_login.this);

        Context ctx;

        BackgroundTask(Context ctx)
        {
            this.ctx=ctx;
        }

        @Override
        protected void onPreExecute( ) {
            progressDialog.setMessage("\tPlease Wait");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String login_url = "http://sqli6.000webhostapp.com/login.php";
            String method  = params[0];
            if(method.equals("login")){
                String email = params[1];
                String password = params[2];

                try {
                    URL url = new URL(login_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream ops = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
                    String data  = URLEncoder.encode("email", "UTF-8")+"="+URLEncoder.encode(email, "UTF-8")+"&"+
                            URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(password, "UTF-8");
                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    ops.close();

                    InputStream inputStream=httpURLConnection.getInputStream();
                    BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                    StringBuilder response= new StringBuilder();
                    String line;
                    while((line= bufferedReader.readLine())!=null){
                        response.append(line);
                    }
                    httpURLConnection.disconnect();

                    String json_string = response.toString().trim();
                    JSONObject jsonObject = new JSONObject(json_string);
                    JSONArray jsonArray = jsonObject.getJSONArray("server_response");
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jo = jsonArray.getJSONObject(i);
                        id= jo.getString("id");
                        name = jo.getString("First_Name");
                        email1 = jo.getString("Email");
                    }
                } catch (IOException | JSONException e) {
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
            progressDialog.dismiss();

            if(email1 == null && name == null) {
                Toast.makeText(activity_login.this,"Incorrect detail",Toast.LENGTH_SHORT).show();
            }
            else{
                Intent i = new Intent(getApplicationContext(), activity_home.class);
                sessionManager.createLoginSession(id,name,email1);
                startActivity(i);
                finish();
            }
        }
    }
}
