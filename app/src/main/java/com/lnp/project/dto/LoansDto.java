package com.lnp.project.dto;

public class LoansDto {
    private String loanType;
    private String loanSubType;
    private String loanAmount;

    public String getLoanType() {
        return loanType;
    }

    public void setLoanType(String loanType) {
        this.loanType = loanType;
    }

    public String getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(String loanAmount) {
        this.loanAmount = loanAmount;
    }

    public String getLoanSubType() {
        return loanSubType;
    }

    public void setLoanSubType(String loanSubType) {
        this.loanSubType = loanSubType;
    }
}
