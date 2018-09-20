package team.developer.official.dpkt;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import team.developer.official.dpkt.Adapter.CustomAdapter;
import team.developer.official.dpkt.Adapter.List;
import team.developer.official.dpkt.Helper.SessionManager;

public class KehadiranSolat extends AppCompatActivity {

    Activity context;
    StringBuffer buffer;
    Boolean isRefresh = false;

    HttpGet httppost;
    HttpResponse response;
    HttpClient httpclient;

    ProgressDialog pd;

    CustomAdapter adapter;
    ListView listHadir;
    ArrayList<List> records;

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.solat_kehadiran);

        getSupportActionBar().setTitle("Kehadiran");
        getSupportActionBar().setSubtitle("Imam Solat Rawatib");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        context = this;
        records=new ArrayList<List>();

        listHadir=(ListView)findViewById(R.id.list);

        adapter=new CustomAdapter(context, R.layout.list_row,R.id.day,
                records);

        listHadir.setAdapter(adapter);


        details();
        refreshSwipe();
    }

    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }

    private void details(){
        listHadir=(ListView)findViewById(R.id.list);
        listHadir.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView msolat = (TextView) view.findViewById(R.id.role);
                TextView mdate = (TextView) view.findViewById(R.id.day);
                String solat = msolat.getText().toString();
                String date = mdate.getText().toString();
                String info = "Imam Solat Rawatib";

                Intent i = new Intent(KehadiranSolat.this, ListDetails.class);
                i.putExtra("solat", solat);
                i.putExtra("date", date);
                i.putExtra("info", info);
                finish();
                startActivity(i);
            }
        });
    }

    public void onStart(){

        super.onStart();

        //execute background task
        KehadiranSolat.BackTask bt=new KehadiranSolat.BackTask();
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
                String url = "http://dpkt.ubkplus.info/action/attendanceSolat.php?username="+username;

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

                    DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                    DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
                    String date = json_data.getString("Date");
                    Date dates = inputFormat.parse(date);
                    String outputDate = outputFormat.format(dates);

                    p.setDay(outputDate);

                    p.setRole(json_data.getString("Shalat"));

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

    private void refreshSwipe(){
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.refresh,R.color.refresh1,R.color.refresh2);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        recreate();
                    }
                },2000);
            }
        });
    }
}
