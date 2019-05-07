package com.bitlove.fetlife.legacy.util;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class MapUtil {

    public static double getRange(LatLng latlng1, LatLng latlng2) {
        Location location1 = new Location("1");
        location1.setLatitude(latlng1.latitude);
        location1.setLongitude(latlng1.longitude);
        Location location2 = new Location("2");
        location2.setLatitude(latlng2.latitude);
        location2.setLongitude(latlng2.longitude);
        return location1.distanceTo(location2) / 1000d;
    }

}
