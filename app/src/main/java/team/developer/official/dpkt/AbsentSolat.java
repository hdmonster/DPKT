package team.developer.official.dpkt;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import team.developer.official.dpkt.Helper.SessionManager;

public class AbsentSolat extends AppCompatActivity {

    RadioGroup radioGroup;
    RadioButton radioButton, Hadir, Tidak;
    Button absent;
    String kehadiran;
    LinearLayout container;
    Boolean isExist = false;
    SessionManager sessionManager;
    Context context;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.solat_absent);

        getSupportActionBar().setTitle("Imam");
        getSupportActionBar().setSubtitle("Sholat "+getIntent().getStringExtra("shalat"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        radioGroup = findViewById(R.id.radioGroup);

        context = AbsentSolat.this;
        progressDialog = new ProgressDialog(context);

        final Date c = Calendar.getInstance().getTime();
        final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE");
        final String dayOfWeek = dateFormat.format(date);

        sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = sessionManager.getUserDetails();
        final String username = user.get(SessionManager.KEY_USER);

        Hadir = findViewById(R.id.radioHadir);
        Tidak = findViewById(R.id.radioTidak);
        container = findViewById(R.id.container);

        absent = findViewById(R.id.btnAbsent);
        absent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isExist == false){
                    kehadiran = "Hadir";
                }else if(isExist == true){
                    for (int i = 0; i < container.getChildCount(); i++) {
                        View view = container.getChildAt(i);
                        EditText reason = (EditText) view.findViewById(R.id.edtReason);
                        kehadiran = reason.getText().toString().trim();
                    }
                }

                String name = username;
                String solat = getIntent().getStringExtra("shalat");
                String day = dayOfWeek;
                String date = df.format(c);
                String hadir = kehadiran;
                String url = "http://dpkt.ubkplus.info/action/absentSolat.php";

                if(hadir.equals("")){
                    Toast.makeText(context, "Anda Belum Memasukkan Alasan!", Toast.LENGTH_SHORT).show();
                }else{
                    absent(name, solat, hadir, day, date, url);
                }

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }

    public void cekKehadiran(View v){

        LayoutInflater layoutInflater =
                (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View addView = layoutInflater.inflate(R.layout.row_reason, null);

        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);

        String cek = radioButton.getText().toString();

        if(cek.equals("Hadir")){
            isExist = false;
            container.removeAllViews();
        }else if(cek.equals("Hadir") && isExist == true){
            isExist = false;
        }else if(cek.equals("Tidak Hadir") && isExist == false){
            isExist = true;
            EditText reason = (EditText) findViewById(R.id.edtReason);
            container.addView(addView);
            Toast.makeText(AbsentSolat.this, "Masukkan Alasan Anda!", Toast.LENGTH_SHORT).show();
        }else{
            isExist = true;
        }

    }
    private void absent(final String name, final String solat, final String hadir, final String day, final String date, final String url){

        progressDialog.setMessage("Sending Record...");
        showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response){
                if(response.contains("Failed")){
                    hideDialog();
                    Toast.makeText(AbsentSolat.this, "Failed to send record, Try again later", Toast.LENGTH_LONG).show();
                }else {
                    hideDialog();
                    Toast.makeText(AbsentSolat.this, "Absent Submitted!", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(AbsentSolat.this, MainActivity.class);
                    startActivity(i);
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                hideDialog();
                Toast.makeText(AbsentSolat.this, "The server is unreachable", Toast.LENGTH_LONG).show();
            }
        }){
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("username",name );
                params.put("solat", solat);
                params.put("kehadiran",hadir );
                params.put("day", day);
                params.put("date", date);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void showDialog(){
        if(!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog(){
        if(progressDialog.isShowing())
            progressDialog.dismiss();
    }
}
