package team.developer.official.dpkt;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import team.developer.official.dpkt.Helper.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private Button btnLogin;
    private EditText edtUser, edtPass;
    private ProgressDialog progressDialog;
    private TextView txtReg;
    private Context context;

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setTitle("DPKT");
        getSupportActionBar().setSubtitle("By Official Developer Team");

        sessionManager = new SessionManager(getApplicationContext());
        sessionManager.checkLogin();

        context = LoginActivity.this;
        progressDialog = new ProgressDialog(context);
        edtUser = findViewById(R.id.logUsername);
        edtPass = findViewById(R.id.logPassword);

        txtReg = (TextView) findViewById(R.id.txtReg);
        txtReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });

        btnLogin = findViewById(R.id.login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = edtUser.getText().toString();
                String pass= edtPass.getText().toString();

                if (user.equals("") || pass.equals("")){
                    Toast.makeText(LoginActivity.this, "That's not a username nor password!", Toast.LENGTH_SHORT).show();
                }else{
                    Login();
                }

            }
        });

    }

    private void Login() {
        progressDialog.setMessage("Logging in...");
        showDialog();
        String url = "http://dpkt.ubkplus.info/action/login.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                if(response.contains("success")){
                    hideDialog();
                    sessionManager.createSession(edtUser.getText().toString(), edtPass.getText().toString());
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    hideDialog();
                    Toast.makeText(LoginActivity.this, "Password Invalid", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog();
                Toast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", edtUser.getText().toString().trim());
                params.put("password", edtPass.getText().toString().trim());
                return params;
            }
        };
        requestQueue.add(stringRequest);
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
