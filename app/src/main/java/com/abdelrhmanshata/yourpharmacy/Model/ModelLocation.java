package com.abdelrhmanshata.yourpharmacy.Model;

import java.io.Serializable;

public class ModelLocation implements Serializable {
    String Latitude, Longitude, CountryName, Locality, Address;

    public ModelLocation() {
    }

    public ModelLocation(String latitude, String longitude, String countryName, String locality, String address) {
        Latitude = latitude;
        Longitude = longitude;
        CountryName = countryName;
        Locality = locality;
        Address = address;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getCountryName() {
        return CountryName;
    }

    public void setCountryName(String countryName) {
        CountryName = countryName;
    }

    public String getLocality() {
        return Locality;
    }

    public void setLocality(String locality) {
        Locality = locality;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    @Override
    public String toString() {
        return "ModelLocation{" +
                "Latitude='" + Latitude + '\'' +
                ", Longitude='" + Longitude + '\'' +
                ", CountryName='" + CountryName + '\'' +
                ", Locality='" + Locality + '\'' +
                ", Address='" + Address + '\'' +
                '}';
    }
}
