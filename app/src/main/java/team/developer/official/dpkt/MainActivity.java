package team.developer.official.dpkt;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import team.developer.official.dpkt.Helper.MySingleton;
import team.developer.official.dpkt.Helper.SessionManager;

public class MainActivity extends AppCompatActivity {

    private TextView mName, mUsername, mPin,mToday;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView navigationView;
    private Button Pengawas, Solat, Tahajud;
    private ProgressDialog pd;

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("DPKT");

        pd = new ProgressDialog(MainActivity.this);
        pd.setMessage("Loading profile...");
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        String dayOfTheWeek = sdf.format(d);

        mToday = (TextView) findViewById(R.id.txtToday);

        Solat = (Button) findViewById(R.id.btnSolat);
        Tahajud = (Button) findViewById(R.id.btnTahajud);
        Pengawas = (Button) findViewById(R.id.btnPengawas);

        Solat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ListSolatWajib.class);
                startActivity(i);
            }
        });

        Tahajud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent l= new Intent(MainActivity.this, AbsentTahajud.class);
                l.putExtra("role", "Imam");
                l.putExtra("RoleUrl", "absentTahajud.php");
                startActivity(l);
            }
        });

        Pengawas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent p = new Intent(MainActivity.this, AbsentTahajud.class);
                p.putExtra("role", "Pengawas");
                p.putExtra("RoleUrl", "absentPengawas.php");
                startActivity(p);
            }
        });

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        navigationView = (NavigationView) findViewById(R.id.navigationView);
        View headerView = navigationView.getHeaderView(0);
        mUsername = (TextView) headerView.findViewById(R.id.Username);
        mPin = (TextView) headerView.findViewById(R.id.Pin);

        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        itemListener();
        getSqlDetails();
        getJadwalToday();
        disableButton();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void disableButton(){
        Solat.setEnabled(false);
        Tahajud.setEnabled(false);
        Pengawas.setEnabled(false);
        Solat.setBackgroundResource(R.drawable.disable_rounded_button);
        Tahajud.setBackgroundResource(R.drawable.disable_rounded_button);
        Pengawas.setBackgroundResource(R.drawable.disable_rounded_button);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void itemListener(){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.jadwal:
                        Intent o = new Intent(MainActivity.this, JadwalActivity.class);
                        startActivity(o);
                        break;
                    case R.id.kehadiranSolat:
                        Intent q = new Intent(MainActivity.this, KehadiranSolat.class);
                        startActivity(q);
                        break;
                    case R.id.kehadiranTahajud:
                        Intent t = new Intent(MainActivity.this, KehadiranTahajud.class);
                        t.putExtra("role", "Imam");
                        t.putExtra("RoleUrl", "attendanceTahajud.php");
                        t.putExtra("DetailsUrl", "tahajudDetails.php");
                        startActivity(t);
                        break;
                    case R.id.kehadiranP:
                        Intent p = new Intent(MainActivity.this, KehadiranTahajud.class);
                        p.putExtra("role", "Pengawas");
                        p.putExtra("RoleUrl", "attendancePengawas.php");
                        p.putExtra("DetailsUrl", "pengawasDetails.php");
                        startActivity(p);
                        break;
                    case R.id.logout:
                        sessionManager.logout();
                        Toast.makeText(MainActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });
    }

    private void getSqlDetails() {

        sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = sessionManager.getUserDetails();
        String username = user.get(SessionManager.KEY_USER);

        String url= "http://dpkt.ubkplus.info/action/profile.php?username="+username;
        pd.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        pd.hide();


                        try {

                            JSONArray jsonarray = new JSONArray(response);

                            for(int i=0; i < jsonarray.length(); i++) {

                                JSONObject jsonobject = jsonarray.getJSONObject(i);


                                String name = jsonobject.getString("Name");
                                String username = jsonobject.getString("Username");
                                String pin = jsonobject.getString("Pin");

                                mPin.setText(pin);
                                mUsername.setText(username);
                                getSupportActionBar().setSubtitle("Halo, "+name);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();


                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error != null){

                            Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_LONG).show();
                        }
                    }
                }

        );

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void getJadwalToday() {

        sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = sessionManager.getUserDetails();
        String username = user.get(SessionManager.KEY_USER);

        String url= "http://dpkt.ubkplus.info/action/jadwalToday.php?username="+username;
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


                                String role = jsonobject.getString("Role");

                                mToday.setText(role);

                                String jadwal = mToday.getText().toString();

                                if(jadwal.equals("Pengawas Tahajud")){
                                    Pengawas.setEnabled(true);
                                    Solat.setEnabled(false);
                                    Tahajud.setEnabled(false);
                                    Pengawas.setBackgroundResource(R.drawable.default_rounded_button);
                                    Solat.setBackgroundResource(R.drawable.disable_rounded_button);
                                    Tahajud.setBackgroundResource(R.drawable.disable_rounded_button);
                                    Toast.makeText(MainActivity.this, "Your Role Today: "+jadwal, Toast.LENGTH_LONG).show();
                                } else if(jadwal.equals("Imam Tahajud dan Witir")){
                                    Tahajud.setEnabled(true);
                                    Solat.setEnabled(false);
                                    Pengawas.setEnabled(false);
                                    Tahajud.setBackgroundResource(R.drawable.default_rounded_button);
                                    Solat.setBackgroundResource(R.drawable.disable_rounded_button);
                                    Pengawas.setBackgroundResource(R.drawable.disable_rounded_button);
                                    Toast.makeText(MainActivity.this, "Your Role Today: "+jadwal, Toast.LENGTH_LONG).show();
                                }else if(jadwal.equals("Imam Solat Rawatib")){
                                    Solat.setEnabled(true);
                                    Pengawas.setEnabled(false);
                                    Tahajud.setEnabled(false);
                                    Solat.setBackgroundResource(R.drawable.default_rounded_button);
                                    Pengawas.setBackgroundResource(R.drawable.disable_rounded_button);
                                    Tahajud.setBackgroundResource(R.drawable.disable_rounded_button);
                                    Toast.makeText(MainActivity.this, "Your Role Today: "+jadwal, Toast.LENGTH_LONG).show();
                                }else{
                                    Solat.setEnabled(false);
                                    Tahajud.setEnabled(false);
                                    Pengawas.setEnabled(false);
                                    Solat.setBackgroundResource(R.drawable.disable_rounded_button);
                                    Tahajud.setBackgroundResource(R.drawable.disable_rounded_button);
                                    Pengawas.setBackgroundResource(R.drawable.disable_rounded_button);
                                    Toast.makeText(MainActivity.this, "You Got No Role Today!", Toast.LENGTH_LONG).show();
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();


                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error != null){

                            Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_LONG).show();
                        }
                    }
                }

        );

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringReq);
    }

}
