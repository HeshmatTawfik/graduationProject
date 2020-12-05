package com.heshmat.doctoreta.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.GeoPoint;

public class AddressInfo implements Parcelable {
    private GeoPoint location;
    private String address;
    private String countryCode;
    private String country;
    private String city;

    protected AddressInfo(Parcel in) {
        address = in.readString();
        countryCode = in.readString();
        country = in.readString();
        city = in.readString();
        double lat = in.readDouble();
        double lng = in.readDouble();
        location = new GeoPoint(lat, lng);
    }

    @Exclude
    public static final Creator<AddressInfo> CREATOR = new Creator<AddressInfo>() {
        @Override
        public AddressInfo createFromParcel(Parcel in) {
            return new AddressInfo(in);
        }
        @Exclude
        @Override
        public AddressInfo[] newArray(int size) {
            return new AddressInfo[size];
        }
    };

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public AddressInfo(GeoPoint location, String address, String countryCode, String country, String city) {
        this.location = location;
        this.address = address;
        this.countryCode = countryCode;
        this.country = country;
        this.city = city;
    }
    public AddressInfo(){}

    @Exclude

    @Override
    public int describeContents() {
        return 0;
    }
    @Exclude
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeString(countryCode);
        dest.writeString(country);
        dest.writeString(city);
        dest.writeDouble(location.getLatitude());
        dest.writeDouble(location.getLongitude());
    }
}
