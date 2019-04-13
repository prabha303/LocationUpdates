package prabhalab.client.location.job;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

/**
 * Created by prabha R on 8/24/2017.
 */

public class DriverLocation
{
    private LatLng CLatLng;
    private   String Lat;
    private   String Lng;
    private   long timeStamp;
    private   String datetimeString;
    private   Date DateFormatVariable;
    private Location location;


    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Date getDateFormat() {
        return DateFormatVariable;
    }

    public void setDateFormat(Date dateFormat) {
        DateFormatVariable = dateFormat;
    }


    public LatLng getCLatLng() {
        return CLatLng;
    }

    public void setCLatLng(LatLng CLatLng) {
        this.CLatLng = CLatLng;
    }

    public String getLat() {
        return Lat;
    }

    public void setLat(String lat) {
        Lat = lat;
    }

    public String getLng() {
        return Lng;
    }

    public void setLng(String lng) {
        Lng = lng;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getDatetimeString() {
        return datetimeString;
    }

    public void setDatetimeString(String datetimeString) {
        this.datetimeString = datetimeString;
    }
}
