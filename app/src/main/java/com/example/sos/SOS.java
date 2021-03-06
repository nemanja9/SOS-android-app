package com.example.sos;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

public class SOS extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);
        Button dugmeHitna = findViewById(R.id.btnHitna);
        dugmeHitna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:123456789"));
                if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    Activity#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    Toast.makeText(SOS.this,"Morate odobriti u podesavanjima dozvolu za pozive!",Toast.LENGTH_LONG).show();

                    return;
                }
                startActivity(callIntent);
            }
        });
        Button dugmePozoviKontakta = findViewById(R.id.btnPozovi);
        dugmePozoviKontakta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                if (sharedPreferences.getString("telefon",null)==null || sharedPreferences.getString("telefon",null).isEmpty()){
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:"));
                    startActivity(intent);
                    Toast.makeText(SOS.this,"Broj nije dodat u podesavanjima!",Toast.LENGTH_LONG).show();

                    return;
                }
                callIntent.setData(Uri.parse("tel:"+sharedPreferences.getString("telefon",null)));
                if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    Activity#requestPermissions
                    Toast.makeText(SOS.this,"Morate odobriti u podesavanjima dozvolu za pozive!",Toast.LENGTH_LONG).show();

                    return;
                }
                startActivity(callIntent);

            }
        });

        Button btnPoruka = findViewById(R.id.btnPosaljiSMS);
        btnPoruka.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dajLokacijuMofo();
            }
        });

    }

    private void dajLokacijuMofo(){
        final FusedLocationProviderClient klijent = LocationServices.getFusedLocationProviderClient(this);

        klijent.getLastLocation().addOnSuccessListener(SOS.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                System.out.println( location);
                if (location != null){
                    SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
                    posaljiLokacijuMOFO(sharedPreferences.getString("telefon",""),location);
                    Toast.makeText(SOS.this,"Poruka uspesno poslata!",Toast.LENGTH_LONG).show();

                }

            }
        });



    }
    public void posaljiLokacijuMOFO(String phoneNumber, Location currentLocation) {
        SmsManager smsManager = SmsManager.getDefault();
        StringBuffer smsBody = new StringBuffer();
        smsBody.append("Moja trenutna lokacija: http://maps.google.com?q=");
        smsBody.append(currentLocation.getLatitude());
        smsBody.append(",");
        smsBody.append(currentLocation.getLongitude());
        smsManager.sendTextMessage(phoneNumber, null, smsBody.toString(), null, null);

    }
}
