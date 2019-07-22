package com.example.hk_pc.gmf;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
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

public class activity_forgetPassword extends AppCompatActivity {

    EditText email, pass;
    String emailHolder, passHolder, emailregex;
    Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        email = (EditText)findViewById(R.id.input_email);
        pass = (EditText)findViewById(R.id.input_password);

        submitButton = findViewById(R.id.btn_submit);
    }

    public void submitButton(View view){

        emailHolder = email.getText().toString().trim();
        passHolder = pass.getText().toString().trim();

        if(TextUtils.isEmpty(emailHolder) || TextUtils.isEmpty(passHolder)){
            Toast.makeText(activity_forgetPassword.this, "Please Fill Detail", Toast.LENGTH_SHORT).show();
        }
        else{
            emailregex = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$";
            Pattern pattern = Pattern.compile(emailregex);
            Matcher emailMatcher= pattern.matcher(emailHolder);
            if(emailMatcher.matches()){
                String method = "submit";
                BackgroundTask backgroundTask  = new BackgroundTask(this);
                backgroundTask.execute(method, emailHolder, passHolder);
            }
            else {
                Toast.makeText(activity_forgetPassword.this, "Invalid email address", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class BackgroundTask extends AsyncTask<String, Void, String> {

        Context ctx;
        ProgressDialog progressDialog = new ProgressDialog(activity_forgetPassword.this);

        BackgroundTask(Context ctx){
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute( ) {
            progressDialog.setMessage("\tPlease Wait");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String submit_url = "http://sqli6.000webhostapp.com/forget.php";
            String method = params[0];
            if(method.equals("submit")){
                String email = params[1];
                String password = params[2];

                try {
                    URL url = new URL(submit_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream ops = httpURLConnection.getOutputStream();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ops));
                    String data  = URLEncoder.encode("email", "UTF-8")+"="+URLEncoder.encode(email, "UTF-8")+"&"+
                            URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(password, "UTF-8");
                    bw.write(data);
                    bw.flush();
                    bw.close();
                    ops.close();

                    InputStream ips = httpURLConnection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(ips));
                    StringBuilder response= new StringBuilder();
                    String line;
                    while((line= br.readLine())!=null){
                        response.append(line+"\n");
                    }
                    httpURLConnection.disconnect();
                    return response.toString();

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

            if(result.trim().equalsIgnoreCase("submit successfully")){
                Toast.makeText(activity_forgetPassword.this,"Password Updated",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(activity_forgetPassword.this,activity_login.class);
                startActivity(intent);
            }
            else{
                Toast.makeText(activity_forgetPassword.this,"Password Not Updated",Toast.LENGTH_SHORT).show();
            }
        }
    }

}
