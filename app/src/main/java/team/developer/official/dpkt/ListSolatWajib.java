package team.developer.official.dpkt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ListSolatWajib extends AppCompatActivity {

    Button subuh, zuhur, asar, maghrib, isya;
    String shalat, extUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_solat_wajib);

        getSupportActionBar().setTitle("Sholat Rawatib");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        subuh = findViewById(R.id.btnSubuh);
        zuhur = findViewById(R.id.btnZuhur);
        asar = findViewById(R.id.btnAsar);
        maghrib = findViewById(R.id.btnMaghrib);
        isya = findViewById(R.id.btnIsya);

        subuh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shalat = "Subuh";
                goAbsent();
            }
        });

        zuhur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shalat = "Zuhur";
                goAbsent();
            }
        });

        asar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shalat = "Asar";
                goAbsent();
            }
        });

        maghrib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shalat = "Maghrib";
                goAbsent();
            }
        });

        isya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shalat = "Isya";
                goAbsent();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }

    private void goAbsent(){
        Intent i = new Intent(getApplicationContext(), AbsentSolat.class);
        i.putExtra("shalat", shalat);
        startActivity(i);
    }
}
