package io.github.invainn.quickeat;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.apache.http.HttpRequestFactory;

import java.net.URL;
import java.util.ArrayList;


public class List_Activity extends ListActivity {

    ArrayList<Places> results;

    private ListView lv;

    LatLng currentLocation;

    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_);

        // Receive bundle and get currentlocation
        Bundle bundle = getIntent().getParcelableExtra("bundle");
        currentLocation = bundle.getParcelable("currentLocation");
        String keyword = getIntent().getStringExtra("keyword");


        new QueryRequest(this).execute(currentLocation, keyword, 1);

        toast = Toast.makeText(List_Activity.this, "Finding restaurants...", Toast.LENGTH_LONG);
        toast.show();
    }

    public void setResults(ArrayList<Places> results) {
        this.results = (ArrayList<Places>) results.clone();

        lv = (ListView) findViewById(android.R.id.list);

        ArrayAdapter<Places> arrayAdapter = new ArrayAdapter<Places>(this, R.layout.list_white_text, R.id.list_content, results);

        lv.setAdapter(arrayAdapter);

        toast.cancel();

        toast = Toast.makeText(List_Activity.this, "Found " + results.size() + " restaurants", Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // Toast.makeText(List_Activity.this, results.get(position).getName(), Toast.LENGTH_LONG).show();

        Places p = results.get(position);

        Intent intent = new Intent(List_Activity.this, InfoActivity.class);
        intent.putExtra("name", p.name);
        intent.putExtra("address", p.address);
        intent.putExtra("phoneNumber", p.phoneNumber);
        intent.putExtra("open_now", p.openNow);
        intent.putExtra("website", p.website);
        intent.putExtra("pLat", p.latitude);
        intent.putExtra("pLng", p.longitude);
        intent.putExtra("currentLatitude", currentLocation.latitude);
        intent.putExtra("currentLongitude", currentLocation.longitude);

        startActivity(intent);


    }
}




