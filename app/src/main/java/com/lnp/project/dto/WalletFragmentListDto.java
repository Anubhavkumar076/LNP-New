package com.lnp.project.dto;

public class WalletFragmentListDto {

    private String transactionType;
    private String debitAmount;
    private String debitTotalAmount;
    private String creditAmount;
    private String creditTotalAmount;

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getDebitAmount() {
        return debitAmount;
    }

    public void setDebitAmount(String debitAmount) {
        this.debitAmount = debitAmount;
    }

    public String getDebitTotalAmount() {
        return debitTotalAmount;
    }

    public void setDebitTotalAmount(String debitTotalAmount) {
        this.debitTotalAmount = debitTotalAmount;
    }

    public String getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(String creditAmount) {
        this.creditAmount = creditAmount;
    }

    public String getCreditTotalAmount() {
        return creditTotalAmount;
    }

    public void setCreditTotalAmount(String creditTotalAmount) {
        this.creditTotalAmount = creditTotalAmount;
    }
}
