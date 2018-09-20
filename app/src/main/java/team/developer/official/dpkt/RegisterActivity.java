package team.developer.official.dpkt;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private EditText mName, mUsername, mPassword, mConfirm,mPin;
    private Button mRegister;
    Context context;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        context = RegisterActivity.this;
        progressDialog = new ProgressDialog(context);
        mName = findViewById(R.id.txtName);
        mUsername = findViewById(R.id.txtUsername);
        mPassword = findViewById(R.id.txtPassword);
        mConfirm = findViewById(R.id.txtConfirm);
        mPin = findViewById(R.id.txtPin);

        mRegister = (Button) findViewById(R.id.btnRegister);
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mName.getText().toString();
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();
                String confirm = mConfirm.getText().toString();
                String pin = mPin.getText().toString();
                String url = "http://dpkt.ubkplus.info/action/register.php";

                if (name.equals("") || username.equals("") || password.equals("") || pin.equals("")){
                    Toast.makeText(context, "All forms required!", Toast.LENGTH_SHORT).show();
                }else if (!Objects.equals(password, confirm)){
                    Toast.makeText(context, "Confirm And Password Must Match!", Toast.LENGTH_SHORT).show();
                }else if(pin.length() <= 5){
                    Toast.makeText(context, "PIN length must have 6 character!", Toast.LENGTH_SHORT).show();
                }else{
                    register(name, username, password, pin, url);
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }

    private void register(final String name, final String username, final String password, final String pin, final String url) {

        progressDialog.setMessage("Registering...");
        showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response){
                if(response.contains("Failed")){
                    hideDialog();
                    Toast.makeText(RegisterActivity.this, "Register Failed, Try again later", Toast.LENGTH_LONG).show();
                }else {
                    hideDialog();
                    Toast.makeText(RegisterActivity.this, "Successfully Registered", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(i);
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                hideDialog();
                Toast.makeText(RegisterActivity.this, "The server is unreachable", Toast.LENGTH_LONG).show();
            }
        }){
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("name",name );
                params.put("username", username);
                params.put("password",password );
                params.put("pin", pin);
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
