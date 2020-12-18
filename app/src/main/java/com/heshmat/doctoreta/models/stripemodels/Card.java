package com.heshmat.doctoreta.models.stripemodels;



public class Card {
    private Integer exp_month;
    private Integer exp_year;
    private String name;
    private String address_line1_check;
    private String address_zip_check;
    private String last4;
    private String brand;
    private String funding;
    private String country;
    private String currency;
    private String cvc_check;
    private String tokenization_method;
    private com.stripe.android.model.Card card;

    public Card(com.stripe.android.model.Card card) {
        this.card = card;
    }

    public Integer getExp_month() {
        return card.getExpMonth();
    }

    public Integer getExp_year() {
        return card.getExpYear();
    }

    public String getName() {
        return card.getName();
    }

    public String getAddress_line1_check() {
        return card.getAddressLine1Check();
    }

    public String getAddress_zip_check() {
        return card.getAddressZipCheck();
    }

    public String getLast4() {
        return card.getLast4();
    }

    public String getBrand() {
        return card.getBrand().name();
    }

    public String getFunding() {
        return card.getFunding()==null?null:card.getFunding().name();
    }

    public String getCountry() {
        return card.getCountry();
    }

    public String getCurrency() {
        return card.getCurrency();
    }



    public String getCvc_check() {
        return card.getCvcCheck();
    }



    public String getTokenization_method() {
        return card.getTokenizationMethod()==null?null:card.getTokenizationMethod().name();
    }
}
