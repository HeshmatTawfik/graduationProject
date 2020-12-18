package com.heshmat.doctoreta.models.stripemodels;


public class Source {
    private String id;
    private com.stripe.android.model.Source sourceStripe;
    private Card card;
    private Long amount;
    private String client_secret;
    private Long created;
    private String currency;
    private Boolean isLiveMode;
    private com.stripe.android.model.Source.Owner ownerStripe;

    private String status;
    private String type;
    private String usage;
    private com.stripe.android.model.Card cardStripe;
    private Address address;
    private Owner owner;

    private String statement_descriptor;

    public String getId() {
        return this.sourceStripe.getId();
    }


    public Long getAmount() {
        return sourceStripe.getAmount();
    }


    public String getClient_secret() {
        return sourceStripe.getClientSecret();
    }


    public Long getCreated() {
        return sourceStripe.getCreated();
    }


    public String getCurrency() {
        return sourceStripe.getCurrency();
    }

    public String getFlow() {

        return sourceStripe.getFlow() == null ? null : sourceStripe.getFlow().name();

    }


    public Boolean getLivemode() {
        return sourceStripe.isLiveMode();
    }


    public String getStatus() {
        return sourceStripe.getStatus() == null ? null : sourceStripe.getStatus().name();
    }

    public String getStatement_descriptor() {
        return sourceStripe.getStatementDescriptor();
    }

    public String getType() {
        return sourceStripe.getType();
    }

    public String getUsage() {
        return sourceStripe.getUsage() == null ? null : sourceStripe.getUsage().name();
    }


    public Source(com.stripe.android.model.Source sourceStripe, com.stripe.android.model.Card card, com.stripe.android.model.Source.Owner ownerStripe) {
        this.sourceStripe = sourceStripe;
        this.card = new Card(card);
        this.ownerStripe = ownerStripe;
        this.owner = new Owner(ownerStripe, ownerStripe.getAddress());
    }

    public Card getCard() {
        return card;
    }

    public Owner getOwner() {
        return owner;
    }
}
