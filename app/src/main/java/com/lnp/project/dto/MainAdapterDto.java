package com.lnp.project.dto;

public class MainAdapterDto {

    private Boolean isAdmin = false;
    private Boolean isRetailer = false;
    private Boolean isUser = false;

    private String debitWallet = null;

    public String getDebitWallet() {
        return debitWallet;
    }

    public void setDebitWallet(String debitWallet) {
        this.debitWallet = debitWallet;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public Boolean getRetailer() {
        return isRetailer;
    }

    public void setRetailer(Boolean retailer) {
        isRetailer = retailer;
    }

    public Boolean getUser() {
        return isUser;
    }

    public void setUser(Boolean user) {
        isUser = user;
    }
}
