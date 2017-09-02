package io.github.invainn.quickeat;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.FloatMath;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class InfoActivity extends Activity {

    String number;
    String website;

    double placeLat;
    double placeLng;

    double currentLat;
    double currentLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        String name = getIntent().getStringExtra("name");
        String address = getIntent().getStringExtra("address");
        number = getIntent().getStringExtra("phoneNumber");
        website = getIntent().getStringExtra("website");

        Location currentLoc = new Location("");
        Location dest = new Location("");

        boolean openNow = getIntent().getBooleanExtra("open_now", false);

        currentLat = getIntent().getDoubleExtra("currentLatitude", 0);
        currentLong = getIntent().getDoubleExtra("currentLongitude", 0);

        placeLat = getIntent().getDoubleExtra("pLat", 0);
        placeLng = getIntent().getDoubleExtra("pLng", 0);

        // Toast.makeText(InfoActivity.this, "" + placeLat, Toast.LENGTH_LONG).show();

        TextView restaurantName = (TextView) findViewById(R.id.restaurantName);
        restaurantName.setText(name);

        TextView restaurantAddress = (TextView) findViewById(R.id.restaurantAddress);
        restaurantAddress.setText(address);

        TextView restaurantNumber = (TextView) findViewById(R.id.restaurantPhone);
        if(number != null) {
            restaurantNumber.setText(number);
            setupCallButton();
        } else {
            restaurantNumber.setText("N/A");
        }

        TextView restaurantOpenNow = (TextView) findViewById(R.id.restaurantOpenNow);
        if(openNow == false) {
            restaurantOpenNow.setText("No");
        } else if(openNow == true) {
            restaurantOpenNow.setText("Yes");
        } else {
            restaurantOpenNow.setText("N/A");
        }

        TextView restaurantDistance = (TextView) findViewById(R.id.distanceFromRestaurant);

        currentLoc.setLatitude(currentLat);
        currentLoc.setLongitude(currentLong);

        dest.setLatitude(placeLat);
        dest.setLongitude(placeLng);

        double dist = currentLoc.distanceTo(dest) * 0.000621371;
        dist = Math.round(dist * 100.0) / 100.0;
        restaurantDistance.setText(String.valueOf(dist) + " Miles");

        getDirections();
        websiteButton();
    }

    public void setupCallButton() {
        Button callNumber = (Button) findViewById(R.id.callButton);

        callNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + number));
                startActivity(intent);
            }
        });
    }

    public void getDirections() {
        Button getDir = (Button) findViewById(R.id.directionsButton);

        getDir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/maps?saddr=" + currentLat + "," + currentLong + "&daddr=" + placeLat + "," + placeLng));
                startActivity(intent);
            }
        });
    }

    public void websiteButton() {
        Button getWeb = (Button) findViewById(R.id.websiteButton);

        getWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(website != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(website));
                    startActivity(intent);
                } else {
                    Toast.makeText(InfoActivity.this, "Website is unavailable", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
