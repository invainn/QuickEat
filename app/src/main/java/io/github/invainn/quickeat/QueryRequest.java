package io.github.invainn.quickeat;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Anthony on 4/28/2015.
 */
public class QueryRequest extends AsyncTask<Object, Void, ArrayList<Places>>  {

    private static final String LOG_TAG = "QueryRequest";
    private static final String apiBaseUrl = "https://maps.googleapis.com/maps/api/place";

    private static final String search = "/search";
    private static final String jsonOut = "/json";
    private static final String details = "/details";

    private static final String apiKey = "AIzaSyCiuHuWUmVqnUnzVgE5GSMHjWWclww3wCo";

    private List_Activity activity;

    public QueryRequest(List_Activity activity) {
        this.activity = activity;
    }


    public static ArrayList<Places> search(String keyword, double lat, double lng) {
        ArrayList<Places> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();


        try {
            StringBuilder sb = new StringBuilder(apiBaseUrl);
            sb.append(search);
            sb.append(jsonOut);
            sb.append("?sensor=false");
            sb.append("&key=" + apiKey);
            sb.append("&keyword=" + URLEncoder.encode(keyword, "utf8"));
            sb.append("&location=" + String.valueOf(lat) + "," + String.valueOf(lng));
            sb.append("&radius=1500");

            System.out.println(sb.toString());

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // need to test but im pretty sure this works
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }

            // System.out.println(jsonResults);

        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Could not process URL", e);
            return null;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Could not connect to Places API", e);
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        // Parsing jsonResults
        try {
            // Setting requested data to a JSONObject for it to be parsed
            JSONObject jsonObj = new JSONObject(jsonResults.toString());

            // Requested data contains results, an array of places, set them to resultJsonArray
            JSONArray resultJsonArray = jsonObj.getJSONArray("results");

            // Set resultList to length of the resultJsonArray to prep for places
            resultList = new ArrayList<Places>(resultJsonArray.length());

            // Get reference string to get place details
            // and then set them to an array index in resultList
            for(int x = 0; x < resultJsonArray.length(); x++) {
                Places place = new Places();

                place.reference = resultJsonArray.getJSONObject(x).getString("reference");
                place = placeDetails(place.reference);

                resultList.add(place);
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Could not process JSON results", e);
        }
        return resultList;
    }

    public static Places placeDetails(String ref) {
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();

        try {
            StringBuilder sb = new StringBuilder(apiBaseUrl);

            sb.append(details);
            sb.append(jsonOut);
            sb.append("?key=" + apiKey);
            sb.append("&reference=" + URLEncoder.encode(ref, "utf8"));
            sb.append("&sensor=false");

            System.out.println(sb.toString());

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            int read;
            char[] buff = new char[1024];
            while((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (UnsupportedEncodingException e) {
            Log.e(LOG_TAG, "Unsupported Encoding Exception");
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Could not process URL");
        } catch (IOException e) {
            Log.e(LOG_TAG, "Could not connect to Places API");
        }

        Places p = null;

        try {
            JSONObject jsonObj = new JSONObject(jsonResults.toString()).getJSONObject("result");

            p = new Places();
            p.name = jsonObj.getString("name");
            p.address = jsonObj.getString("formatted_address");
            p.latitude = jsonObj.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
            p.longitude = jsonObj.getJSONObject("geometry").getJSONObject("location").getDouble("lng");

            // some places might not have a phone number available
            if(jsonObj.has("formatted_phone_number")) {
                p.phoneNumber = jsonObj.getString("formatted_phone_number");
            }
            // same with website
            if(jsonObj.has("website")) {
                p.website = jsonObj.getString("website");
            }
            // and same with opening hours
            if(jsonObj.has("opening_hours")) {
                p.openNow = jsonObj.getJSONObject("opening_hours").getBoolean("open_now");
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Could not process JSON results");
            e.printStackTrace();
        }

        return p;
    }



    @Override
    protected ArrayList<Places> doInBackground(Object... params) {
        LatLng loc = (LatLng) params[0];
        String keyword = (String) params[1];

        return search(keyword, loc.latitude, loc.longitude);

    }

    @Override
    protected void onPostExecute(ArrayList<Places> results) {
        activity.setResults(results);
    }
}
