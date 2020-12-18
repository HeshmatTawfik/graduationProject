package com.heshmat.doctoreta.models.stripemodels;

import com.stripe.android.model.Source;


public class Owner  {
    private Address address;
    private String email;
    private String name;
    private String phone;
    private String verified_address;
    private String verified_email;
    private String verified_name;
    private String verified_phone;
    private Source.Owner owner;
    private com.stripe.android.model.Address  addressStripe;
    public Owner(Source.Owner owner, com.stripe.android.model.Address addressStripe) {
        this.owner = owner;
        this.addressStripe=addressStripe;
        this.address=new Address(addressStripe);
    }

    public String getEmail() {
        return owner.getEmail();
    }

    public String getName() {
        return owner.getName();
    }

    public String getPhone() {
        return owner.getPhone();
    }

    public String getVerified_address() {
        return null;
    }

    public String getVerified_email() {
        return owner.getVerifiedEmail();
    }

    public String getVerified_name() {
        return owner.getVerifiedName();
    }

    public String getVerified_phone() {
        return owner.getVerifiedPhone();
    }
}
