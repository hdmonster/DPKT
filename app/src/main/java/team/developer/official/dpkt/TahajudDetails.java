package team.developer.official.dpkt;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import team.developer.official.dpkt.Helper.MySingleton;
import team.developer.official.dpkt.Helper.SessionManager;

public class TahajudDetails extends AppCompatActivity {

    TextView solat, hadir, dated;
    SessionManager sessionManager;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tahajud_details);

        getSupportActionBar().setTitle("Details");
        getSupportActionBar().setSubtitle(getIntent().getStringExtra("role")+" Solat Tahajud Dan Witir");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        pd = new ProgressDialog(TahajudDetails.this);
        pd.setMessage("Loading details...");
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);

        solat = (TextView) findViewById(R.id.solat);
        dated = (TextView) findViewById(R.id.date);
        hadir = (TextView) findViewById(R.id.hadir);

        getDetails();

    }
    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }

    private void getDetails() {

        sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = sessionManager.getUserDetails();
        String username = user.get(SessionManager.KEY_USER);

        DateFormat inputFormat = new SimpleDateFormat("dd MMM yyyy");
        DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

        final String date = getIntent().getStringExtra("date");
        Date dates = null;
        try {
            dates = inputFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String outputDate = "&date="+outputFormat.format(dates);

        String url= "http://dpkt.ubkplus.info/action/"+getIntent().getStringExtra("url")+"?username="+username+outputDate;
        pd.show();
        StringRequest stringReq = new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        pd.hide();

                        try {

                            JSONArray jsonarray = new JSONArray(response);

                            for(int i=0; i < jsonarray.length(); i++) {

                                JSONObject jsonobject = jsonarray.getJSONObject(i);

                                String day = jsonobject.getString("Day");
                                String Dates = jsonobject.getString("Date");
                                String attend = jsonobject.getString("Attendance");

                                DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                                DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
                                Date dates = inputFormat.parse(Dates);
                                String outputDate = outputFormat.format(dates);

                                dated.setText(day+", "+outputDate);
                                solat.setText(getIntent().getStringExtra("role"));

                                String finalHadir;

                                if(attend.equals("Hadir")){
                                    finalHadir = "Hadir";
                                }else{
                                    finalHadir = "Tidak Hadir, "+attend;
                                }

                                hadir.setText(finalHadir);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();


                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error != null){
                            pd.hide();
                            Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_LONG).show();
                        }
                    }
                }

        );

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringReq);
    }
}
