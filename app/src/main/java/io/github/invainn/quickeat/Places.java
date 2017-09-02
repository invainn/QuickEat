package io.github.invainn.quickeat;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;
import java.util.Locale;

/**
 * Created by Anthony on 5/3/2015.
 */
public class Places {

    public String address;
    public String reference;
    public String name;
    public String phoneNumber;
    public String website;

    public double latitude;
    public double longitude;

    public boolean openNow;

    // ArrayAdapter calls the toString function for something to place into the listview
    public String toString() {
        return this.name;
    }

}
