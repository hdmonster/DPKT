package team.developer.official.dpkt;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import team.developer.official.dpkt.Adapter.CustomAdapter;
import team.developer.official.dpkt.Adapter.List;
import team.developer.official.dpkt.Helper.SessionManager;

public class JadwalActivity extends AppCompatActivity {

    Activity context;
    StringBuffer buffer;

    HttpGet httppost;
    HttpResponse response;
    HttpClient httpclient;

    ProgressDialog pd;

    CustomAdapter adapter;
    ListView listProduct;
    ArrayList<List> records;

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jadwal);

        getSupportActionBar().setTitle("Jadwal Anda");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        context = this;
        records=new ArrayList<List>();

        listProduct=(ListView)findViewById(R.id.list);

        adapter=new CustomAdapter(context, R.layout.list_row,R.id.day,
                records);

        listProduct.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }

    public void onStart(){

        super.onStart();

        //execute background task
        BackTask bt=new BackTask();

        bt.execute();

    }

    private class BackTask extends AsyncTask<Void, Void, Void> {

        protected void onPreExecute(){

            super.onPreExecute();

            pd = new ProgressDialog(context);

            pd.setTitle("Retrieving data");

            pd.setMessage("Please wait...");

            pd.setCancelable(true);

            pd.setIndeterminate(true);

            pd.show();



        }

        @Override
        protected Void doInBackground(Void... params) {

            InputStream is = null;

            String result="";

            sessionManager = new SessionManager(getApplicationContext());
            HashMap<String, String> user = sessionManager.getUserDetails();
            String username = user.get(SessionManager.KEY_USER);

            try{
                String url = "http://dpkt.ubkplus.info/action/jadwal.php?username="+username;

                httpclient=new DefaultHttpClient();

                httppost= new HttpGet(url);

                response = httpclient.execute(httppost);

                HttpEntity entity = response.getEntity();

                // Get our response as a String.
                is = entity.getContent();

            }catch(Exception e){



                if(pd!=null)

                    pd.dismiss(); //close the dialog if error occurs

                Log.e("ERROR", e.getMessage());



            }

            //convert response to string

            try{

                BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf-8"),8);

                StringBuilder sb = new StringBuilder();

                String line = null;

                while ((line = reader.readLine()) != null) {

                    sb.append(line+"\n");

                }

                is.close();

                result=sb.toString();

            }catch(Exception e){

                Log.e("ERROR", "Error converting result "+e.toString());



            }

            //parse json data

            try{

                // Remove unexpected characters that might be added to beginning of the string
                result=result.substring(result.indexOf("["));

                JSONArray jArray =new JSONArray(result);

                for(int i=0;i<jArray.length();i++){

                    JSONObject json_data =jArray.getJSONObject(i);

                    List p=new List();

                    p.setDay(json_data.getString("Role"));

                    p.setRole(json_data.getString("Day"));



                    records.add(p);



                }





            }

            catch(Exception e){

                Log.e("ERROR", "Error pasting data "+e.toString());





            }



            return null;


        }

        protected void onPostExecute(Void result){



            if(pd!=null) pd.dismiss(); //close dialog

            Log.e("size", records.size() + "");

            adapter.notifyDataSetChanged(); //notify the ListView to get new records



        }

    }

}
