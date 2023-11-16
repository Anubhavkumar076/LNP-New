package com.lnp.project.dto;
public class MyListData{
    private String formType;
    private String fullName;
    private String address;
    private String mobileNumber;
    private String loanCategory;
    private String caService;
    private String engineerBuilding;
    private String engineerService;
    private String tenure;
    private String loanType;
    private String loanAmount;
    private String savingAmount;

    private String addedBy;
    private String userAddedBy;
    private String addedDate;

    public MyListData(String formType, String fullName, String address, String mobileNumber,
                      String loanCategory, String caService, String engineerBuilding, String engineerService,
                      String tenure, String loanType, String loanAmount, String savingAmount, String addedBy,
                      String userAddedBy, String addedDate) {
        this.formType = formType;
        this.fullName = fullName;
        this.address = address;
        this.mobileNumber = mobileNumber;
        this.loanCategory = loanCategory;
        this.caService = caService;
        this.engineerBuilding = engineerBuilding;
        this.engineerService = engineerService;
        this.tenure = tenure;
        this.loanType = loanType;
        this.loanAmount = loanAmount;
        this.savingAmount = savingAmount;
        this.addedBy = addedBy;
        this.userAddedBy = userAddedBy;
        this.addedDate = addedDate;
    }

    public String getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(String addedDate) {
        this.addedDate = addedDate;
    }

    public String getUserAddedBy() {
        return userAddedBy;
    }

    public void setUserAddedBy(String userAddedBy) {
        this.userAddedBy = userAddedBy;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public String getFormType() {
        return formType;
    }

    public void setFormType(String formType) {
        this.formType = formType;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getLoanCategory() {
        return loanCategory;
    }

    public void setLoanCategory(String loanCategory) {
        this.loanCategory = loanCategory;
    }

    public String getCaService() {
        return caService;
    }

    public void setCaService(String caService) {
        this.caService = caService;
    }

    public String getEngineerBuilding() {
        return engineerBuilding;
    }

    public void setEngineerBuilding(String engineerBuilding) {
        this.engineerBuilding = engineerBuilding;
    }

    public String getEngineerService() {
        return engineerService;
    }

    public void setEngineerService(String engineerService) {
        this.engineerService = engineerService;
    }

    public String getTenure() {
        return tenure;
    }

    public void setTenure(String tenure) {
        this.tenure = tenure;
    }

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

    public String getSavingAmount() {
        return savingAmount;
    }

    public void setSavingAmount(String savingAmount) {
        this.savingAmount = savingAmount;
    }
}

