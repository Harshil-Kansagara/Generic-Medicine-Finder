package com.example.hk_pc.gmf;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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

public class activity_register extends AppCompatActivity {

    EditText fname,lname,mobile,email,password;
    String fnameHolder, lnameHolder, mobileHolder, emailHolder, passwordHolder , nameRegex, emailRegex, mobileRegex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fname = (EditText)findViewById(R.id.input_first_name);
        lname = (EditText)findViewById(R.id.input_last_name);
        mobile = (EditText)findViewById(R.id.input_mm);
        email = (EditText)findViewById(R.id.input_email);
        password = (EditText)findViewById(R.id.input_password);
    }

    public void registerButtonClicked(View view){
        fnameHolder = fname.getText().toString().trim();
        lnameHolder = lname.getText().toString().trim();
        mobileHolder = mobile.getText().toString().trim();
        emailHolder = email.getText().toString().trim();
        passwordHolder = password.getText().toString().trim();

        if(TextUtils.isEmpty(fnameHolder) || TextUtils.isEmpty(lnameHolder) || TextUtils.isEmpty(emailHolder) || TextUtils.isEmpty(mobileHolder) || TextUtils.isEmpty(passwordHolder)){
            Toast.makeText(activity_register.this,"Please Fill up the form",Toast.LENGTH_SHORT).show();
        }
        else {
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

            if(fnameMatches.matches() && lnameMatches.matches() && emailMatches.matches() && mobileMatches.matches()){
                String method="register";
                BackgroundTask backgroundTask = new BackgroundTask(this);
                backgroundTask.execute(method, fnameHolder, lnameHolder, mobileHolder, emailHolder, passwordHolder);
            }
            else{
                if(!fnameMatches.matches()){
                    Toast.makeText(activity_register.this, "Please enter valid first name", Toast.LENGTH_SHORT).show();
                }
                else if(!lnameMatches.matches()){
                    Toast.makeText(activity_register.this, "Please enter valid last name", Toast.LENGTH_SHORT).show();
                }
                else if(!emailMatches.matches()){
                    Toast.makeText(activity_register.this, "Please enter valid email",Toast.LENGTH_SHORT).show();
                }
                else if(!mobileMatches.matches()){
                    Toast.makeText(activity_register.this,"Please enter valid mobile number", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public class BackgroundTask extends AsyncTask<String, Void, String>{

        ProgressDialog progressDialog = new ProgressDialog(activity_register.this);
        Context ctx;
        AlertDialog alertDialog;
        BackgroundTask(Context ctx){
            this.ctx=ctx;
        }

        @Override
        protected void onPreExecute( ) {
            progressDialog.setMessage("\tPlease Wait");
            progressDialog.setCancelable(false);
            progressDialog.show();

            alertDialog=new AlertDialog.Builder(ctx).create();
        }

        @Override
        protected String doInBackground(String... params) {
            String registration_url = "http://sqli6.000webhostapp.com/registration.php";
            String method = params[0];

            if(method.equals("register")){
                String fname = params[1];
                String lname = params[2];
                String mobile = params[3];
                String email = params[4];
                String password = params[5];

                try {
                    URL url = new URL(registration_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream ops = httpURLConnection.getOutputStream();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
                    String data = URLEncoder.encode("fname","UTF-8")+"="+URLEncoder.encode(fname,"UTF-8")+"&"
                            +URLEncoder.encode("lname","UTF-8")+"="+URLEncoder.encode(lname,"UTF-8")+"&"
                            +URLEncoder.encode("mobile", "UTF-8")+"="+URLEncoder.encode(mobile,"UTF-8")+"&"+
                            URLEncoder.encode("email", "UTF-8")+"="+URLEncoder.encode(email,"UTF-8")+"&"+
                            URLEncoder.encode("password", "UTF-8")+"="+URLEncoder.encode(password,"UTF-8");
                    bw.write(data);
                    bw.flush();
                    bw.close();
                    ops.close();

                    InputStream ips = httpURLConnection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(ips,"iso-8859-1"));
                    StringBuilder result= new StringBuilder();
                    String line = "";
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
            progressDialog.dismiss();

            alertDialog.setMessage(result);
            alertDialog.show();

            if(result.trim().equalsIgnoreCase("Registration Successful")){
                Intent intent= new Intent(activity_register.this, activity_login.class);
                startActivity(intent);
            }
        }
    }
}
