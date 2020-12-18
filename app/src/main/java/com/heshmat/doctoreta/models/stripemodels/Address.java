package com.heshmat.doctoreta.models.stripemodels;

public class Address {
    private String city;
    private String country;
    private String line1;
    private String line2;
    private String postalCode;
    private String state;
    private com.stripe.android.model.Address address;
    public Address(com.stripe.android.model.Address address ){
        this.address=address;
    }

    public String getCity() {
        return address.getCity();
    }


    public String getCountry() {
        return address.getCountry();
    }



    public String getLine1() {
        return address.getLine1();
    }


    public String getLine2() {
        return address.getLine2();
    }



    public String getPostalCode() {
        return address.getPostalCode();
    }



    public String getState() {
        return address.getState();
    }

}
