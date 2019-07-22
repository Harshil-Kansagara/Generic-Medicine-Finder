package com.example.hk_pc.gmf;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.hk_pc.gmf.Information.Information;
import com.example.hk_pc.gmf.Information.RecyclerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class activity_listInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_info);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        BackgroundTask backgroundTask = new BackgroundTask(activity_listInfo.this);
        backgroundTask.execute();

    }

    public class BackgroundTask extends AsyncTask<Void, Information, Void>{
        Context ctx;
        Activity activity;
        RecyclerView recyclerView;
        RecyclerView.Adapter adapter;
        RecyclerView.LayoutManager layoutManager;
        ArrayList<Information> arrayList = new ArrayList<>();

        public BackgroundTask(Context ctx){
            this.ctx=ctx;
            activity = (Activity)ctx;
        }

        String json_String = "http://sqli6.000webhostapp.com/recyclerview.php";

        @Override
        protected void onPreExecute( ) {

            recyclerView = activity.findViewById(R.id.recyclerview);
            layoutManager = new LinearLayoutManager(ctx);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(true);
            adapter = new RecyclerAdapter(arrayList, ctx);
            recyclerView.setAdapter(adapter);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL(json_String);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuilder stringBuilder = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine())!=null){
                    stringBuilder.append(line+"\n");
                }

                httpURLConnection.disconnect();

                String json_string = stringBuilder.toString().trim();

                JSONObject jsonObject = new JSONObject(json_string);
                JSONArray jsonArray = jsonObject.getJSONArray("server_response");
                int count = 0;
                while(count<jsonArray.length()){
                    JSONObject jo = jsonArray.getJSONObject(count);
                    count++;
                    Information information = new Information(jo.getString("Generic_Name"), jo.getString("Brand_Name"));
                    publishProgress(information);
                }
                Log.d("JSON-STRING",json_string);

            } catch (java.io.IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Information... values) {
            arrayList.add(values[0]);
            adapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
