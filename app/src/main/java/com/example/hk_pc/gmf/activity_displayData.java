package com.example.hk_pc.gmf;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.hk_pc.gmf.Information.Information;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class activity_displayData extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_data);
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        String name  = extras.getString("position");

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(name);

        BackgroundTask1 backgroundTask = new BackgroundTask1(this);
        backgroundTask.execute(name);
    }



    class BackgroundTask1 extends AsyncTask<String, Information, Void> {
        String generic_name,brand_name,category,uses,side_effect,strip_per_unit,storage,drug_form,dosage;
        Context ctx;
        TextView gn2,bn,c,u,s,su,st,df,d;

        BackgroundTask1(Context ctx){
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute( ) {
            gn2 = (TextView) findViewById(R.id.gn2);
            bn = (TextView) findViewById(R.id.bn2);
            c = (TextView) findViewById(R.id.c2);
            u = (TextView) findViewById(R.id.u2);
            s = (TextView) findViewById(R.id.se2);
            su = (TextView) findViewById(R.id.spu2);
            st = (TextView) findViewById(R.id.s2);
            df = (TextView) findViewById(R.id.df2);
            d = (TextView) findViewById(R.id.d2);

        }

        @Override
        protected Void doInBackground(String... params) {

            String detail_url = "http://sqli6.000webhostapp.com/detail.php";
            String name = (params[0]);
            try {
                URL url = new URL(detail_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
                String data = URLEncoder.encode("name","UTF-8")+"="+URLEncoder.encode(name,"UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuilder stringBuilder = new StringBuilder();

                String line = "";

                while((line = bufferedReader.readLine())!=null){
                    stringBuilder.append(line+"\n");
                }

                httpURLConnection.disconnect();

                String json_string = stringBuilder.toString().trim();

                JSONObject jsonObject = new JSONObject(json_string);
                JSONArray jsonArray = jsonObject.getJSONArray("server_response");
                for ( int i=0;i<jsonArray.length();i++){
                    JSONObject jo = jsonArray.getJSONObject(i);
                    generic_name = jo.getString("Generic_Name");
                    brand_name = jo.getString("Brand_Name");
                    category = jo.getString("Category");
                    uses = jo.getString("Uses");
                    side_effect = jo.getString("Side_Effect");
                    strip_per_unit = jo.getString("Strip_per_Unit");
                    storage = jo.getString("Storage");
                    drug_form = jo.getString("Drug_Form");
                    dosage = jo.getString("Dosage");
                }
                Log.d("Generic_Name",generic_name);
                Log.d("JSON_STRING",json_string);


            } catch (java.io.IOException e ) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            gn2.setText(generic_name);
            bn.setText(brand_name);
            c.setText(category);
            u.setText(uses);
            s.setText(side_effect);
            su.setText(strip_per_unit);
            st.setText(storage);
            df.setText(drug_form);
            d.setText(dosage);
        }

        @Override
        protected void onProgressUpdate(Information... values) {

        }
    }
}
